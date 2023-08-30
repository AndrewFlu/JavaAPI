package tests;

import io.restassured.RestAssured;
import io.restassured.http.Headers;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import utils.FileReader;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class APITests {

    public static final String ENDPOINT_303 = "https://playground.learnqa.ru/api/get_303";
    public static final String ENDPOINT_505 = "https://playground.learnqa.ru/api/get_500";
    public static final String ENDPOINT_TEXT = "https://playground.learnqa.ru/api/get_text";
    public static final String ENDPOINT_CHECK_TYPE = "https://playground.learnqa.ru/api/check_type";
    private static final String ENDPOINT_ALL_HEADERS = "https://playground.learnqa.ru/api/show_all_headers";
    private static final String ENDPOINT_GET_AUTH_COOKIE = "https://playground.learnqa.ru/api/get_auth_cookie";
    private static final String ENDPOINT_CHECK_AUTH_COOKIE = "https://playground.learnqa.ru/api/check_auth_cookie";
    private static final String ENDPOINT_CHECK_AJAX_AUTH_COOKIE = "https://playground.learnqa.ru/ajax/api/check_auth_cookie";
    private static final String ENDPOINT_GET_JSON_HOMEWORK = "https://playground.learnqa.ru/api/get_json_homework";
    private static final String ENDPOINT_LONG_REDIRECT = "https://playground.learnqa.ru/api/long_redirect";
    private static final String ENDPOINT_LONGTIME_JOB = "https://playground.learnqa.ru/api/longtime_job";
    private static final String ENDPOINT_GET_SECRET_PASSWORD = "https://playground.learnqa.ru/ajax/api/get_secret_password_homework";
    private static final String ENDPOINT_HOMEWORK_COOKIE = "https://playground.learnqa.ru/api/homework_cookie";
    private static final String ENDPOINT_HOMEWORK_HEADER = "https://playground.learnqa.ru/api/homework_header";
    private static final String ENDPOINT_USER_AGENT_CHECK = "https://playground.learnqa.ru/ajax/api/user_agent_check";

    @Test
    void getText() {
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
    void redirect() {
        Response response = RestAssured
                .given()
                .redirects()
                .follow(false)
                .when()
                .get(ENDPOINT_303)
                .andReturn();
        int statusCode = response.statusCode();
        String locationHeader = response.getHeader("Location");
        System.out.println(statusCode);
        System.out.println("Location = " + locationHeader);
    }

    @Test
    void allHeaders() {
        Map<String, String> headers = new HashMap<>();
        headers.put("myHeader1", "myValue1");
        headers.put("myHeader2", "myValue2");
        Response response = RestAssured
                .given()
                .headers(headers)
                .when()
                .get(ENDPOINT_ALL_HEADERS)
                .andReturn();
        response.prettyPrint();

        Headers responseHeaders = response.getHeaders();
        System.out.println("Response headers: \n" + responseHeaders);
    }

    @Test
    void getCookies() {
        Map<String, String> data = new HashMap<>();
        data.put("login", "secret_login");
        data.put("password", "secret_pass");
        Response response = RestAssured
                .given()
                .body(data)
                .when()
                .post(ENDPOINT_GET_AUTH_COOKIE)
                .andReturn();

        System.out.println("Response:");
        response.prettyPrint();

        System.out.println();
        System.out.println("All headers:");
        System.out.println(response.getHeaders());

        System.out.println();
        System.out.println("Cookies:");
        System.out.println(response.getCookies());
    }

    @Test
    void useAuthCookie() {
        Map<String, String> authData = new HashMap<>();
        authData.put("login", "secret_login");
        authData.put("password", "secret_pass");
        Response getCookieResponse = RestAssured
                .given()
                .body(authData)
                .when()
                .post(ENDPOINT_GET_AUTH_COOKIE)
                .andReturn();
        String authCookie = getCookieResponse.getCookie("auth_cookie");

        Map<String, String> cookies = new HashMap<>();
        if (authCookie != null) {
            cookies.put("auth_cookie", authCookie);
        }
        Response checkCookieResponse = RestAssured
                .given()
                .body(authData)
                .cookies(cookies)
                .when()
                .post(ENDPOINT_CHECK_AUTH_COOKIE)
                .andReturn();
        checkCookieResponse.print();
    }

    @Test
    void getJsonHomeWork() {
        JsonPath response = RestAssured
                .get(ENDPOINT_GET_JSON_HOMEWORK)
                .jsonPath();
        List<LinkedHashMap<String, String>> responseList = response.getList("messages");

        System.out.println("Содержимое второго сообщения ответа: ");
        LinkedHashMap<String, String> secondMessage = responseList.get(1);
        Set<String> keys = secondMessage.keySet();
        for (String key : keys) {
            String value = secondMessage.get(key);
            System.out.println(key + " : " + value);
        }
    }

    @Test
    void testLongRedirect() {
        Response response = RestAssured
                .given()
                .redirects()
                .follow(false)
                .when()
                .get(ENDPOINT_LONG_REDIRECT)
                .andReturn();
        String location = response.getHeader("Location");
        System.out.printf("Адрес для перенаправления: %s", location);
    }

    @Test
    void redirectDepth() {
        int responseCode;
        String url = ENDPOINT_LONG_REDIRECT;
        List<String> locations = new ArrayList<>();
        do {
            Response response = RestAssured
                    .given()
                    .redirects()
                    .follow(false)
                    .when()
                    .get(url)
                    .andReturn();

            responseCode = response.getStatusCode();
            String location = response.getHeader("Location");

            if (responseCode == 301) {
                if (location != null) {
                    locations.add(location);
                    url = location;
                }
            }
        }
        while (responseCode != 200);

        System.out.printf("Количество перенаправлений: %d \n", locations.size());
        System.out.println("Список адресов, куда выполнялись перенаправления:");
        locations.forEach(System.out::println);
    }

    @Test
    void token() {
        String result;
        String status;

        JsonPath response_step1 = RestAssured
                .get(ENDPOINT_LONGTIME_JOB)
                .jsonPath();
        LinkedHashMap<String, Object> tokenResponse = response_step1.get();
        String token = (String) tokenResponse.get("token");
        int seconds = (int) tokenResponse.get("seconds");

        if (token != null && !token.isEmpty()) {
            JsonPath response_step2 = RestAssured
                    .given()
                    .param("token", token)
                    .when()
                    .get(ENDPOINT_LONGTIME_JOB)
                    .jsonPath();
            LinkedHashMap<String, String> statusResponse = response_step2.get();
            result = statusResponse.get("result");
            status = statusResponse.get("status");
            System.out.printf("result: %s\n", result);
            System.out.printf("status: %s\n", status);

            assertNull(result);
            assertEquals("Job is NOT ready", status);

            long delay = seconds * 1000L;
            System.out.printf("Запуск ожидания в %s мс...\n", delay);
            try {
                Thread.sleep(delay);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            JsonPath response_step2_1 = RestAssured
                    .given()
                    .param("token", token)
                    .when()
                    .get(ENDPOINT_LONGTIME_JOB)
                    .jsonPath();
            LinkedHashMap<String, String> statusResponse_2_1 = response_step2_1.get();
            result = statusResponse_2_1.get("result");
            status = statusResponse_2_1.get("status");
            System.out.printf("result: %s\n", result);
            System.out.printf("status: %s\n", status);

            assertNotNull(result);
            assertEquals("Job is ready", status);
        }
    }

    @Test
    void bruteForcePasswords() {
        String login = "super_admin";
        String password = null;
        String authCookieName = "auth_cookie";

        String passwordsPath = "src/test/resources/passwords_2011-2019.csv";
        List<String> passwordList = FileReader.getPasswords(passwordsPath);
        Set<String> uniquePasswords = FileReader.uniquePasswords(passwordList);

        // 1. get_secret_password_homework
        for (String uniquePassword : uniquePasswords) {
            Map<String, String> authData = new HashMap<>();
            authData.put("login", login);
            authData.put("password", uniquePassword);
            Response cookieResponse = RestAssured
                    .given()
                    .body(authData)
                    .when()
                    .post(ENDPOINT_GET_SECRET_PASSWORD)
                    .andReturn();

            String authCookieValue = cookieResponse.getCookie("auth_cookie");

            if (authCookieValue != null && !authCookieValue.isEmpty()) {
                // 2. check_auth_cookie
                Response checkCookieResponse = RestAssured
                        .given()
                        .header("Cookie", authCookieName + "=" + authCookieValue)
                        .body(authData)
                        .when()
                        .post(ENDPOINT_CHECK_AJAX_AUTH_COOKIE)
                        .andReturn();
                String result = checkCookieResponse.body().asString();

                if (result.equals("You are authorized")) {
                    password = uniquePassword;
                    break;
                }
            } else {
                fail("Не удалось получить auth_cookie!");
            }
        }
        if (password == null || password.isEmpty()) {
            fail("Не удалось подобрать пароль");
        } else {
            System.out.println("Учётные данные найдены!");
            System.out.printf("Логин: %s\nПароль: %s", login, password);
        }
    }

    @Test
    void getCookie() {
        String expectedCookieName = "HomeWork";
        Response response = RestAssured.get(ENDPOINT_HOMEWORK_COOKIE).andReturn();
        Map<String, String> cookies = response.getCookies();

        assertFalse(cookies.isEmpty());
        assertTrue(cookies.containsKey(expectedCookieName), String.format("Cookies doesn't contain '%s' cookie", expectedCookieName));
        assertEquals("hw_value", cookies.get(expectedCookieName), "Unexpected cookie's value");
    }

    @Test
    void getHeader() {
        Response response = RestAssured.get(ENDPOINT_HOMEWORK_HEADER).andReturn();
        Headers headers = response.getHeaders();

        assertTrue(headers.exist());
        assertTrue(headers.hasHeaderWithName("Date"));
        assertTrue(headers.hasHeaderWithName("Content-Type"));
        assertTrue(headers.hasHeaderWithName("Content-Length"));
        assertTrue(headers.hasHeaderWithName("Connection"));
        assertTrue(headers.hasHeaderWithName("Keep-Alive"));
        assertTrue(headers.hasHeaderWithName("Server"));
        assertTrue(headers.hasHeaderWithName("x-secret-homework-header"));
        assertTrue(headers.hasHeaderWithName("Cache-Control"));
        assertTrue(headers.hasHeaderWithName("Expires"));

        assertEquals("Some secret value", headers.getValue("x-secret-homework-header"),
                "Unexpected 'x-secret-homework-header' header value");
    }

    @ParameterizedTest
//    @CsvSource({
//            "'Mozilla/5.0 (Linux; U; Android 4.0.2; en-us; Galaxy Nexus Build/ICL53F) AppleWebKit/534.30 (KHTML, like Gecko) Version/4.0 Mobile Safari/534.30', Mobile, No, Android"
//            , "'Mozilla/5.0 (iPad; CPU OS 13_2 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) CriOS/91.0.4472.77 Mobile/15E148 Safari/604.1', Mobile, Chrome, iOS"
//            , "'Mozilla/5.0 (compatible; Googlebot/2.1; +http://www.google.com/bot.html)', Googlebot, Unknown, Unknown"
//            , "'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.77 Safari/537.36 Edg/91.0.100.0', Web, Chrome, No"
//            , "'Mozilla/5.0 (iPad; CPU iPhone OS 13_2_3 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/13.0.3 Mobile/15E148 Safari/604.1', Mobile, No, iPhone"
//    })
    @MethodSource("providers.DataProvider#dataProvider")
    void userAgent(String userAgent, String expectedPlatform, String expectedBrowser, String expectedDevice) {
        Response response = RestAssured
                .given()
                .header("user-agent", userAgent)
                .when()
                .get(ENDPOINT_USER_AGENT_CHECK)
                .andReturn();
        String actualPlatform = response.jsonPath().getString("platform");
        String actualBrowser = response.jsonPath().getString("browser");
        String actualDevice = response.jsonPath().getString("device");

        assertEquals(expectedPlatform, actualPlatform, "Unexpected platform");
        assertEquals(expectedBrowser, actualBrowser, "Unexpected browser");
        assertEquals(expectedDevice, actualDevice, "Unexpected device");
    }
}
