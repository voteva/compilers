package executor;

import model.Epsilon;
import model.Grammar;
import model.Production;
import model.Symbol;

import java.util.LinkedList;
import java.util.List;

public class EpsilonTransitionsEliminator {

    public static Grammar eliminate(Grammar grammar) {
        List<Production> notEpsilonProductions = new LinkedList<>(grammar.getProductions());
        List<Production> epsilonProductions = new LinkedList<>();

        for (Production p : grammar.getProductions()) {
            if (p.getRightPart().contains(Epsilon.getInstance())) {
                epsilonProductions.add(p);

                // do not allow to delete for the start symbol
                if (!p.getLeftPart().equals(grammar.getStartSymbol())) {
                    notEpsilonProductions.remove(p);
                }
            }
        }

        List<Production> newProductions = new LinkedList<>(notEpsilonProductions);

        for (Production production : notEpsilonProductions) {
            for (Production epsilonProduction : epsilonProductions) {
                if (production.getRightPart().contains(epsilonProduction.getLeftPart())) {
                    List<Symbol> newRightPart = new LinkedList<>(production.getRightPart());
                    newRightPart.remove(epsilonProduction.getLeftPart());
                    newProductions.add(
                            new Production()
                                    .setLeftPart(production.getLeftPart())
                                    .setRightPart(newRightPart));
                }
            }
        }

        return grammar.setProductions(newProductions);
    }
}
