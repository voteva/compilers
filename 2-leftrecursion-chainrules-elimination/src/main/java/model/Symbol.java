package model;

public class Symbol {

    private final String name;

    public Symbol(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        if (getClass() != o.getClass()) return false;
        Symbol other = (Symbol) o;
        return this.getName() != null && this.getName().equals(other.getName());
    }

    @Override
    public int hashCode() {
        return 7 + 31 * this.getName().hashCode();
    }
}
