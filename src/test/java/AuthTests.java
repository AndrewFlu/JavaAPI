import io.restassured.RestAssured;
import io.restassured.http.Headers;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class AuthTests {
    private static final String ENDPOINT_LOGIN = "https://playground.learnqa.ru/api/user/login";
    private static final String ENDPOINT_AUTH = "https://playground.learnqa.ru/api/user/auth";

    @Test
    void auth() {
        final String login = "vinkotov@example.com";
        final String password = "1234";

        Map<String, String> authData = new HashMap<>();
        authData.put("password", password);
        authData.put("email", login);

        Response loginEndpointResponse = RestAssured
                .given()
                .body(authData)
                .when()
                .post(ENDPOINT_LOGIN)
                .andReturn();

        Headers headers = loginEndpointResponse.headers();
        Map<String, String> cookies = loginEndpointResponse.cookies();
        int loginUserId = loginEndpointResponse.jsonPath().getInt("user_id");

        assertEquals(200, loginEndpointResponse.statusCode(), "Unexpected status code");
        assertTrue(cookies.containsKey("auth_sid"), "Response doesn't contains 'auth_sid' cookie");
        assertTrue(headers.hasHeaderWithName("x-csrf-token"), "Response doesn't contains 'x-csrf-token' header");
        assertTrue(loginUserId > 0, "'user_id' in 'login endpoint'  should be greater then 0");

        Response authEndpointResponse = RestAssured
                .given()
                .header("x-csrf-token", loginEndpointResponse.getHeader("x-csrf-token"))
                .cookie("auth_sid", loginEndpointResponse.cookies().get("auth_sid"))
                .when()
                .get(ENDPOINT_AUTH)
                .andReturn();

        int authUserId = authEndpointResponse.jsonPath().getInt("user_id");

        assertTrue(authUserId > 0, "'user_id' in 'auth endpoint'  should be greater then 0");
        assertEquals(loginUserId, authUserId, "Cannot authorised in auth endpoint");
    }
}
