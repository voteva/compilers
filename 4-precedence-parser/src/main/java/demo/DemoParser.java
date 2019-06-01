package demo;

import demo.Engine.Node;
import literals.Name;
import operators.Infix;
import operators.Outfix;
import operators.Prefix;
import parser.InteractiveParser;
import parser.Parser;
import exception.SyntaxException;
import parser.Token;
import whitespace.Whitespace;

import java.util.ArrayList;
import java.util.List;

public class DemoParser extends InteractiveParser<Node> {

    private Engine engine;

    DemoParser(Engine engine) {
        super();
        this.engine = engine;
    }

    @Override
    protected boolean canRefeed(int stage) {
        return curr == null && queryState >= 0 && stage == 0 && depth > 1;
    }

    @Override
    protected void init() {
        new Whitespace<>(this).setLevel(0);

        new Name<Node>(this, "<name>") {
            @Override
            public Node makeNode(String lexeme) {
                return engine.makeSym(lexeme);
            }
        }.setLevel(50);

        new TokParentheses(this);

        new TokInfix(this, "&", 40);
        new TokInfix(this, "|", 40);

        new Prefix<Node>(this, "!", 80) {
            @Override
            public Node makePrefixNode(Node operand) {
                return engine.new UnaryOp(name, operand);
            }
        };
    }

    class TokInfix extends Infix<Node> {
        public TokInfix(Parser<Node> parser, String name, int lbp) {
            super(parser, name, lbp);
        }

        @Override
        public Node makeInfixNode(Node left, Node right) {
            return engine.new BinOp(name, left, right);
        }
    }

    class TokParentheses extends Outfix<Node> {

        public TokParentheses(Parser<Node> parser) {
            super(parser, "(", ")", 0);
            this.lbp = 200;
        }

        @Override
        public Node parse(Token<Node> token, Node left) throws SyntaxException {
            if (!(left instanceof Engine.Sym)) {
                throw new SyntaxException("Function name expected", input.getLoc());
            }
            List<Node> exprs = new ArrayList<Node>();
            advance(tokClose);
            return engine.new Composite((Engine.Sym) left, exprs.toArray(new Node[exprs.size()]));
        }
    }
}