package utils;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class FileReaderTest {
    @Test
    void canParseFileWithPasswords() {
        String filePath = "src/test/resources/commonpasswords.csv";
        List<String> passwords = FileReader.getPasswords(filePath);

        assertNotNull(passwords);
        assertEquals(20, passwords.size());
        assertEquals("password123", passwords.get(passwords.size() - 1));
    }
}
