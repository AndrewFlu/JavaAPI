package tests;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import lib.Assertions;
import lib.BaseTestCase;
import lib.DataGenerator;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

public class UserEditTests extends BaseTestCase {
    private static final String ENDPOINT_USER = "https://playground.learnqa.ru/api/user/";
    private static final String ENDPOINT_LOGIN = "https://playground.learnqa.ru/api/user/login";

    @Test
    void editCreatedUser() {
        // Register user
        Map<String, String> registrationData = DataGenerator.getRegistrationData();
        Response registerResponse = RestAssured.given().body(registrationData).post(ENDPOINT_USER).andReturn();
        String userId = registerResponse.jsonPath().getString("id");

        // Log In by registered user
        Map<String, String> authData = new HashMap<>();
        authData.put("email", registrationData.get("email"));
        authData.put("password", registrationData.get("password"));
        Response loginResponse = RestAssured.given().body(authData).post(ENDPOINT_LOGIN).andReturn();

        // Edit user
        String newName = "EditedUserName";
        Map<String, String> editData = new HashMap<>();
        editData.put("username", newName);

        Response editResponse = RestAssured
                .given()
                .header("x-csrf-token", this.getHeader(loginResponse, "x-csrf-token"))
                .cookie("auth_sid", this.getCookie(loginResponse, "auth_sid"))
                .body(editData)
                .put(ENDPOINT_USER + userId)
                .andReturn();
        Assertions.assertResponseCodeEquals(editResponse, 200);

        // Get user data
        Response getUserResponse = RestAssured
                .given()
                .header("x-csrf-token", this.getHeader(loginResponse, "x-csrf-token"))
                .cookie("auth_sid", this.getCookie(loginResponse, "auth_sid"))
                .get(ENDPOINT_USER + userId)
                .andReturn();

        Assertions.assertJsonByName(getUserResponse, "username", newName);
    }
}
