package executor;

import model.Grammar;
import model.Nonterm;
import model.Production;
import model.Symbol;
import model.Term;

import java.util.LinkedList;
import java.util.List;

public class CyclesEliminator {
    private static final Symbol EPSILON_SYMBOL = new Term("EPSILON");

    public static Grammar eliminate(Grammar grammar) {
        List<Production> newProductions = new LinkedList<>(grammar.getProductions());

        for (Production production : grammar.getProductions()) {
            if (production.getRightPart().size() == 1 && production.getRightPart().get(0) instanceof Nonterm) {
                for (Production other : grammar.getProductions()) {
                    if (other.getLeftPart().equals(production.getRightPart().get(0)) &&
                            other.getRightPart().size() == 1 &&
                            other.getRightPart().get(0).equals(production.getLeftPart())) {
                        newProductions.remove(production);
                        /*findProductions().forEach(p -> {
                            newProductions.add(
                                    new Production()
                                            .setLeftPart(production.getLeftPart())
                                            .setRightPart(p.getRightPart()));
                        });*/
                    }
                }
            }
        }

        grammar.setProductions(newProductions);
        return grammar;
    }

    /*private static List<Production> findProductions(List) {

    }*/
}
