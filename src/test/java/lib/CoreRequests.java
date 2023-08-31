package lib;

import io.qameta.allure.Step;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.http.Header;
import io.restassured.response.Response;

import java.util.Map;

import static io.restassured.RestAssured.given;

public class CoreRequests {
    @Step("Make a GET-request with token and auth cookie")
    public Response makeGetRequest(String url, String token, String cookie) {
        return given()
                .filter(new AllureRestAssured())
                .header(new Header("x-csrf-token", token))
                .cookie("auth_sid", cookie)
                .get(url)
                .andReturn();
    }

    @Step("Make a GET-request with auth cookie only")
    public Response makeGetRequestWithCookie(String url, String cookie) {
        return given()
                .filter(new AllureRestAssured())
                .cookie("auth_sid", cookie)
                .get(url)
                .andReturn();
    }

    @Step("Make a GET-request with token only")
    public Response makeGetRequestWithToken(String url, String token) {
        return given()
                .filter(new AllureRestAssured())
                .header(new Header("x-csrf-token", token))
                .get(url)
                .andReturn();
    }

    @Step("Make a POST-Request")
    public Response makePostRequest(String url, Map<String, String> bodyData) {
        return given()
                .filter(new AllureRestAssured())
                .body(bodyData)
                .post(url)
                .andReturn();
    }

    @Step("Make a simple GET-request")
    public Response makeGetRequest(String url) {
        return makeGetRequest(url, null, null);
    }

    @Step("Make a simple PUT-request")
    public Response makePutRequest(String url, String token, String authCookie, Map<String, String> userData) {
        return given()
                .filter(new AllureRestAssured())
                .header(new Header("x-csrf-token", token))
                .cookie("auth_sid", authCookie)
                .body(userData)
                .put(url)
                .andReturn();
    }
}
