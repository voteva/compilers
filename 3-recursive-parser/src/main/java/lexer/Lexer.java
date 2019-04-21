package lexer;

import java.io.*;

public class Lexer {

    private StreamTokenizer input;

    public static final int EOL = -3;
    public static final int EOF = -2;
    public static final int INVALID = -1;

    public static final int OR = 1;
    public static final int AND = 2;
    public static final int NOT = 3;
    public static final int TILDE = 4;

    public static final int TRUE = 10;
    public static final int FALSE = 11;

    public static final int LEFT = 20;
    public static final int RIGHT = 21;
    public static final int IDENT = 22;

    private static final String TRUE_LITERAL = "true";
    private static final String FALSE_LITERAL = "false";

    public Lexer(InputStream in) {
        Reader r = new BufferedReader(new InputStreamReader(in));
        input = new StreamTokenizer(r);

        input.resetSyntax();
        input.wordChars('a', 'z');
        input.wordChars('A', 'Z');
        input.whitespaceChars('\u0000', ' ');
        input.whitespaceChars('\n', '\t');

        input.ordinaryChar('(');
        input.ordinaryChar(')');
        input.ordinaryChar('&');
        input.ordinaryChar('|');
        input.ordinaryChar('!');
        input.ordinaryChar('~');
    }

    public int nextSymbol() {
        int symbol;
        try {
            switch (input.nextToken()) {
                case StreamTokenizer.TT_EOL:
                    symbol = EOL;
                    break;
                case StreamTokenizer.TT_EOF:
                    symbol = EOF;
                    break;
                case StreamTokenizer.TT_WORD: {
                    if (input.sval.equalsIgnoreCase(TRUE_LITERAL)) {
                        symbol = TRUE;
                    } else if (input.sval.equalsIgnoreCase(FALSE_LITERAL)) {
                        symbol = FALSE;
                    } else {
                        symbol = IDENT;
                    }
                    break;
                }
                case '(':
                    symbol = LEFT;
                    break;
                case ')':
                    symbol = RIGHT;
                    break;
                case '&':
                    symbol = AND;
                    break;
                case '|':
                    symbol = OR;
                    break;
                case '!':
                    symbol = NOT;
                    break;
                case '~':
                    symbol = TILDE;
                    break;
                default:
                    symbol = INVALID;
            }
        } catch (IOException e) {
            symbol = EOF;
        }

        return symbol;
    }

    public String toString() {
        return input.toString();
    }
}
