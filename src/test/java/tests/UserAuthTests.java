package tests;

import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import lib.Assertions;
import lib.BaseTestCase;
import lib.CoreRequests;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.HashMap;
import java.util.Map;

@Epic("Authorization cases")
@Feature("Authorisation")
public class UserAuthTests extends BaseTestCase {
    private final CoreRequests coreRequests = new CoreRequests();

    private static final String ENDPOINT_LOGIN = "https://playground.learnqa.ru/api/user/login";
    private static final String ENDPOINT_AUTH = "https://playground.learnqa.ru/api/user/auth";

    private String cookie;
    private String header;
    private int loginUserId;

    @BeforeEach
    @Description("Log in with email and password")
    public void login() {

        Map<String, String> authData = new HashMap<>();
        String login = "vinkotov@example.com";
        authData.put("email", login);
        String password = "1234";
        authData.put("password", password);

        Response loginEndpointResponse = coreRequests.makePostRequest(ENDPOINT_LOGIN, authData);

        this.cookie = this.getCookie(loginEndpointResponse, "auth_sid");
        this.header = this.getHeader(loginEndpointResponse, "x-csrf-token");
        this.loginUserId = this.getIntFromJson(loginEndpointResponse, "user_id");
    }


    @Test
    @Description("Check that user can be successfully authenticated by email and password")
    @DisplayName("Test positive auth user")
    void auth() {
        Response authEndpointResponse = coreRequests.makeGetRequest(ENDPOINT_AUTH, this.header, this.cookie);

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
            Response responseToCheck = coreRequests.makeGetRequestWithCookie(ENDPOINT_AUTH, this.cookie);
            Assertions.assertJsonByName(responseToCheck, "user_id", 0);
        } else if (condition.equals("headers")) {
            Response responseToCheck = coreRequests.makeGetRequestWithToken(ENDPOINT_AUTH, this.header);
            Assertions.assertJsonByName(responseToCheck, "user_id", 0);
        } else {
            throw new IllegalArgumentException("Unrecognized test parameter: " + condition);
        }
    }
}
