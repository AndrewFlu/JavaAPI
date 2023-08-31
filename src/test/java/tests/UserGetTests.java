package tests;

import io.qameta.allure.Description;
import io.restassured.response.Response;
import lib.Assertions;
import lib.BaseTestCase;
import lib.CoreRequests;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

public class UserGetTests extends BaseTestCase {

    private static final String ENDPOINT_GET_USER = "https://playground.learnqa.ru/api/user/";
    private static final String ENDPOINT_USER_LOGIN = "https://playground.learnqa.ru/api/user/login";

    CoreRequests coreRequests = new CoreRequests();

    @Test
    @Description("Cannot get full user info without authorisation except username")
    void getUserInfoWithoutAuthorization() {
        String userId = "2";
        Response response = coreRequests.makeGetRequest(ENDPOINT_GET_USER.concat(userId));

        Assertions.assertJsonContainsField(response, "username");
        Assertions.assertJsonDoesNotContainField(response, "firstName");
        Assertions.assertJsonDoesNotContainField(response, "lastName");
        Assertions.assertJsonDoesNotContainField(response, "email");
    }

    @Test
    @Description("Successfully get full user info with authorisation by same user")
    void getUserInfoWithAuthorizationWithSameUser() {
        Map<String, String> authData = new HashMap<>();
        authData.put("email", "vinkotov@example.com");
        authData.put("password", "1234");
        Response authResponse = coreRequests.makePostRequest(ENDPOINT_USER_LOGIN, authData);

        String token = authResponse.getHeader("x-csrf-token");
        String cookie = authResponse.getCookie("auth_sid");

        String userId = "2";
        Response userInfoResponse = coreRequests.makeGetRequest(ENDPOINT_GET_USER.concat(userId), token, cookie);

        String[] expectedFields = {"username", "firstName", "lastName", "email"};
        Assertions.assertJsonContainFields(userInfoResponse, expectedFields);
    }

    @Test
    @Description("Cannot get full user info with authorisation by another user except username")
    void getUserInfoWithAuthorizationByAnotherUser() {
        Map<String, String> authData = new HashMap<>();
        authData.put("email", "vinkotov@example.com");
        authData.put("password", "1234");
        Response authResponse = coreRequests.makePostRequest(ENDPOINT_USER_LOGIN, authData);

        String token = authResponse.getHeader("x-csrf-token");
        String cookie = authResponse.getCookie("auth_sid");

        String anotherUserId = "1";
        Response userInfoResponse = coreRequests.makeGetRequest(ENDPOINT_GET_USER.concat(anotherUserId), token, cookie);

        Assertions.assertResponseCodeEquals(userInfoResponse, 200);
        Assertions.assertJsonContainsField(userInfoResponse, "username");
        Assertions.assertJsonDoesNotContainField(userInfoResponse, "firstName");
        Assertions.assertJsonDoesNotContainField(userInfoResponse, "lastName");
        Assertions.assertJsonDoesNotContainField(userInfoResponse, "email");
    }
}
