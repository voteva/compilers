package anasy.literals;

import anasy.parser.Location;
import anasy.parser.Parser;
import anasy.parser.SyntaxError;
import anasy.parser.Token;


/**
 * Simple real numbers.
 *
 * @param <N>
 * @author jure
 */
public abstract class RealNum<N> extends Literal<N> {

    public RealNum(Parser<N> parser, String name) {
        super(parser, name);
    }

    public Token<N> lex() throws SyntaxError {
        // digits.digits, .digits, digits.
        char c1 = input.peek();
        char c2 = input.peek(1);
        if (!(Character.isDigit(c1) || c1 == '.' && Character.isDigit(c2))) return null;
        Location lexloc = input.mark();
        input.advanceDigits(10);
        if (input.advanceIf('.')) input.advanceDigits(10);
        if (Character.isLetterOrDigit(input.peek()))
            throw new SyntaxError(String.format("Invalid digit '%c'", input.peek()), input.getLoc());
        return new Token<N>(this, lexloc, makeNode(input.extract()));
    }

}