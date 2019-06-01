package anasy.literals;

import anasy.parser.Parser;
import anasy.parser.SyntaxError;
import anasy.parser.Token;
import anasy.parser.Tokind;


/**
 * Base class for literal tokinds.
 *
 * @param <N>
 * @author jure
 */
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