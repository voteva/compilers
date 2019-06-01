package parser;

import exception.SyntaxException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class Parser<N> {

    protected Input input;
    protected List<Tokind<N>> tokinds;
    protected Token<N> curr;
    protected int depth;

    public Parser() {
        this.input = new Input();
        this.tokinds = new ArrayList<>();
        init();
    }

    protected abstract void init();

    public Tokind<N> find(String name) {
        for (Tokind<N> kind : tokinds) {
            if (kind.name.equals(name)) {
                return kind;
            }
        }
        return null;
    }

    protected Tokind<N> register(Tokind<N> kind) {
        tokinds.add(kind);
        Collections.sort(tokinds);
        return kind;
    }

    protected Token<N> lex() throws SyntaxException {
        restart:
        while (true) {
            input.restartLex = false;
            for (Tokind<N> kind : tokinds) {
                Token<N> tok = kind.lex();
                if (input.restartLex) {
                    continue restart;
                }
                if (tok != null) {
                    return tok;
                }
                if (!input.ready()) {
                    return null;
                }
            }
            throw new SyntaxException(String.format("invalid character '%c'", input.peek()), input.getLoc());
        }
    }

    public Token<N> peek() throws SyntaxException {
        return curr;
    }

    public boolean peek(Tokind<N> kind) throws SyntaxException {
        return peek() != null && curr.kind == kind;
    }

    public boolean ready() {
        return curr != null || input.ready();
    }

    public Token<N> advance() throws SyntaxException {
        Token<N> tok = peek();
        curr = lex();
        return tok;
    }

    public Token<N> advance(Tokind<N> kind) throws SyntaxException {
        if (peek(kind)) {
            return advance();
        }

        throw new SyntaxException("expected '" + kind + "'", (curr != null) ? curr.loc : input.getLoc());
    }

    public Token<N> advanceIf(Tokind<N> kind) throws SyntaxException {
        if (!peek(kind)) {
            return null;
        }

        return advance();
    }

    public N expression(int rbp) throws SyntaxException {
        depth++;

        Token<N> tok = advance();
        if (tok == null) {
            throw new SyntaxException("unexpected end of input", input.getLoc());
        }

        N tree = tok.parse();

        while (curr != null && curr.getLbp() > rbp) {
            tok = advance();
            tree = tok.parse(tree);
        }

        depth--;
        return tree;
    }


    protected N expression() throws SyntaxException {
        return expression(0);
    }

    protected N expression(int rbp, Class<?> nodeClass) throws SyntaxException {
        N node = expression(rbp);
        if (nodeClass.isInstance(node)) {
            return node;
        }
        throw new SyntaxException(String.format("Expected '%s'", nodeClass), input.getLoc());
    }

    public boolean begin(String s) throws SyntaxException {
        input.reset();
        input.feed(s);
        depth = 0;
        curr = lex();
        return curr != null;
    }

    public N parse(String input) throws SyntaxException {
        if (!begin(input)) {
            return null;
        }

        N tree = expression();
        return tree;
    }

    public N parse() throws SyntaxException {
        return parse(null);
    }
}
