package mx.gob.cnbv.examples.apiexample.utils;

import org.apache.commons.codec.binary.Base64;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

/**
 * Contiene las llamadas principales para interactuar con el token.
 *
 * @author -
 */
public class AccessToken {

    RestTemplateUtil restTemplateUtil;
    private final String GRANT_TYPE_NAME = "grant_type";
    private final String USERNAME = "username";
    private final String PASSWORD = "password";
    private final String REFRESH_TOKEN = "refresh_token";
    private final String AUTHORIZATION = "Authorization";
    private final String BASIC = "Basic ";
    private final String TOKEN = "token";
    private final String TOKEN_TYPE_HINT = "token_type_hint";
    private final String ACCESS_TOKEN = "access_token";
    private final String TOKEN_URL = "https://sitiapiqa.cnbv.gob.mx:8243/token";
    private final String REFRESH_TOKEN_URL = "https://sitiapiqa.cnbv.gob.mx:8243/token";
    private final String REVOKE_TOKEN_URL = "https://sitiapiqa.cnbv.gob.mx:8243/revoke";
    private String clientId;
    private String clientSecret;
    private String username;
    private String password;
    private String encodedCredentials;
    private HttpHeaders headers;
    private RestTemplate restTemplate;
    private MultiValueMap<String, String> body;

    /**
     * *
     * Crea las cabeceras que contiene el 'clientId' y 'clientSecret' en base 64
     * y coloca el 'ContentType' como APPLICATION_FROM_URLENCODED.
     *
     * @param clientId clientid.
     * @param clientSecret clientsecreet
     * @param username nombre de usuario.
     * @param password contraseña.
     */
    public AccessToken(
            String clientId,
            String clientSecret,
            String username,
            String password,
            boolean sslVerify) {
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.username = username;
        this.password = password;
        this.body = new LinkedMultiValueMap<String, String>();
        encodeCredentials();
        createHeaders();
        createRestTemplate(sslVerify);
    }

    private void encodeCredentials() {
        String credentials = clientId + ":" + clientSecret;
        this.encodedCredentials = new String(Base64.encodeBase64(credentials.getBytes()));
    }

    private void createHeaders() {
        this.headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.add(AUTHORIZATION, BASIC + encodedCredentials);
    }

    private void createRestTemplate(boolean sslVerify) {
        restTemplateUtil = new RestTemplateUtil();
        this.restTemplate = restTemplateUtil.getRestTemplate(sslVerify);
    }

    /**
     * *
     * Realiza la petición para generar un nuevo token. Es requerido el uso de
     * usuario, contraseña y las credenciales en base 64.
     *
     * @return RestTemplate
     */
    public String getAccessToken() {
        body.clear();
        body.add(GRANT_TYPE_NAME, PASSWORD);
        body.add(USERNAME, username);
        body.add(PASSWORD, password);
        ResponseEntity<String> response
                = restTemplate.postForEntity(
                        TOKEN_URL,
                        new HttpEntity<MultiValueMap<String, String>>(body, headers),
                        String.class);
        return response.getBody();
    }

    /**
     * *
     * Realiza la petición para actualizar el token. Es requerido el uso de
     * 'refresh_token' y las credenciales en base 64.
     *
     * @param refreshToken
     * @return
     */
    public String getRefreshToken(String refreshToken) {
        body.clear();
        body.add(GRANT_TYPE_NAME, REFRESH_TOKEN);
        body.add(REFRESH_TOKEN, refreshToken);
        ResponseEntity<String> response
                = restTemplate.postForEntity(
                        REFRESH_TOKEN_URL,
                        new HttpEntity<MultiValueMap<String, String>>(body, headers),
                        String.class);
        return response.getBody();
    }

    /**
     * *
     * Realiza la petición para hacer inválido el token. Es requerido el token y
     * las credenciales en base 64.
     *
     * @param token
     * @return
     */
    public String revokeToken(String token) {
        body.clear();
        body.add(TOKEN, token);
        body.add(TOKEN_TYPE_HINT, ACCESS_TOKEN);
        ResponseEntity<String> response
                = restTemplate.postForEntity(
                        REVOKE_TOKEN_URL,
                        new HttpEntity<MultiValueMap<String, String>>(body, headers),
                        String.class);
        return response.getBody();
    }

}
