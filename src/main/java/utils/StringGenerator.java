package utils;

public class StringGenerator {
    public static String generateRandomString(int length) {
        if (length > 0) {
            char[] letters = {'a','b','c','d','e','f','g','h','i','j','k','l','m','n','o','p','q','r','s','t','u','v','w','x','y','z'};
            StringBuilder builder = new StringBuilder();
            for (int i = 0; i < length; i++) {
                builder.append(letters[(int) (Math.random() * (letters.length - 1))]);
            }
            return builder.toString();
        } else if (length == 0) {
            return "";

        } else throw new IllegalArgumentException("Invalid length parameter");
    }
}
