import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;

import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

public class HelloAPITest {
    @Test
    void printHello(){
        System.out.println("Hello from Andrew");
    }

    @Test
    void testRestAssured() {
        String url = "https://playground.learnqa.ru/api/hello";
        Response response = RestAssured.get(url).andReturn();
        response.prettyPrint();
    }

    @Test
    void testParameter() {
       String methodURL = "https://playground.learnqa.ru/api/hello";
        Response response = RestAssured
                .given()
                .queryParam("name", "John Snow")
                .get(methodURL).andReturn();
        response.prettyPrint();
    }

    @Test
    void testParameters() {
        String methodURL = "https://playground.learnqa.ru/api/hello";
        Map<String, String> params = new HashMap<>();
        params.put("name", "Sansa Stark");

        Response response = RestAssured
                .given()
                .queryParams(params)
                .get(methodURL)
                .andReturn();
        response.prettyPrint();
    }

    @Test
    void testJsonPath() {
        String methodURL = "https://playground.learnqa.ru/api/hello";
        JsonPath response = RestAssured
                .given()
                .queryParam("name", "John Snow")
                .get(methodURL)
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
    void getText(){
        String url = "https://playground.learnqa.ru/api/get_text";
        Response response = RestAssured.get(url).andReturn();
        response.print();
    }
}
