import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

public class HelloEndpointTests {
    public static final String ENDPOINT_HELLO = "https://playground.learnqa.ru/api/hello";
    @Test
    void testParameters() {
        Map<String, String> params = new HashMap<>();
        params.put("name", "Sansa Stark");

        Response response = RestAssured
                .given()
                .queryParams(params)
                .get(ENDPOINT_HELLO)
                .andReturn();
        response.prettyPrint();
    }

    @Test
    void testJsonPath() {
        JsonPath response = RestAssured
                .given()
                .queryParam("name", "John Snow")
                .get(ENDPOINT_HELLO)
                .jsonPath();
        String key = "answer";
        String value = response.get(key);
        if (value == null) {
            System.out.printf("The key %s is absent.", key);
        } else {
            System.out.println(value);
        }
    }

    @Test
    void testRestAssured() {
        Response response = RestAssured.get(ENDPOINT_HELLO).andReturn();
        response.prettyPrint();
    }

    @Test
    void testParameter() {
        Response response = RestAssured
                .given()
                .queryParam("name", "John Snow")
                .get(ENDPOINT_HELLO).andReturn();
        response.prettyPrint();
    }
}
