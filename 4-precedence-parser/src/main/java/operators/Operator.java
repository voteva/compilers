package anasy.operators;

import anasy.parser.*;


/**
 * Base class for operators: punctuation or keyword.
 *
 * @param <N>
 * @author jure
 */
public class Operator<N> extends Tokind<N> {

	private boolean isPunctuation;

	public Operator(Parser<N> parser, String name, int lbp) {
		super(parser, name, lbp);
		this.isPunctuation = !isKeyword(name);
	}

	public static <N> Tokind<N> make(Parser<N> parser, String lexeme, int lbp) {
		Tokind<N> kind = parser.find(lexeme);
		if (kind == null) return new Operator<N>(parser, lexeme, lbp);
		if (kind.lbp() == lbp) return kind;
		return null;
	}

	protected boolean isKeyword(String lexeme) {
		if (lexeme.length() <= 0) return false;
		char first = lexeme.charAt(0);
		if (!Character.isLetter(first) && first != '_') return false;
		for (char ch : lexeme.toCharArray())
			if (!Character.isLetterOrDigit(ch) && ch != '_') return false;
		return true;
	}

	@Override
	public Token<N> lex() throws SyntaxError {
		Location lexloc = input.mark();
		if (input.advanceIf(name) && (isPunctuation || !Character.isLetterOrDigit(input.peek())))
			return new Token<N>(this, lexloc);
		input.setLoc(lexloc);
		return null;
	}

}