package model;

import java.util.List;

public class Grammar {

    private List<Term> terminalSymbols;
    private List<Nonterm> nonterminalSymbols;
    private List<Production> productions;
    private Nonterm startSymbol;

    public List<Term> getTerminalSymbols() {
        return terminalSymbols;
    }

    public Grammar setTerminalSymbols(List<Term> terminalSymbols) {
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
}
