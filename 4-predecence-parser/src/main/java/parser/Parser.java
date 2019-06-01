package parser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public abstract class Parser<N> {

    // ***** Tokenizer *****

    protected Input input;
    protected List<Tokind<N>> tokinds;
    protected Token<N> curr;
    protected int depth;

    public Parser() {
        this.input = new Input();
        this.tokinds = new ArrayList<Tokind<N>>();
        init();
    }

    /**
     * An abstract method which initializes the parser. Should be overridden with proper construction of token kinds.
     */
    protected abstract void init();

    /**
     * Find a token kind by the name.
     *
     * @param name
     * @return
     */
    public Tokind<N> find(String name) {
        for (Tokind<N> kind : tokinds)
            if (kind.name.equals(name)) return kind;
        return null;
    }

    /**
     * Register a new token kind.
     *
     * @param kind
     * @return
     */
    protected Tokind<N> register(Tokind<N> kind) {
        tokinds.add(kind);
        Collections.sort(tokinds);
        return kind;
    }

    /**
     * Recognize next token at the input.
     *
     * @return token at the input
     * @throws SyntaxError
     */
    protected Token<N> lex() throws SyntaxError {
        restart:
        while (true) {
            input.restartLex = false;
            for (Tokind<N> kind : tokinds) {
                Token<N> tok = kind.lex();
                if (input.restartLex) continue restart;
                if (tok != null) return tok;
                if (!input.ready()) return null;
            }
            throw new SyntaxError(String.format("invalid character '%c'", input.peek()), input.getLoc());
        }
    }

    /**
     * Peek the next available token.
     *
     * @return
     * @throws SyntaxError
     */
    public Token<N> peek() throws SyntaxError {
        return curr;
    }

    /**
     * Is the next available token of a given kind.
     *
     * @param kind
     * @return
     * @throws SyntaxError
     */
    public boolean peek(Tokind<N> kind) throws SyntaxError {
        return peek() != null && curr.kind == kind;
    }

    /**
     * Is next token available?
     *
     * @return
     */
    public boolean ready() {
        return curr != null || input.ready();
    }

    /**
     * Advance to the next available token.
     *
     * @return previous token
     * @throws SyntaxError
     */
    public Token<N> advance() throws SyntaxError {
        Token<N> tok = peek();
        curr = lex();
        return tok;
    }

    /**
     * Advance next token or throw syntax error
     *
     * @param kind
     * @return
     * @throws SyntaxError
     */
    public Token<N> advance(Tokind<N> kind) throws SyntaxError {
        if (peek(kind)) return advance();
        throw new SyntaxError("expected '" + kind + "'", (curr != null) ? curr.loc : input.getLoc());
    }

    /**
     * Advance or return false
     *
     * @param kind
     * @return
     * @throws SyntaxError
     */
    public Token<N> advanceIf(Tokind<N> kind) throws SyntaxError {
        if (!peek(kind)) return null;
        return advance();
    }

    // ***** Parser *****

    /**
     * Parse an operator-precedence expression using Pratt's algorithm.
     *
     * @param rbp initial right binding precedence
     * @return representation of parsed expression
     * @throws SyntaxError
     */
    public N expression(int rbp) throws SyntaxError {
        depth++;
        // get token
        Token<N> tok = advance();
        if (tok == null) throw new SyntaxError("unexpected end of input", input.getLoc());
        // parse token
        N tree = tok.parse();
        // while next token exists and has higher binding power
        while (curr != null && curr.getLbp() > rbp) {
            // get next token and parseLeft it
            tok = advance();
            tree = tok.parse(tree);
        }
        depth--;
        return tree;
    }

    /**
     * As expression(0)
     *
     * @return
     * @throws SyntaxError
     */
    protected N expression() throws SyntaxError {
        return expression(0);
    }

    /**
     * Parse expression and verify its class
     *
     * @param rbp
     * @param nodeClass
     * @return
     * @throws SyntaxError
     */
    protected N expression(int rbp, Class<?> nodeClass) throws SyntaxError {
        N node = expression(rbp);
        if (nodeClass.isInstance(node)) return node;
        throw new SyntaxError(String.format("Expected '%s'", nodeClass), input.getLoc());
    }

    /**
     * Begin parsing
     *
     * @param s input buffer to be parsed
     * @return
     * @throws SyntaxError
     */
    public boolean begin(String s) throws SyntaxError {
        input.reset();
        input.feed(s);
        depth = 0;
        curr = lex();
        return curr != null;
    }

    /**
     * Complete parse procedure
     *
     * @param input
     * @return
     * @throws SyntaxError
     */
    public N parse(String input) throws SyntaxError {
        if (!begin(input)) return null;
        N tree = expression();
//      if (peek() != null) throw new SyntaxError("end of input expected near '" + peek() + "'", peek().loc);
        return tree;
    }

    public N parse() throws SyntaxError {
        return parse(null);
    }

}
