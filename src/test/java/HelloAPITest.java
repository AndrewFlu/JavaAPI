import io.restassured.RestAssured;
import io.restassured.response.Response;

import org.junit.jupiter.api.Test;

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
    void getText(){
        String url = "https://playground.learnqa.ru/api/get_text";
        Response response = RestAssured.get(url).andReturn();
        response.print();
    }
}
