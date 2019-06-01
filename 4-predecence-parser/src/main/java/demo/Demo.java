package demo;

import exception.SemanticException;
import exception.SyntaxException;

public class Demo {

    public static void main(String[] args) {
        DemoParser parser = new DemoParser(new Engine());

        while (parser.ready()) {
            // build tree
            Engine.Node tree;
            try {
                tree = parser.parse();
                if (tree == null) continue;
            } catch (SyntaxException e) {
                System.err.println(e);
                continue;
            }
            System.out.println("Tree: " + tree);

            // evaluate tree
            Engine.Node result;
            try {
                result = tree.eval();
            } catch (SemanticException e) {
                System.err.println(e);
                continue;
            }
            System.out.println(result);
        }
    }
}
