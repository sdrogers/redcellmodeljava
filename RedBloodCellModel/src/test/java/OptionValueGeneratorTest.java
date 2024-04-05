import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.beans.Transient;

import static org.junit.jupiter.api.Assertions.assertFalse;

import modelcomponents.OptionValueGenerator;
class OptionValueGeneratorTest {
 
    @Test
    void testCheck() {
        Boolean a = OptionValueGenerator.checkString("random(1e-10,3)");
        assertTrue(a);
        a = OptionValueGenerator.checkString("rffandom(1e-10,3)");
        assertFalse(a);
    }

    @Test
    void testSample() {
        Double d = OptionValueGenerator.getRandomValue("random(10,20)");
        assertTrue(d >= 10.0);
        assertTrue(d <= 20.0);
    }

    @Test
    void testDouble() {
        Double d = OptionValueGenerator.processRequest("1e-10");
        assertTrue(d == 1e-10);
    }

    @Test
    void testRandom() {
        Double d = OptionValueGenerator.processRequest("random(1.0, 2)");
        assertTrue(d >= 1.0);
        assertTrue(d <= 2.0);
    }
}