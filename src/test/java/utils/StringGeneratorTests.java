package utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class StringGeneratorTests {
    @Test
    void generateString() {
        String string = StringGenerator.generateRandomString(10);
        System.out.println(string);
        assertEquals(10, string.length());
    }

    @Test
    void generateEmptyString() {
        String string = StringGenerator.generateRandomString(0);
        System.out.println(string);
        assertEquals(0, string.length());
    }

    @Test
    void generateLongString() {
        String string = StringGenerator.generateRandomString(260);
        System.out.println(string);
        assertEquals(260, string.length());
    }

    @Test
    void cannotGenerateWithInvalidParameter() {
        Throwable exception = assertThrows(IllegalArgumentException.class,
                () -> StringGenerator.generateRandomString(-1));
        assertEquals("Invalid length parameter", exception.getMessage());
    }

}
