package ast.nonterminal;

import ast.NonTerminal;

public class Or extends NonTerminal {

    public String toString() {
        return String.format("(%s | %s)", left, right);
    }
}
