package mx.gob.cnbv.examples.apiexample;

import java.util.logging.Logger;
import mx.gob.cnbv.examples.apiexample.utils.AccessToken;
import org.json.JSONObject;

/**
 * Pryecto prueba para realizar peticiones de token.
 *
 * @author -
 */
public class Main {

    private static final Logger LOGGER = Logger.getLogger(Main.class.getName());

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // Ejemplo de accessToken
        String clientId = "<clientId>";
        String clientSecret = "<clientSecret>";
        String username = "<username>";
        String password = "<password>";

        AccessToken accessToken = new AccessToken(
                clientId,
                clientSecret,
                username,
                password,
                false);
        //Solicita un nuevo token.
        String token = accessToken.getAccessToken();
        LOGGER.info("token: " + token);
        JSONObject json = new JSONObject(token);
        //Realiza una actualización del token usando el 'refresh_token' de la petición anterior.
        String newToken = accessToken.getRefreshToken(json.getString("refresh_token"));
        LOGGER.info("newToken: " + newToken);
        JSONObject newJson = new JSONObject(newToken);
        //Invalida el token.
        accessToken.revokeToken(newJson.getString("access_token"));

    }

}
