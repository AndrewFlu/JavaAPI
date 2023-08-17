import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;

import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

public class HelloAPITest {

    public static final String ENDPOINT_303 = "https://playground.learnqa.ru/api/get_303";
    public static final String ENDPOINT_505 = "https://playground.learnqa.ru/api/get_500";
    public static final String ENDPOINT_HELLO = "https://playground.learnqa.ru/api/hello";
    public static final String ENDPOINT_TEXT = "https://playground.learnqa.ru/api/get_text";
    public static final String ENDPOINT_CHECK_TYPE = "https://playground.learnqa.ru/api/check_type";

    @Test
    void printHello(){
        System.out.println("Hello from Andrew");
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
    void getText(){
        Response response = RestAssured.get(ENDPOINT_TEXT).andReturn();
        response.print();
    }

    @Test
    void testGetRequestType() {
        Response response = RestAssured
                .given()
                .queryParam("param1", "value1")
                .queryParam("param2", "value2")
                .get(ENDPOINT_CHECK_TYPE)
                .andReturn();
        response.print();
    }

    @Test
    void testPostRequestType() {
        Map<String, Object> body = new HashMap<>();
        body.put("param1", "value1");
        body.put("param2", "value2");
        Response response = RestAssured
                .given()
//                .body("param1=value1&param2=value2") // via String
//                .body("{\"param1\":\"value1\",\"param2\":\"value2\"}") // via JSON
                .body(body) // via Map
                .post(ENDPOINT_CHECK_TYPE)
                .andReturn();
        response.print();
    }

    @Test
    void getResponseCode() {
        Response response = RestAssured
                .get(ENDPOINT_505)
                .andReturn();
        int statusCode = response.getStatusCode();
        System.out.println(statusCode);
    }

    @Test
    void redirect () {
        Response response = RestAssured
                .given()
                .redirects()
                .follow(true)
                .when()
                .get(ENDPOINT_303)
                .andReturn();
        int statusCode = response.statusCode();
        System.out.println(statusCode);
    }
}
