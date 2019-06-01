package whitespace;

import parser.Parser;
import parser.SyntaxError;
import parser.Token;
import parser.Tokind;

/**
 * Inline comments. Comment begins with a given string (name).
 *
 * @param <N>
 * @author jure
 */
public class InlineComment<N> extends Tokind<N> {

    public InlineComment(Parser<N> parser, String name) {
        super(parser, name, 0);
        rp = 0;
    }

    @Override
    protected Token<N> lex() throws SyntaxError {
        if (input.advanceIf(name)) {
            while (input.ready() && input.peek() != '\n') input.advance();
            input.restartLex();
        }
        return null;
    }

}
