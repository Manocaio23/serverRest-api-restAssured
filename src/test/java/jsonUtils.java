import org.json.JSONObject;
public class jsonUtils {
    public static JSONObject getLoginRequestBody() {
        JSONObject requestBody = new JSONObject();
        requestBody.put("email", "teste2@outlook.com");
        requestBody.put("password", "teste");
        return requestBody;
    }
}
