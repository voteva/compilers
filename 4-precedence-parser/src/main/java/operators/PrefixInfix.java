package anasy.operators;

import anasy.parser.Parser;
import anasy.parser.SyntaxError;
import anasy.parser.Token;


/**
 * Prefix and infix operators.
 *
 * @param <N>
 * @author jure
 */
public abstract class PrefixInfix<N> extends Operator<N> {

    private int pbp;                // prefix bounding precedence
    private boolean rightAssoc;        // associativity for infix

    public PrefixInfix(Parser<N> parser, String name, int pbp, int lbp, boolean rightAssoc) {
        super(parser, name, lbp);
        this.pbp = pbp;
        this.rightAssoc = rightAssoc;
    }

    public PrefixInfix(Parser<N> parser, String name, int pbp, int lbp) {
        this(parser, name, pbp, lbp, false);
    }

    public abstract N makePrefixNode(N operand);

    public abstract N makeInfixNode(N left, N right);

    @Override
    public N parse(Token<N> token) throws SyntaxError {
        return makePrefixNode(parser.expression(pbp));
    }

    @Override
    public N parse(Token<N> token, N left) throws SyntaxError {
        N right = parser.expression(rightAssoc ? lbp - 1 : lbp);
        return makeInfixNode(left, right);
    }
}