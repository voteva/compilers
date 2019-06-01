package anasy.operators;

import anasy.parser.Parser;
import anasy.parser.SyntaxError;
import anasy.parser.Token;


/**
 * Prefix and postfix operators.
 *
 * @param <N>
 * @author jure
 */
public abstract class PrefixPostfix<N> extends Operator<N> {

    int pbp;

    public PrefixPostfix(Parser<N> parser, String name, int pbp, int lbp) {
        super(parser, name, lbp);
        this.pbp = pbp;
    }

    public abstract N makePrefixNode(N operand);

    public abstract N makePostfixNode(N operand);

    @Override
    public N parse(Token<N> token) throws SyntaxError {
        return makePrefixNode(parser.expression(pbp));
    }

    @Override
    public N parse(Token<N> token, N left) throws SyntaxError {
        return makePostfixNode(left);
    }

}