package mx.gob.cnbv.examples.apiexample.utils;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.net.ssl.SSLContext;
import mx.gob.cnbv.examples.apiexample.Main;
import org.apache.http.client.HttpClient;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.BasicHttpClientConnectionManager;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.ssl.SSLContexts;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.util.ResourceUtils;
import org.springframework.web.client.RestTemplate;

/**
 * Crea el RestTemplate con la configuración de seguridad.
 *
 * @author -
 */
public class RestTemplateUtil {

    private static final Logger LOGGER = Logger.getLogger(Main.class.getName());
    private final String JKS_PASSWORD = "keystorepass";
    private final String JKS_STORE = "classpath:<setKeyStore>.jks";

    public RestTemplate getRestTemplate(boolean sslVerification) {
        if (sslVerification) {
            return getSSLRestTemplate();
        }
        return getAcceptAllRestTemplate();
    }

    /**
     * *
     * Regresa el objeto RestTemplate sin realizar ninguna validación del
     * certificado.
     *
     * @return
     */
    private RestTemplate getAcceptAllRestTemplate() {
        try {
            TrustStrategy acceptingTrustStrategy = (cert, authType) -> true;
            SSLContext sslContext = SSLContexts.custom()
                    .loadTrustMaterial(null, acceptingTrustStrategy).build();
            SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslContext,
                    NoopHostnameVerifier.INSTANCE);
            Registry<ConnectionSocketFactory> socketFactoryRegistry
                    = RegistryBuilder.<ConnectionSocketFactory>create()
                            .register("https", sslsf)
                            .register("http", new PlainConnectionSocketFactory())
                            .build();
            BasicHttpClientConnectionManager connectionManager
                    = new BasicHttpClientConnectionManager(socketFactoryRegistry);
            CloseableHttpClient httpClient = HttpClients.custom().setSSLSocketFactory(sslsf)
                    .setConnectionManager(connectionManager).build();
            HttpComponentsClientHttpRequestFactory requestFactory
                    = new HttpComponentsClientHttpRequestFactory(httpClient);
            return new RestTemplate(requestFactory);
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(AccessToken.class.getName()).log(Level.SEVERE, null, ex);
        } catch (KeyStoreException ex) {
            Logger.getLogger(AccessToken.class.getName()).log(Level.SEVERE, null, ex);
        } catch (KeyManagementException ex) {
            Logger.getLogger(AccessToken.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    /**
     * *
     * Regresa el objeto RestTemplate realizando validación del certiticado.
     *
     * @return
     */
    private RestTemplate getSSLRestTemplate() {
        try {
            SSLContext sslContext = SSLContextBuilder
                    .create()
                    .loadTrustMaterial(
                            ResourceUtils.getFile(JKS_STORE),
                            JKS_PASSWORD.toCharArray())
                    .build();
            HttpClient client = HttpClients.custom()
                    .setSSLContext(sslContext)
                    .build();
            HttpComponentsClientHttpRequestFactory requestFactory
                    = new HttpComponentsClientHttpRequestFactory();
            requestFactory.setHttpClient(client);
            return new RestTemplate(requestFactory);
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(AccessToken.class.getName()).log(Level.SEVERE, null, ex);
        } catch (KeyStoreException ex) {
            Logger.getLogger(AccessToken.class.getName()).log(Level.SEVERE, null, ex);
        } catch (CertificateException ex) {
            Logger.getLogger(AccessToken.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(AccessToken.class.getName()).log(Level.SEVERE, null, ex);
        } catch (KeyManagementException ex) {
            Logger.getLogger(AccessToken.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
}
