import executor.ChainRulesEliminator;
import executor.EpsilonTransitionsEliminator;
import executor.LeftRecursionEliminator;
import model.Grammar;
import org.junit.Test;
import util.GrammarReader;

public class GrammarTest {
    private static final String RESOURCES_PATH_PREFIX = "src/test/resources/";

    @Test
    public void test() {
        Grammar grammar = GrammarReader.readFromFile(RESOURCES_PATH_PREFIX + "grammar2.json");

        System.out.println("*** Initial grammar ***");
        System.out.println(grammar.toString());

        System.out.println("\n*** After epsilon transitions elimination ***");
        System.out.println(EpsilonTransitionsEliminator.eliminate(grammar).toString());

        System.out.println("\n*** After chain rules elimination ***");
        System.out.println(ChainRulesEliminator.eliminate(grammar).toString());

        System.out.println("\n*** After left recursion elimination ***");
        System.out.println(LeftRecursionEliminator.eliminate(grammar).toString());

        System.out.println("\n*** Final ***");
        System.out.println(
                ChainRulesEliminator.eliminate(
                        EpsilonTransitionsEliminator.eliminate(grammar)
                ).toString());
    }
}