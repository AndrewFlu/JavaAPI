package tests;

import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.restassured.response.Response;
import lib.Assertions;
import lib.BaseTestCase;
import lib.CoreRequests;
import lib.DataGenerator;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

@Epic("Delete user tests")
public class UserDeleteTests extends BaseTestCase {
    private static final String ENDPOINT_LOGIN = "https://playground.learnqa.ru/api/user/login";
    private static final String ENDPOINT_USER = "https://playground.learnqa.ru/api/user/";
    private final CoreRequests coreRequests = new CoreRequests();

    @Test
    @Description("Cannot delete user defended for testing")
    void cannotDeleteImportantUser() {
        String userId = "2";
        Map<String, String> authData = new HashMap<>();
        authData.put("email", "vinkotov@example.com");
        authData.put("password", "1234");

        Response authResponse = coreRequests.makePostRequest(ENDPOINT_LOGIN, authData);
        String token = authResponse.getHeader("x-csrf-token");
        String cookie = authResponse.getCookie("auth_sid");

        Response response = coreRequests.makeDeleteRequest(ENDPOINT_USER + userId, token, cookie);

        Assertions.assertResponseCodeEquals(response, 400);
        Assertions.assertResponseTextEquals(response, "Please, do not delete test users with ID 1, 2, 3, 4 or 5.");
    }

    @Test
    @Description("User can be deleted by same user")
    void canDeleteUser() {
        // register user
        Map<String, String> registrationData = DataGenerator.getRegistrationData();
        Response registerResponse = coreRequests.makePostRequest(ENDPOINT_USER, registrationData);
        String userId = registerResponse.jsonPath().getString("id");

        // login user
        Map<String, String> authData = new HashMap<>();
        authData.put("email", registrationData.get("email"));
        authData.put("password", registrationData.get("password"));
        Response loginResponse = coreRequests.makePostRequest(ENDPOINT_LOGIN, authData);
        String token = this.getHeader(loginResponse, "x-csrf-token");
        String cookie = this.getCookie(loginResponse, "auth_sid");

        // delete user
        Response response = coreRequests.makeDeleteRequest(ENDPOINT_USER + userId, token, cookie);
        Assertions.assertResponseCodeEquals(response, 200);

        // check user doesn't exist
        Response userInfoResponse1 = coreRequests.makeGetRequest(ENDPOINT_USER + userId, token, cookie);
        Assertions.assertResponseCodeEquals(userInfoResponse1, 404);
    }

    @Test
    @Description("User cannot be deleted by another user")
    void cannotDeleteByAnotherUser() {
        // generate user_1
        Map<String, String> user_1_registrationData = DataGenerator.getRegistrationData();
        // generate user_2
        Map<String, String> user_2_registrationData = DataGenerator.getRegistrationData();

        // register user_1
        Response user_1_registerResponse = coreRequests.makePostRequest(ENDPOINT_USER, user_1_registrationData);
        Assertions.assertJsonContainsField(user_1_registerResponse, "id");
        // register user_2
        Response user_2_registerResponse = coreRequests.makePostRequest(ENDPOINT_USER, user_2_registrationData);
        String user_2_id = user_2_registerResponse.jsonPath().getString("id");

        // Log In by user_1
        Map<String, String> user_1_authData = new HashMap<>();
        user_1_authData.put("email", user_1_registrationData.get("email"));
        user_1_authData.put("password", user_1_registrationData.get("password"));

        Response user_1_loginResponse = coreRequests.makePostRequest(ENDPOINT_LOGIN, user_1_authData);
        String firstUserToken = this.getHeader(user_1_loginResponse, "x-csrf-token");
        String firstUserCookie = this.getCookie(user_1_loginResponse, "auth_sid");

        // Delete user_2 by user_1
        Response deleteResponse = coreRequests.makeDeleteRequest(ENDPOINT_USER + user_2_id, firstUserToken, firstUserCookie);
        Assertions.assertResponseCodeEquals(deleteResponse, 200);

        // check that data didn't delete
        // Log In by user_2
        Map<String, String> user_2_authData = new HashMap<>();
        user_2_authData.put("email", user_2_registrationData.get("email"));
        user_2_authData.put("password", user_2_registrationData.get("password"));
        Response user_2_loginResponse = coreRequests.makePostRequest(ENDPOINT_LOGIN, user_2_authData);
        String user_2_token = this.getHeader(user_2_loginResponse, "x-csrf-token");
        String user_2_cookie = this.getCookie(user_2_loginResponse, "auth_sid");

        // Get user_2 data
        Response user_2_infoResponse = coreRequests.makeGetRequest(ENDPOINT_USER + user_2_id, user_2_token, user_2_cookie);
        Assertions.assertResponseCodeEquals(user_2_infoResponse, 200);
        Assertions.assertJsonByName(user_2_infoResponse, "username", user_2_registrationData.get("username"));
    }
}
