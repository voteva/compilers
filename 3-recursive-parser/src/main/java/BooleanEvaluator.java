import ast.BooleanExpression;
import lexer.Lexer;
import parser.RecursiveDescentParser;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

class BooleanEvaluator {
    private static final String FILE_ENCODING = "UTF-8";

    static void evaluateFromFile(String filePath) {
        String expression = getContentFromFile(filePath);

        evaluateExpression(expression);
    }

    static void evaluateExpression(String expression) {
        Lexer lexer = new Lexer(new ByteArrayInputStream(expression.getBytes()));
        RecursiveDescentParser parser = new RecursiveDescentParser(lexer);
        BooleanExpression ast = parser.build();

        System.out.println(String.format("EXP: %s", expression));
        System.out.println(String.format("AST: %s\n", ast));
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
}
