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

    @Test
    void testGetRequestType() {
        String endpointURL = "https://playground.learnqa.ru/api/check_type";
        Response response = RestAssured
                .given()
                .queryParam("param1", "value1")
                .queryParam("param2", "value2")
                .get(endpointURL)
                .andReturn();
        response.print();
    }

    @Test
    void testPostRequestType() {
        String endpointURL = "https://playground.learnqa.ru/api/check_type";
        Map<String, Object> body = new HashMap<>();
        body.put("param1", "value1");
        body.put("param2", "value2");
        Response response = RestAssured
                .given()
//                .body("param1=value1&param2=value2") // via String
//                .body("{\"param1\":\"value1\",\"param2\":\"value2\"}") // via JSON
                .body(body) // via Map
                .post(endpointURL)
                .andReturn();
        response.print();
    }
}
