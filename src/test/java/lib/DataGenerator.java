package lib;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

public class DataGenerator {
    public static String getRandomEmail() {
        String timeStamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new java.util.Date());
        return String.format("learnqa%s@example.com", timeStamp);
    }

    public static Map<String, String> getRegistrationData() {
        Map<String, String> data = new HashMap<>();
        data.put("email", DataGenerator.getRandomEmail());
        data.put("password", "123456");
        data.put("username", "learnQA_username");
        data.put("firstName", "learnQA_firstname");
        data.put("lastName", "learnQA_lastname");

        return data;
    }

    public static Map<String, String> getRegistrationData(Map<String, String> nonDefaultData) {
        Map<String, String> defaultRegistrationData = DataGenerator.getRegistrationData();

        Map<String, String> data = new HashMap<>();
        String[] keys = {"email", "password", "username", "firstName", "lastName"};
        for (String key : keys) {
            if (nonDefaultData.containsKey(key)) {
                data.put(key, nonDefaultData.get(key));
            } else {
                data.put(key, defaultRegistrationData.get(key));
            }
        }

        return data;
    }
}
