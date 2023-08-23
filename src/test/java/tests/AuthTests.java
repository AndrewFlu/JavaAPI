package tests;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import lib.Assertions;
import lib.BaseTestCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.HashMap;
import java.util.Map;

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

        Assertions.assertJsonByName(authEndpointResponse, "user_id", this.loginUserId);
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
        Response authResponse = spec.get().andReturn();

        Assertions.assertJsonByName(authResponse, "user_id", 0);
    }
}
