import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class HelloEndpointTests {
    public static final String ENDPOINT_HELLO = "https://playground.learnqa.ru/api/hello";

    @Test
    void defaultName() {
        JsonPath response = RestAssured
                .get(ENDPOINT_HELLO)
                .jsonPath();
        String answer = response.getString("answer");

        assertEquals("Hello, someone", answer, "Unexpected response!");
    }

    @Test
    void parameterizedName() {
        String name = "John Snow";
        JsonPath response = RestAssured
                .given()
                .param("name", name)
                .when()
                .get(ENDPOINT_HELLO)
                .jsonPath();
        String answer = response.getString("answer");

        assertEquals(String.format("Hello, %s", name), answer, "Unexpected response!" );
    }
}
