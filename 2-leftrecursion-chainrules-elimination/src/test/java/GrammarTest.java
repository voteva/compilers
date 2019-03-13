import executor.LeftRecursionEliminator;
import model.Grammar;
import org.junit.Test;
import util.GrammarReader;

public class GrammarTest {
    private static final String RESOURCES_PATH_PREFIX = "src/test/resources/";

    @Test
    public void test() {
        Grammar grammar = GrammarReader.readFromFile(RESOURCES_PATH_PREFIX + "grammar1.json");

        System.out.println("*** Initial grammar ***");
        System.out.println(grammar.toString());

        System.out.println("\n*** After left recursion elimination ***");
        System.out.println(LeftRecursionEliminator.eliminate(grammar).toString());
    }
}