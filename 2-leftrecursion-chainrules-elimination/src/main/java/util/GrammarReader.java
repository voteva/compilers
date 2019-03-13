package util;

import com.jayway.jsonpath.JsonPath;
import model.Epsilon;
import model.Grammar;
import model.Nonterm;
import model.Production;
import model.Symbol;
import model.Term;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class GrammarReader {
    private static final String FILE_ENCODING = "UTF-8";

    public static Grammar readFromFile(String path) {
        String contentString = getContentFromFile(path);

        return new Grammar()
                .setTerminalSymbols(parseTerminalSymbols(contentString))
                .setNonterminalSymbols(parseNonterminalSymbols(contentString))
                .setProductions(parseProductions(contentString))
                .setStartSymbol(parseStartSymbol(contentString));
    }

    private static String getContentFromFile(String path) {
        try {
            File file = new File(path);
            FileInputStream fis = new FileInputStream(file);
            byte[] data = new byte[(int) file.length()];

            fis.read(data);
            fis.close();

            return new String(data, FILE_ENCODING);
        } catch (FileNotFoundException e) {
            throw new RuntimeException("File not found");
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    private static Map<String, String> parseTerminalSymbols(String contentString) {
        List<Map<String, Object>> terms = JsonPath.parse(contentString)
                .read("$.grammar.terminalsymbols.term");

        Map<String, String> terminals = new HashMap<>();
        terms.forEach(s -> terminals.put(
                s.get("-name").toString(),
                s.get("-spell").toString()));

        terminals.put(Epsilon.getInstance().getName(), ((Term) Epsilon.getInstance()).getSpell());

        return terminals;
    }

    private static List<Nonterm> parseNonterminalSymbols(String contentString) {
        List<Map<String, Object>> nonterms = JsonPath.parse(contentString)
                .read("$.grammar.nonterminalsymbols.nonterm");

        return nonterms.stream()
                .map(s -> new Nonterm(s.get("-name").toString()))
                .collect(Collectors.toList());
    }

    private static List<Production> parseProductions(String contentString) {
        List<Map<String, Object>> productions = JsonPath.parse(contentString)
                .read("$.grammar.productions.production");

        return productions.stream()
                .map(s -> new Production()
                        .setLeftPart(parseLeftPart(s))
                        .setRightPart(parseRightPart(s)))
                .collect(Collectors.toList());
    }

    private static Nonterm parseLeftPart(Map<String, Object> content) {
        return new Nonterm(JsonPath.parse(content).read("$.lhs.-name").toString());
    }

    private static List<Symbol> parseRightPart(Map<String, Object> content) {
        List<Map<String, Object>> symbols = JsonPath.parse(content).read("$.rhs.symbol");

        return symbols.stream()
                .map(s -> {
                    if ("term".equals(s.get("-type").toString())) {
                        return new Term(s.get("-name").toString());
                    } else {
                        return new Nonterm(s.get("-name").toString());
                    }
                })
                .collect(Collectors.toList());
    }

    private static Nonterm parseStartSymbol(String contentString) {
        Map<String, Object> startSymbol = JsonPath.parse(contentString)
                .read("$.grammar.startsymbol");

        return Optional.ofNullable(startSymbol)
                .map(s -> new Nonterm(s.get("-name").toString()))
                .orElse(null);
    }
}
