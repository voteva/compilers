package whitespace;

import parser.Parser;
import parser.SyntaxError;
import parser.Token;
import parser.Tokind;

public class Whitespace<N> extends Tokind<N> {

    public Whitespace(Parser<N> parser) {
        super(parser, "<whitespace>", 0);
    }

    public Token<N> lex() throws SyntaxError {
        while (Character.isWhitespace(input.peek())) input.advance();
        return null;
    }

}