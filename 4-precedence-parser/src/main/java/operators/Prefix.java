package anasy.operators;

import anasy.parser.Parser;
import anasy.parser.SyntaxError;
import anasy.parser.Token;


/**
 * Prefix operators.
 *
 * @param <N>
 * @author jure
 */
public abstract class Prefix<N> extends Operator<N> {

    private int pbp;    // prefix bounding precedence

    public Prefix(Parser<N> parser, String name, int pbp) {
        super(parser, name, 0);
        this.pbp = pbp;
    }

    public abstract N makePrefixNode(N operand);

    @Override
    public N parse(Token<N> token) throws SyntaxError {
        return makePrefixNode(parser.expression(pbp));
    }

}