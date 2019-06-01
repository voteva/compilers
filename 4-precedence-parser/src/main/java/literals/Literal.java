package literals;

import parser.Parser;
import exception.SyntaxException;
import parser.Token;
import parser.Tokind;

public abstract class Literal<N> extends Tokind<N> {

    public Literal(Parser<N> parser, String name) {
        super(parser, name, 0);
    }

    public abstract N makeNode(String lexeme);

    @Override
    public N parse(Token<N> token) throws SyntaxException {
        return token.getValue();
    }

}