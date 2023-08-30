package tests;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import lib.Assertions;
import lib.BaseTestCase;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

public class UserRegisterTests extends BaseTestCase {

    private static final String ENDPOINT_CREATE_USER = "https://playground.learnqa.ru/api/user/";

    @Test
    void cannotRegisterWithExistingEmail() {
        String email = "vinkotov@example.com";
        String password = "123";

        Map<String, String> userData = new HashMap<>();
        userData.put("email", email);
        userData.put("password", password);
        userData.put("username", "learnQA_username");
        userData.put("firstName", "learnQA_firstname");
        userData.put("lastName", "learnQA_lastname");
        Response response = RestAssured
                .given()
                .body(userData)
                .when()
                .post(ENDPOINT_CREATE_USER)
                .andReturn();

        Assertions.assertResponseCodeEquals(response, 400);
        Assertions.assertResponseTextEquals(response, String.format("Users with email '%s' already exists", email));
    }
}
