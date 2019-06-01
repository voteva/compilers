package parser;

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

    /**
     * Append a string to current input buffer.
     *
     * @param input string to be appended
     */
    public void feed(String input) {
        if (input == null) return;
        if (buf == null) buf = "";
        buf += input;
    }

    /**
     * Is there any character available in input.
     *
     * @return true if available otherwise false
     */
    public boolean ready() {
        return buf != null && pos < buf.length();
    }

    /**
     * Peek a character from the input buffer.
     *
     * @param ahead offset into the future
     * @return peeked character
     */
    public char peek(int ahead) {
        if (buf != null && pos + ahead < buf.length())
            return buf.charAt(pos + ahead);
        return NOCHAR;
    }

    /**
     * Peek a character from the input buffer.
     *
     * @return peeked character
     */
    public char peek() {
        return peek(0);
    }

    /**
     * Advance to the next character.
     *
     * @return previous character
     */
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

    /**
     * Advance several characters.
     *
     * @param count number of characters to advance
     */
    public void advance(int count) {
        while (ready() && count-- > 0) advance();
    }

    /**
     * Advance if the next characters is equal to ch
     *
     * @param ch next character
     * @return true if advance succeeded
     */
    public boolean advanceIf(char ch) {
        if (peek() != ch) return false;
        advance();
        return true;
    }

    /**
     * Advance or throw syntax error.
     *
     * @param ch
     * @return
     * @throws SyntaxError
     */
    public char advance(char ch) throws SyntaxError {
        if (peek() != ch) return advance();
        throw new SyntaxError(String.format("Character '%c' expected", ch), getLoc());
    }

    /**
     * Advance if string is at the current input window.
     *
     * @param str
     * @return
     */
    public boolean advanceIf(String str) {
        for (int i = 0; i < str.length(); i++)
            if (peek(i) != str.charAt(i)) return false;
        advance(str.length());
        return true;
    }

    //

    /**
     * Advance string or throw syntax error
     *
     * @param str
     * @throws SyntaxError
     */
    public void advance(String str) throws SyntaxError {
        if (!advanceIf(str))
            throw new SyntaxError(String.format("String '%s' expected", str), getLoc());
    }

    /**
     * Advance while digits in given radix are at the input.
     *
     * @param radix
     */
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
