package exception;

public class MalformedExpressionException extends RuntimeException {

    public MalformedExpressionException() {
        super("Expression Malformed");
    }
}
