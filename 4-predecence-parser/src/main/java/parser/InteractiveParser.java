package parser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public abstract class InteractiveParser<N> extends Parser<N> {

    // -1 - no query, 0 - auto query
    protected int queryState;
    private String promptBegin = "> ";
    private String promptCont = ". ";
    private BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

    @Override
    public boolean begin(String input) throws SyntaxError {
        if (input == null) input = readLine(0);
        queryState = 0;
        return super.begin(input);
    }

    @Override
    public boolean ready() {
        return reader != null || super.ready();
    }

    // stage: 0-beforeAdvance/peek, 1-afterAdvance
    protected abstract boolean canRefeed(int stage);

    @Override
    public Token<N> peek() throws SyntaxError {
        if (canRefeed(0)) refeed();
        return super.peek();
    }

    public Token<N> advance() throws SyntaxError {
        Token<N> tok = super.advance();
        if (canRefeed(1)) refeed();
        return tok;
    }

    public String readLine(int lineNo) {
        try {
            if (lineNo == 0) System.out.print(promptBegin);
            else System.out.print(promptCont);
            String line = reader.readLine();
            if (line == null) {
                reader = null;
                return null;
            }
            return line;
        } catch (IOException e) {
            reader = null;
            return null;
        }
    }

    public boolean refeed() throws SyntaxError {
        while (curr == null) {
            String line = readLine(1);
            if (line == null) return false;
            input.feed("\n" + line);
            curr = lex();
            if (line.length() == 0) {
                queryState = -1;    // no more queries if empty line fed
                break;
            }
        }
        return true;
    }

}
