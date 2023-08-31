package tests;

import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.restassured.response.Response;
import lib.Assertions;
import lib.BaseTestCase;
import lib.CoreRequests;
import lib.DataGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import utils.StringGenerator;

import java.util.HashMap;
import java.util.Map;

@Epic("Registration tests")
public class UserRegisterTests extends BaseTestCase {

    private static final String ENDPOINT_CREATE_USER = "https://playground.learnqa.ru/api/user/";
    private final CoreRequests coreRequests = new CoreRequests();

    @Test
    @Description("User cannot register with existing email")
    void cannotRegisterWithExistingEmail() {
        String email = "vinkotov@example.com";
        String password = "123";

        Map<String, String> userData = new HashMap<>();
        userData.put("email", email);
        userData.put("password", password);
        Map<String, String> registrationData = DataGenerator.getRegistrationData(userData);
        Response response = coreRequests.makePostRequest(ENDPOINT_CREATE_USER, registrationData);

        Assertions.assertResponseCodeEquals(response, 400);
        Assertions.assertResponseTextEquals(response, String.format("Users with email '%s' already exists", email));
    }

    @Test
    @Description("User can be successfully registered")
    void registerUserSuccessfully() {
        Map<String, String> userData = DataGenerator.getRegistrationData();

        Response response = coreRequests.makePostRequest(ENDPOINT_CREATE_USER, userData);

        Assertions.assertResponseCodeEquals(response, 200);
        Assertions.assertJsonContainsField(response, "id");
    }

    @Test
    @Description("User cannot register with invalid email")
    void cannotRegisterWithInvalidEmail() {
        Map<String, String> invalidData = new HashMap<>();
        invalidData.put("email", "learnqaexample.com");
        Map<String, String> registrationData = DataGenerator.getRegistrationData(invalidData);
        Response response = coreRequests.makePostRequest(ENDPOINT_CREATE_USER, registrationData);

        Assertions.assertResponseCodeEquals(response, 400);
        Assertions.assertResponseTextEquals(response, "Invalid email format");
    }

    @Test
    @Description ("User cannot register with short name")
    void cannotRegisterUserWithShortFirstName() {
        Map<String, String> invalidData = new HashMap<>();
        invalidData.put("firstName", "a");
        Map<String, String> registrationData = DataGenerator.getRegistrationData(invalidData);
        Response response = coreRequests.makePostRequest(ENDPOINT_CREATE_USER, registrationData);

        Assertions.assertResponseCodeEquals(response, 400);
        Assertions.assertResponseTextEquals(response, "The value of 'firstName' field is too short");
    }

    @Test
    @Description("User cannot register with too long firstName")
    void cannotRegisterUserWithTooLongFirstName() {
        Map<String, String> invalidData = new HashMap<>();
        String tooLongFirstName = StringGenerator.generateRandomString(251);
        invalidData.put("firstName", tooLongFirstName);
        Map<String, String> registrationData = DataGenerator.getRegistrationData(invalidData);
        Response response = coreRequests.makePostRequest(ENDPOINT_CREATE_USER, registrationData);

        Assertions.assertResponseCodeEquals(response, 400);
        Assertions.assertResponseTextEquals(response, "The value of 'firstName' field is too long");
    }

    @ParameterizedTest
    @ValueSource(strings = {"email", "password", "username", "firstName", "lastName"})
    @Description("User cannot registered without mandatory field")
    void cannotRegisterWithoutOneMandatoryField(String field) {
        Map<String, String> registrationData = DataGenerator.getRegistrationData();
        registrationData.remove(field);
        Response response = coreRequests.makePostRequest(ENDPOINT_CREATE_USER, registrationData);

        Assertions.assertResponseCodeEquals(response, 400);
        Assertions.assertResponseTextEquals(response, String.format("The following required params are missed: %s", field));
    }
}
