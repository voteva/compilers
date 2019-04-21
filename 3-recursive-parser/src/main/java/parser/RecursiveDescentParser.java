package parser;

import ast.BooleanExpression;
import ast.nonterminal.And;
import ast.nonterminal.Not;
import ast.nonterminal.Or;
import ast.nonterminal.Tilde;
import ast.terminal.False;
import ast.terminal.Ident;
import ast.terminal.True;
import exception.MalformedExpressionException;
import lexer.Lexer;

public class RecursiveDescentParser {

    private Lexer lexer;
    private int symbol;
    private BooleanExpression root;

    private final True t = new True();
    private final False f = new False();
    private final Ident ident = new Ident();

    public RecursiveDescentParser(Lexer lexer) {
        this.lexer = lexer;
    }

    public BooleanExpression build() {
        expression();
        return root;
    }

    private void expression() {
        term();
        while (symbol == Lexer.OR) {
            Or or = new Or();
            or.setLeft(root);
            term();
            or.setRight(root);
            root = or;
        }
    }

    private void term() {
        factor();
        while (symbol == Lexer.AND) {
            And and = new And();
            and.setLeft(root);
            factor();
            and.setRight(root);
            root = and;
        }
    }

    private void factor() {
        symbol = lexer.nextSymbol();
        if (symbol == Lexer.TRUE) {
            root = t;
            symbol = lexer.nextSymbol();
        } else if (symbol == Lexer.FALSE) {
            root = f;
            symbol = lexer.nextSymbol();
        } else if (symbol == Lexer.IDENT) {
            root = ident;
            symbol = lexer.nextSymbol();
        } else if (symbol == Lexer.NOT) {
            Not not = new Not();
            factor();
            not.setChild(root);
            root = not;
        } else if (symbol == Lexer.TILDE) {
            Tilde tilde = new Tilde();
            factor();
            tilde.setChild(root);
            root = tilde;
        } else if (symbol == Lexer.LEFT) {
            expression();
            symbol = lexer.nextSymbol(); // don't care about ')'
        } else {
            throw new MalformedExpressionException();
        }
    }
}
