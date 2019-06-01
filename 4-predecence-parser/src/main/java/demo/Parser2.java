package demo;

import demo.Engine.Node;
import literals.Name;
import operators.Infix;
import operators.Operator;
import operators.Outfix;
import operators.Prefix;
import operators.PrefixInfix;
import operators.PrefixPostfix;
import parser.InteractiveParser;
import parser.Parser;
import parser.SyntaxError;
import parser.Token;
import parser.Tokind;
import whitespace.Whitespace;

import java.util.ArrayList;
import java.util.List;

public class Parser2 extends InteractiveParser<Node> {

    private Engine engine;

    Parser2(Engine engine) {
        super();
        this.engine = engine;
    }

    @Override
    protected boolean canRefeed(int stage) {
        return curr == null && queryState >= 0 && stage == 0 && depth > 1;
    }

    @Override
    protected void init() {
        new Whitespace<Node>(this).setLevel(0);

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

        public TokInfix(Parser<Node> parser, String name, int lbp, boolean rightAssoc) {
            super(parser, name, lbp, rightAssoc);
        }

        @Override
        public Node makeInfixNode(Node left, Node right) {
            return engine.new BinOp(name, left, right);
        }
    }

    class TokPrefixInfix extends PrefixInfix<Node> {
        public TokPrefixInfix(Parser<Node> parser, String name, int pbp, int lbp) {
            super(parser, name, pbp, lbp);
        }

        @Override
        public Node makePrefixNode(Node operand) {
            return engine.new UnaryOp(name, operand);
        }

        @Override
        public Node makeInfixNode(Node left, Node right) {
            return engine.new BinOp(name, left, right);
        }
    }

    class TokPrefixPostfix extends PrefixPostfix<Node> {
        public TokPrefixPostfix(Parser<Node> parser, String name, int pbp, int lbp) {
            super(parser, name, pbp, lbp);
        }

        @Override
        public Node makePrefixNode(Node operand) {
            return engine.new UnaryOp(name, operand);
        }

        @Override
        public Node makePostfixNode(Node operand) {
            return engine.new UnaryOp(name, operand);
        }
    }

    class TokParentheses extends Outfix<Node> {
        Tokind<Node> tokComma;

        public TokParentheses(Parser<Node> parser) {
            super(parser, "(", ")", 0);
            this.lbp = 200;
            this.tokComma = Operator.make(parser, ",", 0);
        }

        @Override
        public Node parse(Token<Node> token, Node left) throws SyntaxError {
            if (!(left instanceof Engine.Sym))
                throw new SyntaxError("Function name expected", input.getLoc());
            // function call or definition
            List<Node> exprs = new ArrayList<Node>();
            if (advanceIf(tokClose) == null)
                do
                    exprs.add(expression(0));
                while (advanceIf(tokComma) != null);
            advance(tokClose);
            return engine.new Composite((Engine.Sym) left, exprs.toArray(new Node[exprs.size()]));
        }
    }
}