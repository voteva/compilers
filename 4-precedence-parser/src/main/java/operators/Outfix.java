package operators;

import parser.Parser;
import exception.SyntaxException;
import parser.Token;

public class Outfix<N> extends Operator<N> {

    protected OutfixClose tokClose;

    public Outfix(Parser<N> parser, String lexemeOpen, String lexemeClose, int lbp) {
        super(parser, lexemeOpen, lbp);
        tokClose = new OutfixClose(parser, lexemeClose, lbp);
    }

    protected N makeNode(N operand) {
        return operand;
    }

    @Override
    public N parse(Token<N> token) throws SyntaxException {
        // check if open token is immediately followed by close token
        N tree = parser.peek(tokClose) ? null : parser.expression(0);
        // advance newline
//			parser.advanceIf(parser.tokenNewline);
        // advance the closing token
        parser.advance(tokClose);
        // return new object generated from content
        return makeNode(tree);
    }

    public class OutfixClose extends Operator<N> {
        public OutfixClose(Parser<N> parser, String name, int lbp) {
            super(parser, name, lbp);
        }
    }

}