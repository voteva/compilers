package anasy.operators;

import anasy.parser.Parser;
import anasy.parser.SyntaxError;
import anasy.parser.Token;


/**
 * Infix operators.
 *
 * @param <N>
 * @author jure
 */
public abstract class Infix<N> extends Operator<N> {

    private boolean rightAssoc;

    public Infix(Parser<N> parser, String name, int lbp, boolean rightAssoc) {
        super(parser, name, lbp);
        this.rightAssoc = rightAssoc;
    }

    public Infix(Parser<N> parser, String name, int lbp) {
        this(parser, name, lbp, false);
    }

    public abstract N makeInfixNode(N left, N right);

    @Override
    public N parse(Token<N> token, N left) throws SyntaxError {
        N right = parser.expression(rightAssoc ? lbp - 1 : lbp);
        return makeInfixNode(left, right);
    }

}