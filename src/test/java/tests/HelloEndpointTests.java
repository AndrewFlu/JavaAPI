package tests;

import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class HelloEndpointTests {
    public static final String ENDPOINT_HELLO = "https://playground.learnqa.ru/api/hello";


    @ParameterizedTest
    @ValueSource(strings = {"", "John Snow", "Sansa Stark"})
    void parameterizedName(String name) {
        Map<String, String> requestParams = new HashMap<>();
        if (!name.isEmpty()) {
            requestParams.put("name", name);
        }

        JsonPath response = RestAssured
                .given()
                .params(requestParams)
                .when()
                .get(ENDPOINT_HELLO)
                .jsonPath();
        String answer = response.getString("answer");
        String expectedName = (name.isEmpty() ? "someone" : name);

        assertEquals(String.format("Hello, %s", expectedName), answer, "Unexpected response!");
    }
}
