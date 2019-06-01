package anasy.whitespace;

import anasy.parser.Parser;
import anasy.parser.SyntaxError;
import anasy.parser.Token;
import anasy.parser.Tokind;

/**
 * Whitespace skipper. " \t\n". See Character.isWhitespace().
 *
 * @param <N>
 * @author jure
 */
public class Whitespace<N> extends Tokind<N> {

    public Whitespace(Parser<N> parser) {
        super(parser, "<whitespace>", 0);
    }

    public Token<N> lex() throws SyntaxError {
        while (Character.isWhitespace(input.peek())) input.advance();
        return null;
    }

}