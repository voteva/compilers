package anasy.literals;

import anasy.parser.Location;
import anasy.parser.Parser;
import anasy.parser.SyntaxError;
import anasy.parser.Token;


/**
 * Names -- identifiers -- start with a letter or underscore.
 *
 * @param <N>
 * @author jure
 */
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