package operators;

import parser.Parser;
import exception.SyntaxException;
import parser.Token;

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
    public N parse(Token<N> token, N left) throws SyntaxException {
        N right = parser.expression(rightAssoc ? lbp - 1 : lbp);
        return makeInfixNode(left, right);
    }

}