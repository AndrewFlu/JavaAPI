package utils;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class FileReaderTest {
    @Test
    void canParseFileWith20Passwords() {
        String filePath = "src/test/resources/commonpasswords_2021.csv";
        List<String> passwords = FileReader.getPasswords(filePath);

        assertNotNull(passwords);
        assertEquals(20, passwords.size());
        assertEquals("password123", passwords.get(passwords.size() - 1));
    }

    @Test
    void canParseFileWith225() {
        String file = "src/test/resources/passwords_2011-2019.csv";
        List<String> passwords = FileReader.getPasswords(file);

        assertNotNull(passwords);
        assertEquals(225, passwords.size());
        assertEquals("password", passwords.get(0));
        assertEquals("123qwe", passwords.get(passwords.size() - 1));
    }

    @Test
    void testUniquePasswords () {
        String file = "src/test/resources/passwords_2011-2019.csv";
        List<String> passwords = FileReader.getPasswords(file);
        Set<String> uniquePasswords = FileReader.uniquePasswords(passwords);
        assertFalse(uniquePasswords.isEmpty());
        assertTrue(uniquePasswords.contains("123456"));
    }
}
