import exception.MalformedExpressionException;
import org.junit.Test;

public class ParserTest {
    private static final String RESOURCES_PATH_PREFIX = "src/test/resources/";

    @Test
    public void testIdentExpression() {
        BooleanEvaluator.evaluateExpression("!a & b");
    }

    @Test
    public void testExpression() {
        BooleanEvaluator.evaluateExpression("~true & !false | true");
    }

    @Test(expected = MalformedExpressionException.class)
    public void testWrongExpression() {
        BooleanEvaluator.evaluateExpression("& true");
    }

    @Test
    public void testExpressionFromFile() {
        BooleanEvaluator.evaluateFromFile(RESOURCES_PATH_PREFIX + "expression.txt");
    }
}
