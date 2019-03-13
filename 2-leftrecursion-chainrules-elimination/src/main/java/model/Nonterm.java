package model;

public class Nonterm extends Symbol {

    public Nonterm(String name) {
        super(name);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        if (getClass() != o.getClass()) return false;
        Nonterm other = (Nonterm) o;
        return this.getName() != null && this.getName().equals(other.getName());
    }

    @Override
    public int hashCode() {
        return 7 + 31 * this.getName().hashCode();
    }
}
