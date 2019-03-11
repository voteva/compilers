import model.Grammar;
import org.junit.Test;
import util.GrammarReader;

import static org.junit.Assert.assertNotNull;

public class GrammarTest {
    private static final String RESOURCES_PATH_PREFIX = "src/test/resources/";

    @Test
    public void test() {
        Grammar grammar = GrammarReader.readFromFile(RESOURCES_PATH_PREFIX + "grammar1.json");

        assertNotNull(grammar);
    }
}