package ast.nonterminal;

import ast.BooleanExpression;
import ast.NonTerminal;

public class Not extends NonTerminal {

    public void setChild(BooleanExpression child) {
        setLeft(child);
    }

    public void setRight(BooleanExpression right) {
        throw new UnsupportedOperationException();
    }

    public String toString() {
        return String.format("!%s", left);
    }
}
