package tests;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import lib.Assertions;
import lib.BaseTestCase;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.HashMap;
import java.util.Map;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Description;

@Epic("Authorization cases")
@Feature("Authorisation")
public class UserAuthTests extends BaseTestCase {
    private static final String ENDPOINT_LOGIN = "https://playground.learnqa.ru/api/user/login";
    private static final String ENDPOINT_AUTH = "https://playground.learnqa.ru/api/user/auth";

    private final String login = "vinkotov@example.com";
    private final String password = "1234";

    private String cookie;
    private String header;
    private int loginUserId;

    @BeforeEach
    @Description("Log in with email and password")
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
    @Description("Check that user can be successfully authenticated by email and password")
    @DisplayName("Test positive auth user")
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

    @Description("Check that user cannot be authenticated without auth-cookie or token")
    @DisplayName("Test negative auth user")
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
