package model;

import java.util.List;

public class Production {

    private Nonterm leftPart;
    private List<Symbol> rightPart;

    public Nonterm getLeftPart() {
        return leftPart;
    }

    public Production setLeftPart(Nonterm leftPart) {
        this.leftPart = leftPart;
        return this;
    }

    public List<Symbol> getRightPart() {
        return rightPart;
    }

    public Production setRightPart(List<Symbol> rightPart) {
        this.rightPart = rightPart;
        return this;
    }
}
