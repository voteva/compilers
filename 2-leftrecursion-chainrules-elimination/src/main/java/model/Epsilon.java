package model;

public class Epsilon extends Symbol {
    private static final Symbol EPSILON_SYMBOL = new Term("EPSILON", "ϵ");

    public static Symbol getInstance() {
        return EPSILON_SYMBOL;
    }

    private Epsilon(String name) {
        super(name);
    }
}
