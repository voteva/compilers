package executor;

import model.Epsilon;
import model.Grammar;
import model.Nonterm;
import model.Production;
import model.Symbol;
import util.ProductionMapCreator;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class LeftRecursionEliminator {

    public static Grammar eliminate(Grammar grammar) {
        EpsilonTransitionsEliminator.eliminate(grammar);
        ChainRulesEliminator.eliminate(grammar);

        Map<Nonterm, List<List<Symbol>>> prodMap = ProductionMapCreator.create(grammar.getProductions());

        List<Production> newProductions = new LinkedList<>();
        for (Nonterm prodMapKey : prodMap.keySet()) {
            List<List<Symbol>> recursiveRules = findRecursiveRulesForLeftPart(prodMapKey, prodMap.get(prodMapKey));
            List<List<Symbol>> nonrecursiveRules = findNonRecursiveRulesForLeftPart(prodMapKey, prodMap.get(prodMapKey));

            if (recursiveRules.isEmpty()) {
                for (List<Symbol> nonrecursiveRule : nonrecursiveRules) {
                    newProductions.add(new Production()
                            .setLeftPart(prodMapKey)
                            .setRightPart(nonrecursiveRule));
                }
                continue;
            }

            Nonterm newNonterm = new Nonterm(prodMapKey.getName() + "'");
            grammar.getNonterminalSymbols().add(newNonterm);

            for (List<Symbol> nonrecursiveRule : nonrecursiveRules) {
                nonrecursiveRule.add(nonrecursiveRule.size(), newNonterm);
                newProductions.add(new Production()
                        .setLeftPart(prodMapKey)
                        .setRightPart(nonrecursiveRule));
            }
            for (List<Symbol> recursiveRule : recursiveRules) {
                recursiveRule.add(recursiveRule.size(), newNonterm);
                recursiveRule.remove(0);
                newProductions.add(new Production()
                        .setLeftPart(newNonterm)
                        .setRightPart(recursiveRule));
            }
            recursiveRules.add(Collections.singletonList(Epsilon.getInstance()));
            newProductions.add(new Production()
                    .setLeftPart(newNonterm)
                    .setRightPart(Collections.singletonList(Epsilon.getInstance())));
        }

        return grammar.setProductions(newProductions);
    }

    private static List<List<Symbol>> findRecursiveRulesForLeftPart(Nonterm leftPart, List<List<Symbol>> rules) {
        return rules.stream()
                .filter(rule -> rule.get(0).equals(leftPart))
                .collect(Collectors.toList());
    }

    private static List<List<Symbol>> findNonRecursiveRulesForLeftPart(Nonterm leftPart, List<List<Symbol>> rules) {
        return rules.stream()
                .filter(rule -> !rule.get(0).equals(leftPart))
                .collect(Collectors.toList());
    }
}
