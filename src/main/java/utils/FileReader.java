package utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FileReader {
    public static List<String> getPasswords(String path) {
        List<String> passwords = new ArrayList<>();
        try {
            BufferedReader reader = new BufferedReader(new java.io.FileReader(path));
            String line = reader.readLine();
            do {
                String[] split = line.split("\t");
                passwords.add(split[1]);
                line = reader.readLine();
            }  while (line != null);

            reader.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return passwords;
    }
}
