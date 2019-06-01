package exception;

import parser.Location;

@SuppressWarnings("serial")
public class SyntaxException extends Exception {

    private final Location loc;

    public SyntaxException(String msg, Location loc) {
        super(msg);
        this.loc = loc;
    }

    @Override
    public String toString() {
        return "Syntax error at " + loc + ": " + getLocalizedMessage() + ".";
    }

}
