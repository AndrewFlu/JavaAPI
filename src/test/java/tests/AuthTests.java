package tests;

import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import lib.BaseTestCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class AuthTests extends BaseTestCase {
    private static final String ENDPOINT_LOGIN = "https://playground.learnqa.ru/api/user/login";
    private static final String ENDPOINT_AUTH = "https://playground.learnqa.ru/api/user/auth";

    private final String login = "vinkotov@example.com";
    private final String password = "1234";

    private String cookie;
    private String header;
    private int loginUserId;

    @BeforeEach
    public void login() {

        Map<String, String> authData = new HashMap<>();
        authData.put("email", login);
        authData.put("password", password);

        Response loginEndpointResponse = RestAssured
                .given()
                .body(authData)
                .when()
                .post(ENDPOINT_LOGIN)
                .andReturn();
        this.cookie = this.getCookie(loginEndpointResponse, "auth_sid");
        this.header = this.getHeader(loginEndpointResponse, "x-csrf-token");
        this.loginUserId = this.getIntFromJson(loginEndpointResponse, "user_id");
    }


    @Test
    void auth() {
        Response authEndpointResponse = RestAssured
                .given()
                .header("x-csrf-token", this.header)
                .cookie("auth_sid", this.cookie)
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
        RequestSpecification spec = RestAssured.given();
        spec.baseUri(ENDPOINT_AUTH);
        if (condition.equals("cookie")) {
            spec.cookie("auth_sid", this.cookie);
        } else if (condition.equals("headers")) {
            spec.header("x-csrf-token", this.header);
        } else {
            throw new IllegalArgumentException("Unrecognized test parameter: " + condition);
        }
        JsonPath authResponse = spec.get().jsonPath();
        int userId = authResponse.get("user_id");

        assertEquals(0, userId, "'user_id' should be 0 for unauthorised request to auth endpoint");
    }
}
