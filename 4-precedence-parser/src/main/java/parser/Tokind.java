package anasy.parser;

import java.util.Collections;

/**
 * Tokind is a central concept, and used to represent token kind. It is described with name,
 * recognition precedence and left binding precedence.
 *
 * @param <N>
 * @author jure
 */
public abstract class Tokind<N> implements Comparable<Tokind<N>> {

    protected final Parser<N> parser;
    protected final Input input;
    protected String name;
    protected int rp;        // recognition precedence
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

    /**
     * Compare token kinds based on recognition precedence.
     *
     * @param other
     * @return
     */
    @Override
    public int compareTo(Tokind<N> other) {
        int d = Math.abs(rp) - Math.abs(other.rp);
        if (d == 0) return other.name.length() - name.length();
        return d;
    }

    /**
     * Set recognition precedence.
     *
     * @param level
     */
    public void setLevel(int level) {
        this.rp = level;
        Collections.sort(this.parser.tokinds);
    }

    /**
     * Token recognition procedure.
     *
     * @return
     * @throws SyntaxError
     */
    protected abstract Token<N> lex() throws SyntaxError;

    /**
     * Initial parsing procedure.
     *
     * @param token
     * @return
     * @throws SyntaxError
     */
    protected N parse(Token<N> token) throws SyntaxError {
        return null;
    }

    /**
     * Continuation parsing procedure.
     *
     * @param token
     * @param left
     * @return
     * @throws SyntaxError
     */
    protected N parse(Token<N> token, N left) throws SyntaxError {
        return null;
    }
}