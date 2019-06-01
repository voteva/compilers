package anasy.parser;

/**
 * Exception for syntax errors.
 *
 * @author jure
 */
@SuppressWarnings("serial")
public class SyntaxError extends Exception {

    private final Location loc;

    public SyntaxError(String msg, Location loc) {
        super(msg);
        this.loc = loc;
    }

    @Override
    public String toString() {
        return "Syntax error at " + loc + ": " + getLocalizedMessage() + ".";
    }

}
