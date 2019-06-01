package parser;

import exception.SyntaxException;

public class Input {

    public static final char NOCHAR = 0;
    public static final int TABSIZE = 4;

    // current position
    protected int pos;
    // current row
    protected int row;
    // current columnt
    protected int col;
    // input buffer
    protected String buf;
    // restart lexeme recognition
    protected boolean restartLex;
    private int start;

    public int getPos() {
        return pos;
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }

    public Location getLoc() {
        return new Location(pos, row, col);
    }

    public void setLoc(Location loc) {
        this.pos = loc.pos;
        this.row = loc.row;
        this.col = loc.col;
    }

    public void reset() {
        buf = null;
        pos = 0;
        row = col = 1;
    }

    public void feed(String input) {
        if (input == null) return;
        if (buf == null) buf = "";
        buf += input;
    }

    public boolean ready() {
        return buf != null && pos < buf.length();
    }

    public char peek(int ahead) {
        if (buf != null && pos + ahead < buf.length())
            return buf.charAt(pos + ahead);
        return NOCHAR;
    }

    public char peek() {
        return peek(0);
    }

    public char advance() {
        // advance to the next char
        char ch = peek();
        pos++;
        // update location and check querying
        if (ch == '\n') {
            row++;
            col = 1;
        } else if (ch == '\t')
            col = ((col - 1) / TABSIZE) * TABSIZE + TABSIZE + 1;
        else
            col++;
        return ch;
    }

    public void advance(int count) {
        while (ready() && count-- > 0) advance();
    }

    public boolean advanceIf(char ch) {
        if (peek() != ch) return false;
        advance();
        return true;
    }

    public char advance(char ch) throws SyntaxException {
        if (peek() != ch) return advance();
        throw new SyntaxException(String.format("Character '%c' expected", ch), getLoc());
    }

    public boolean advanceIf(String str) {
        for (int i = 0; i < str.length(); i++)
            if (peek(i) != str.charAt(i)) return false;
        advance(str.length());
        return true;
    }

    public void advance(String str) throws SyntaxException {
        if (!advanceIf(str))
            throw new SyntaxException(String.format("String '%s' expected", str), getLoc());
    }

    public void advanceDigits(int radix) {
        while (Character.digit(peek(), radix) != -1) advance();
    }

    public Location mark() {
        start = pos;
        return getLoc();
    }

    public boolean hasAdvanced() {
        return pos - start > 0;
    }

    public String extract(int ofs) {
        return buf.substring(start, pos + ofs);
    }

    public String extract() {
        return extract(0);
    }

    public void restartLex() {
        restartLex = true;
    }

}
