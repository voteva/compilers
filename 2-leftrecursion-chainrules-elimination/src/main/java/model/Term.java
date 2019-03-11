package model;

public class Term {

    private final String name;
    private final String spell;

    public Term(String name, String spell) {
        this.name = name;
        this.spell = spell;
    }

    public String getName() {
        return name;
    }

    public String getSpell() {
        return spell;
    }
}
