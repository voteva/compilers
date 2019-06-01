package demo;

import parser.SyntaxError;

public class Kalq2repl {

    public static void main(String[] args) {
        Parser2 parser = new Parser2(new Engine());
        while (parser.ready()) {
            Engine.Node tree;
            try {
                tree = parser.parse();
                if (tree == null) continue;
            } catch (SyntaxError e) {
                System.err.println(e);
                continue;
            }
            System.out.println("Tree: " + tree);
            // evaluate tree
            Engine.Node result;
            try {
                result = tree.eval();
            } catch (SemanticError e) {
                System.err.println(e);
                continue;
            }
            // print result
            System.out.println(result);
        }
    }

}
