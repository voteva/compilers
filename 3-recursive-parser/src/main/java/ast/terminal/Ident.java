package ast.terminal;

import ast.BooleanExpression;

public class Ident implements BooleanExpression {

    @Override
    public String toString() {
        return "IDENT";
    }
}
