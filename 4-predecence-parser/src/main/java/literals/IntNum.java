package literals;

import parser.Location;
import parser.Parser;
import parser.SyntaxError;
import parser.Token;

public abstract class IntNum<N> extends Literal<N> {

    public IntNum(Parser<N> parser, String name) {
        super(parser, name);
    }

    public Token<N> lex() throws SyntaxError {
        // digits
        if (!Character.isDigit(input.peek())) return null;
        Location lexloc = input.mark();
        input.advanceDigits(10);
        if (Character.isLetterOrDigit(input.peek()))
            throw new SyntaxError(String.format("Invalid digit '%c'", input.peek()), input.getLoc());
        return new Token<N>(this, lexloc, makeNode(input.extract()));
    }

}