package operators;

import parser.Parser;
import parser.SyntaxError;
import parser.Token;

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