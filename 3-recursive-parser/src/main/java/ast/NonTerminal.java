package ast;

public abstract class NonTerminal implements BooleanExpression {

    protected BooleanExpression left;
    protected BooleanExpression right;

    public void setLeft(BooleanExpression left) {
        this.left = left;
    }

    public void setRight(BooleanExpression right) {
        this.right = right;
    }
}
