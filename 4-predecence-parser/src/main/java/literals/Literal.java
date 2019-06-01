package literals;

import parser.Parser;
import parser.SyntaxError;
import parser.Token;
import parser.Tokind;

public abstract class Literal<N> extends Tokind<N> {

    public Literal(Parser<N> parser, String name) {
        super(parser, name, 0);
    }

    public abstract N makeNode(String lexeme);

    @Override
    public N parse(Token<N> token) throws SyntaxError {
        return token.getValue();
    }

}