package lib;

import io.restassured.response.Response;

import static org.hamcrest.Matchers.hasKey;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class Assertions {
    public static void assertJsonByName(Response response, String name, int expectedValue) {
        response.then().assertThat().body("$", hasKey(name));
        int value = response.jsonPath().getInt(name);

        assertEquals(expectedValue, value, "JSON value is not equal to expected value");
    }

    public static void assertStringLengthGraterThenSupplyLimit(String checkString, int minLength) {
        int actualLength = checkString.length();
        assertTrue(actualLength > minLength, String
                .format("Symbol count of string: '%s' less then allowed min limit: %s", checkString, minLength));
    }
}
