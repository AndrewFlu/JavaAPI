package utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.*;

public class FileReader {
    public static List<String> getPasswords(String path) {
        List<String> passwords = new ArrayList<>();
        try {
            BufferedReader reader = new BufferedReader(new java.io.FileReader(path));
            String line = reader.readLine();
            do {
                String[] split = line.split("\t");
                passwords.addAll(Arrays.asList(split).subList(1, split.length));
                line = reader.readLine();
            }  while (line != null);

            reader.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return passwords;
    }

    public static Set<String> uniquePasswords(List<String> passwords) {
        return new HashSet<>(passwords);
    }
}
