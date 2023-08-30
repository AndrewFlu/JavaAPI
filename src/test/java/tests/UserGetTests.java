package tests;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import lib.Assertions;
import lib.BaseTestCase;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

public class UserGetTests extends BaseTestCase {

    private static final String ENDPOINT_GET_USER = "https://playground.learnqa.ru/api/user/";
    private static final String ENDPOINT_USER_LOGIN = "https://playground.learnqa.ru/api/user/login";

    @Test
    void getUserInfoWithoutAuthorization() {
        String userId = "2";
        Response response = RestAssured.get(ENDPOINT_GET_USER.concat(userId)).andReturn();

        Assertions.assertJsonContainsField(response, "username");
        Assertions.assertJsonDoesNotContainField(response, "firstName");
        Assertions.assertJsonDoesNotContainField(response, "lastName");
        Assertions.assertJsonDoesNotContainField(response, "email");
    }

    @Test
    void getUserInfoWithAuthorizationWithSameUser() {
        Map<String, String> authData = new HashMap<>();
        authData.put("email", "vinkotov@example.com");
        authData.put("password", "1234");
        Response authResponse = RestAssured
                .given()
                .body(authData)
                .when()
                .post(ENDPOINT_USER_LOGIN)
                .andReturn();
        String header = authResponse.getHeader("x-csrf-token");
        String cookie = authResponse.getCookie("auth_sid");

        String userId = "2";
        Response userInfoResponse = RestAssured
                .given()
                .header("x-csrf-token", header)
                .cookie("auth_sid", cookie)
                .when()
                .get(ENDPOINT_GET_USER.concat(userId))
                .andReturn();

        String[] expectedFields = {"username", "firstName", "lastName", "email"};
        Assertions.assertJsonContainFields(userInfoResponse, expectedFields);
    }
}
