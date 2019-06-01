package demo;

import parser.SyntaxError;

public class Kalq2 {

    public static String[] exprs = {
            "true & false",
            "!true & (true | !false)",
            "true & (true & !false)"
    };

    public static void main(String[] args) {
        Parser2 parser = new Parser2(new Engine());
        Engine.Node tree = null;

        for (String expr : exprs) {
            // parse string
            try {
                tree = parser.parse(expr);
            } catch (SyntaxError e) {
                System.err.println(e);
                System.exit(0);
            }
            System.out.println("Tree: " + tree);

            // evaluate tree
            Engine.Node result = null;
            try {
                result = tree.eval();
            } catch (SemanticError e) {
                System.err.println(e);
                System.exit(0);
            }
            System.out.println("Result: " + result + "\n");
        }
    }
}
