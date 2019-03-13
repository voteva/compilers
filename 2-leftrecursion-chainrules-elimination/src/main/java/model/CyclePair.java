package model;

public class CyclePair {
    private Nonterm leftPart;
    private Nonterm rightPart;

    public Nonterm getLeftPart() {
        return leftPart;
    }

    public Nonterm getRightPart() {
        return rightPart;
    }

    public CyclePair withLeftPart(Nonterm leftPart) {
        this.leftPart = leftPart;
        return this;
    }

    public CyclePair withRightPart(Nonterm rightPart) {
        this.rightPart = rightPart;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        if (getClass() != o.getClass()) return false;
        CyclePair other = (CyclePair) o;
        return this.leftPart.equals(other.leftPart) && this.rightPart.equals(other.rightPart);
    }

    @Override
    public int hashCode() {
        return 7 + 31 * this.leftPart.hashCode() + 7 * this.rightPart.hashCode();
    }
}
