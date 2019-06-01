package whitespace;

import exception.SyntaxException;
import operators.Operator;
import parser.*;

import java.util.Stack;


public class Indenter<N> extends Tokind<N> {
    public Tokind<N> tokNewline;
    public Tokind<N> tokIndent;
    public Tokind<N> tokDedent;
    //
    int dedentCount;
    // indentation levels stack
    private Stack<Integer> indents;

    public Indenter(Parser<N> parser) {
        super(parser, "<indenter>", 0);
        //tokNewline = register("<newline>", 0);
        tokIndent = new Operator<N>(parser, "<indent>", 0);
        tokDedent = new Operator<N>(parser, "<dedent>", 0);
        indents = new Stack<Integer>();
        indents.clear();
        indents.add(1);
        dedentCount = 0;
    }

    public int getIndentationLevel() {
        return indents.size();
    }

    void checkIndentation() throws SyntaxException {
        // count dedentations
        while (input.getCol() < indents.peek()) {
            indents.pop();
            dedentCount++;
        }
        // check indentation level
        if (input.getCol() != indents.peek())
            throw new SyntaxException("invalid indentation level", input.getLoc());
    }

    @Override
    public Token<N> lex() throws SyntaxException {
        // generate any Dedent
        if (dedentCount > 0) {
            dedentCount--;
            return new Token<N>(tokDedent, input.getLoc());
        }
        // after dedents generate also newline (if not end of input)
//			if (dedented && lexer.peek() > 0)
//				tokens.add(new Token(tokenNewline, lexer.getLoc()));		
        Location locnl = null;
        while (Character.isWhitespace(input.peek())) {
            if (input.peek() == '\n') locnl = input.getLoc();
            input.advance();
        }
        if (locnl == null) return null;
        input.restartLex();
        int col = indents.peek();
        if (input.getCol() == col) {
            if (!input.ready()) return null;
            return new Token<N>(tokNewline, locnl);
        }
        if (input.getCol() > col) {
            indents.push(input.getCol());
            return new Token<N>(tokIndent, locnl);
        }
        // otherwise: lexer.loc.col < col
        checkIndentation();
        dedentCount--;        // dedentCount>0
        return new Token<N>(tokDedent, input.getLoc());
    }
}