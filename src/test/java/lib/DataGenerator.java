package lib;

import utils.StringGenerator;

import java.util.HashMap;
import java.util.Map;

public class DataGenerator {
    public static String getRandomEmail() {
        long timeStamp = System.currentTimeMillis();
        return String.format("learnqa%d@example.com", timeStamp);
    }

    public static Map<String, String> getRegistrationData() {
        Map<String, String> data = new HashMap<>();
        data.put("email", DataGenerator.getRandomEmail());
        data.put("password", StringGenerator.generateRandomString(7));
        data.put("username", String.format("learnQA_username+%s", StringGenerator.generateRandomString(3)));
        data.put("firstName", String.format("learnQA_firstname+%s", StringGenerator.generateRandomString(3)));
        data.put("lastName", String.format("learnQA_lastname+%s", StringGenerator.generateRandomString(3)));

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
