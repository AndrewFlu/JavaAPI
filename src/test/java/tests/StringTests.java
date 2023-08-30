package tests;

import lib.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

public class StringTests {

    @ParameterizedTest
    @Disabled
    @ValueSource(strings = {"", "Invalid string", "Valid string for test min limit"})
    void stringLengthTest(String string) {
        Assertions.assertStringLengthGraterThenSupplyLimit(string, 15);
    }
}
