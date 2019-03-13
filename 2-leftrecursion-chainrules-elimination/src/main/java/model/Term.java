package model;

public class Term extends Symbol {

    private String spell;

    public Term(String name) {
        super(name);
    }

    public Term(String name, String spell) {
        super(name);
        this.spell = spell;
    }

    public String getSpell() {
        return spell;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        if (getClass() != o.getClass()) return false;
        Term other = (Term) o;
        return this.getName() != null && this.getName().equals(other.getName());
    }

    @Override
    public int hashCode() {
        return 7 + 31 * this.getName().hashCode();
    }
}
