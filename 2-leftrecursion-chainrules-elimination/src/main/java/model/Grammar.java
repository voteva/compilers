package model;

import java.util.List;
import java.util.Map;

public class Grammar {

    private Map<String, String> terminalSymbols;
    private List<Nonterm> nonterminalSymbols;
    private List<Production> productions;
    private Nonterm startSymbol;

    public Map<String, String> getTerminalSymbols() {
        return terminalSymbols;
    }

    public Grammar setTerminalSymbols(Map<String, String> terminalSymbols) {
        this.terminalSymbols = terminalSymbols;
        return this;
    }

    public List<Nonterm> getNonterminalSymbols() {
        return nonterminalSymbols;
    }

    public Grammar setNonterminalSymbols(List<Nonterm> nonterminalSymbols) {
        this.nonterminalSymbols = nonterminalSymbols;
        return this;
    }

    public List<Production> getProductions() {
        return productions;
    }

    public Grammar setProductions(List<Production> productions) {
        this.productions = productions;
        return this;
    }

    public Nonterm getStartSymbol() {
        return startSymbol;
    }

    public Grammar setStartSymbol(Nonterm startSymbol) {
        this.startSymbol = startSymbol;
        return this;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        productions.forEach(p -> {
            sb.append(p.getLeftPart().getName());
            sb.append("->");
            p.getRightPart().forEach(r -> {
                if (r instanceof Nonterm) {
                    sb.append(r.getName());
                } else {
                    sb.append(terminalSymbols.get(r.getName()));
                }
            });
            sb.append("\n");
        });

        return sb.toString();
    }
}
