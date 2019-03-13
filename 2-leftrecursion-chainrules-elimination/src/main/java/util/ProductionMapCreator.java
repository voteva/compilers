package util;

import model.Nonterm;
import model.Production;
import model.Symbol;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class ProductionMapCreator {

    public static Map<Nonterm, List<List<Symbol>>> create(List<Production> productions) {
        Map<Nonterm, List<List<Symbol>>> prodMap = new HashMap<>();

        for (Production production : productions) {
            List<List<Symbol>> prods;
            if (!prodMap.containsKey(production.getLeftPart())) {
                prods = new LinkedList<>();
            } else {
                prods = prodMap.get(production.getLeftPart());
            }

            prods.add(production.getRightPart());
            prodMap.put(production.getLeftPart(), prods);
        }

        return prodMap;
    }
}
