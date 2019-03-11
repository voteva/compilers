package model;

public class Symbol {

    private final String type;
    private final String name;

    public Symbol(String type, String name) {
        this.type = type;
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public String getName() {
        return name;
    }
}
