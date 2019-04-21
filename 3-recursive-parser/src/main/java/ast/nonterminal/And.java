package ast.nonterminal;

import ast.NonTerminal;

public class And extends NonTerminal {

    public String toString() {
        return String.format("(%s & %s)", left, right);
    }
}
