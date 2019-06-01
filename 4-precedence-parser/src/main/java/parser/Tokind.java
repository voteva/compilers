package parser;

import exception.SyntaxException;

import java.util.Collections;

public abstract class Tokind<N> implements Comparable<Tokind<N>> {

    protected final Parser<N> parser;
    protected final Input input;
    protected String name;
    protected int rp;         // recognition precedence
    protected int lbp;        // left binding precedence

    public Tokind(Parser<N> parser, String name, int lbp) {
        this.parser = parser;
        this.input = parser.input;
        this.name = name;
        this.lbp = lbp;
        this.rp = 10;
        this.parser.register(this);
    }

    @Override
    public String toString() {
        return name;
    }

    public String name() {
        return name;
    }

    public int lbp() {
        return lbp;
    }

    @Override
    public int compareTo(Tokind<N> other) {
        int d = Math.abs(rp) - Math.abs(other.rp);
        if (d == 0) return other.name.length() - name.length();
        return d;
    }

    public void setLevel(int level) {
        this.rp = level;
        Collections.sort(this.parser.tokinds);
    }

    protected abstract Token<N> lex() throws SyntaxException;

    protected N parse(Token<N> token) throws SyntaxException {
        return null;
    }

    protected N parse(Token<N> token, N left) throws SyntaxException {
        return null;
    }
}