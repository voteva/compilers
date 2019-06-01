package anasy.operators;

import anasy.parser.Parser;
import anasy.parser.SyntaxError;
import anasy.parser.Token;


/**
 * Postfix operators.
 *
 * @param <N>
 * @author jure
 */
public abstract class Postfix<N> extends Operator<N> {

    public Postfix(Parser<N> parser, String name, int lbp) {
        super(parser, name, lbp);
    }

    public abstract N makePostfixNode(N operand);

    @Override
    public N parse(Token<N> token, N left) throws SyntaxError {
        return makePostfixNode(left);
    }

}