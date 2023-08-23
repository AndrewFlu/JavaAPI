import io.restassured.RestAssured;
import io.restassured.http.Headers;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class AuthTests {
    private static final String ENDPOINT_LOGIN = "https://playground.learnqa.ru/api/user/login";
    private static final String ENDPOINT_AUTH = "https://playground.learnqa.ru/api/user/auth";

    private final String login = "vinkotov@example.com";
    private final String password = "1234";

    @Test
    void auth() {

        Map<String, String> authData = new HashMap<>();
        authData.put("email", login);
        authData.put("password", password);

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

    @ParameterizedTest
    @ValueSource(strings = {"cookie", "headers"})
    void negativeAuth(String condition) {
        Map<String, String> authData = new HashMap<>();
        authData.put("email", login);
        authData.put("password", password);

        Response loginResponse = RestAssured
                .given()
                .body(authData)
                .when()
                .post(ENDPOINT_LOGIN)
                .andReturn();

        Map<String, String> cookies = loginResponse.getCookies();
        Headers headers = loginResponse.getHeaders();

        RequestSpecification spec = RestAssured.given();
        spec.baseUri(ENDPOINT_AUTH);
        if (condition.equals("cookie")) {
            spec.cookie("auth_sid", cookies.get("auth_sid"));
        } else if (condition.equals("headers")) {
            spec.header("x-csrf-token", headers.get("x-csrf-token"));
        } else {
            throw new IllegalArgumentException("Unrecognized test parameter: " + condition);
        }
        JsonPath authResponse = spec.get().jsonPath();
        int userId = authResponse.get("user_id");

        assertEquals(0, userId, "'user_id' should be 0 for unauthorised request to auth endpoint");
    }
}
