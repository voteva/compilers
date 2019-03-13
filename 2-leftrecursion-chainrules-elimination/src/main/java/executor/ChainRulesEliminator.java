package executor;

import model.CyclePair;
import model.Grammar;
import model.Nonterm;
import model.Production;
import model.Symbol;
import util.ProductionMapCreator;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Stack;

public class ChainRulesEliminator {

    public static Grammar eliminate(Grammar grammar) {
        EpsilonTransitionsEliminator.eliminate(grammar);

        List<Production> productionsWithoutCycleRules = new LinkedList<>();
        for (Production production : grammar.getProductions()) {
            if (!isChain(production)) {
                productionsWithoutCycleRules.add(production);
            }
        }

        Map<Nonterm, List<List<Symbol>>> prodMap = ProductionMapCreator.create(productionsWithoutCycleRules);
        List<CyclePair> cycleRules = getCycleRules(grammar);

        for (CyclePair cycleRule : cycleRules) {
            prodMap.get(cycleRule.getRightPart()).forEach(p -> {
                productionsWithoutCycleRules.add(
                        new Production()
                        .setLeftPart(cycleRule.getLeftPart())
                        .setRightPart(p));
            });
        }

        return grammar.setProductions(productionsWithoutCycleRules);
    }

    private static List<CyclePair> getCycleRules(Grammar grammar) {
        List<CyclePair> cyclePairsAll = new LinkedList<>();

        for (Nonterm nonterm : grammar.getNonterminalSymbols()) {
            cyclePairsAll.add(new CyclePair()
                    .withLeftPart(nonterm)
                    .withRightPart(nonterm));
        }

        List<CyclePair> cyclePairs = new LinkedList<>();
        for (Production production : grammar.getProductions()) {
            if (isChain(production)) {
                Stack<CyclePair> pairs = new Stack<>();
                pairs.addAll(cyclePairsAll);

                while (!pairs.isEmpty()) {
                    CyclePair cyclePair = pairs.pop();
                    if (cyclePair.getRightPart().equals(production.getLeftPart())) {
                        CyclePair newCyclePair = new CyclePair()
                                .withLeftPart(cyclePair.getLeftPart())
                                .withRightPart((Nonterm) production.getRightPart().get(0));

                        if (!cyclePairsAll.contains(newCyclePair)) {
                            cyclePairsAll.add(newCyclePair);
                            cyclePairs.add(newCyclePair);
                            pairs.push(newCyclePair);
                        }
                    }
                }
            }
        }

        return cyclePairs;
    }

    private static boolean isChain(Production production) {
        return production.getRightPart().size() == 1 && production.getRightPart().get(0) instanceof Nonterm;
    }
}
