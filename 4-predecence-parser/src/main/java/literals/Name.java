package literals;

import parser.Location;
import parser.Parser;
import parser.SyntaxError;
import parser.Token;

public abstract class Name<N> extends Literal<N> {

    public Name(Parser<N> parser, String name) {
        super(parser, name);
    }

    public Token<N> lex() throws SyntaxError {
        if (!Character.isLetter(input.peek()) && input.peek() != '_') return null;
        Location lexloc = input.mark();
        while (Character.isLetterOrDigit(input.peek()) || input.peek() == '_') input.advance();
        return new Token<N>(this, lexloc, makeNode(input.extract()));
    }

}