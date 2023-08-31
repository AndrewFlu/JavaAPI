package tests;

import io.qameta.allure.Description;
import io.restassured.response.Response;
import lib.Assertions;
import lib.BaseTestCase;
import lib.CoreRequests;
import lib.DataGenerator;
import org.junit.jupiter.api.Test;
import utils.StringGenerator;

import java.util.HashMap;
import java.util.Map;

public class UserEditTests extends BaseTestCase {
    private static final String ENDPOINT_USER = "https://playground.learnqa.ru/api/user/";
    private static final String ENDPOINT_LOGIN = "https://playground.learnqa.ru/api/user/login";

    CoreRequests coreRequests = new CoreRequests();

    @Test
    void editCreatedUser() {
        // Register user
        Map<String, String> registrationData = DataGenerator.getRegistrationData();
        Response registerResponse = coreRequests.makePostRequest(ENDPOINT_USER, registrationData);
        String userId = registerResponse.jsonPath().getString("id");

        // Log In by registered user
        Map<String, String> authData = new HashMap<>();
        authData.put("email", registrationData.get("email"));
        authData.put("password", registrationData.get("password"));
        Response loginResponse = coreRequests.makePostRequest(ENDPOINT_LOGIN, authData);

        // Edit user
        String newName = "EditedUserName";
        Map<String, String> editData = new HashMap<>();
        editData.put("username", newName);

        String token = this.getHeader(loginResponse, "x-csrf-token");
        String cookie = this.getCookie(loginResponse, "auth_sid");

        Response editResponse = coreRequests.makePutRequest(ENDPOINT_USER + userId, token, cookie, editData);

        Assertions.assertResponseCodeEquals(editResponse, 200);

        // Get user data
        Response getUserResponse = coreRequests.makeGetRequest(ENDPOINT_USER + userId, token, cookie);

        Assertions.assertJsonByName(getUserResponse, "username", newName);
    }

    @Test
    @Description("Cannot edit user without authentication")
    void cannotEditUserWithoutAuthentication() {
        // register user
        Map<String, String> registrationData = DataGenerator.getRegistrationData();
        Response registerResponse = coreRequests.makePostRequest(ENDPOINT_USER, registrationData);
        String userId = registerResponse.jsonPath().getString("id");

        // edit user
        String newName = "EditedUserName";
        Map<String, String> editData = new HashMap<>();
        editData.put("username", newName);

        Response editResponse = coreRequests.makePutRequest(ENDPOINT_USER + userId, null, null, editData);

        Assertions.assertResponseCodeEquals(editResponse, 400);
        Assertions.assertResponseTextEquals(editResponse, "Auth token not supplied");
    }

    @Test
    @Description("User cannot edit another user")
    void cannotEditUserWithAuthenticationByAnotherUser() {
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

        // Edit user_2 by user_1
        String newName = "EditedUserName";
        Map<String, String> editData = new HashMap<>();
        editData.put("username", newName);
        Response user_2_editResponse = coreRequests.makePutRequest(ENDPOINT_USER + user_2_id, firstUserToken, firstUserCookie, editData);

        Assertions.assertResponseCodeEquals(user_2_editResponse, 200);

        // check that data didn't change
        // Log In by user_2
        Map<String, String> user_2_authData = new HashMap<>();
        user_2_authData.put("email", user_2_registrationData.get("email"));
        user_2_authData.put("password", user_2_registrationData.get("password"));
        Response user_2_loginResponse = coreRequests.makePostRequest(ENDPOINT_LOGIN, user_2_authData);
        String user_2_token = this.getHeader(user_2_loginResponse, "x-csrf-token");
        String user_2_cookie = this.getCookie(user_2_loginResponse, "auth_sid");

        // Get user_2 data
        Response user_2_infoResponse = coreRequests.makeGetRequest(ENDPOINT_USER + user_2_id, user_2_token, user_2_cookie);

        Assertions.assertJsonByName(user_2_infoResponse, "username", user_2_registrationData.get("username"));
    }

    @Test
    @Description("User cannot change email to invalid data")
    void cannotSetInvalidEmail() {
        // Register user
        Map<String, String> registrationData = DataGenerator.getRegistrationData();
        Response registerResponse = coreRequests.makePostRequest(ENDPOINT_USER, registrationData);
        String userId = registerResponse.jsonPath().getString("id");

        // Log In by registered user
        Map<String, String> authData = new HashMap<>();
        authData.put("email", registrationData.get("email"));
        authData.put("password", registrationData.get("password"));
        Response loginResponse = coreRequests.makePostRequest(ENDPOINT_LOGIN, authData);
        String token = this.getHeader(loginResponse, "x-csrf-token");
        String cookie = this.getCookie(loginResponse, "auth_sid");

        // Edit user
        String invalidEmail = "invalidemailexample.com";
        Map<String, String> editData = new HashMap<>();
        editData.put("email", invalidEmail);
        Response editResponse = coreRequests.makePutRequest(ENDPOINT_USER + userId, token, cookie, editData);

        Assertions.assertResponseCodeEquals(editResponse, 400);
        Assertions.assertResponseTextEquals(editResponse, "Invalid email format");
    }

    @Test
    @Description("User cannot change firstName to short value")
    void cannotSetShortFirstName() {
        // Register user
        Map<String, String> registrationData = DataGenerator.getRegistrationData();
        Response registerResponse = coreRequests.makePostRequest(ENDPOINT_USER, registrationData);
        String userId = registerResponse.jsonPath().getString("id");

        // Log In by registered user
        Map<String, String> authData = new HashMap<>();
        authData.put("email", registrationData.get("email"));
        authData.put("password", registrationData.get("password"));
        Response loginResponse = coreRequests.makePostRequest(ENDPOINT_LOGIN, authData);
        String token = this.getHeader(loginResponse, "x-csrf-token");
        String cookie = this.getCookie(loginResponse, "auth_sid");

        // Edit user
        String shortFirstName = StringGenerator.generateRandomString(1);
        Map<String, String> editData = new HashMap<>();
        editData.put("firstName", shortFirstName);
        Response editResponse = coreRequests.makePutRequest(ENDPOINT_USER + userId, token, cookie, editData);

        Assertions.assertResponseCodeEquals(editResponse, 400);
        Assertions.assertJsonByName(editResponse, "error", "Too short value for field firstName");
    }
}
