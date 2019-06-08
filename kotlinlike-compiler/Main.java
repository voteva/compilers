import syntaxtree.*;
import visitor.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

class Main {

    public static Map<String, SymbolMap> classMap;
    public static Map<String, String> inheritanceMap;

    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("Usage: java [MainClassName] [file1]");
            System.exit(1);
        }
        /*perform semantic analysis on all files given as arguments*/
        FileInputStream fis = null;
        for (int i = 0; i < args.length; i++) {
            try {
                /*syntax analysis of file*/
                fis = new FileInputStream(args[i]);
                KotlinParser parser = new KotlinParser(fis);
                Goal root = parser.Goal();
                System.out.println("Program parsed successfully.");

                /*semantic analysis(call all required visitors)*/
                /*class visitor*/
                ClassVisitor cv = new ClassVisitor();
                root.accept(cv, null);

                /*Symbol visitor*/
                SymbolVisitor sv = new SymbolVisitor();
                root.accept(sv, null);

                /*Type Check visitor*/
                TypeCheckVisitor tcv = new TypeCheckVisitor();
                root.accept(tcv, null);
                System.out.println("Program is semantically correct.");

                /*Intermediate code generation visitor*/
                String[] tokens = args[i].split("/");
                String filename = tokens[tokens.length - 1].split("\\.")[0];
                File newFile = new File("./output/" + filename + ".ll");
                System.out.println("Generating LLVM code @ ./output/" + filename + ".ll");
                PrintWriter output = new PrintWriter(newFile);
                IRvisitor irv = new IRvisitor(output);
                root.accept(irv, null);
                output.flush();
                output.close();
            } catch (ParseException ex) {
                System.out.println(ex.getMessage());
            } catch (FileNotFoundException ex) {
                System.err.println(ex.getMessage());
            } catch (RuntimeException ex) {
                System.err.println(ex.getMessage());
            } finally {
                try {
                    if (fis != null) fis.close();
                } catch (IOException ex) {
                    System.err.println(ex.getMessage());
                }
            }
        }
    }

    public static boolean isParentClass(String c1, String c2) {
        if (c1.equals("int") || c1.equals("int[]") || c1.equals("bool") || c2.equals("int") || c2.equals("int[]") || c2.equals("bool")) {
            return false;
        }
        String parentClassName = inheritanceMap.get(c2);
        while (parentClassName != null) {
            if (parentClassName.equals(c1)) {
                return true;
            }
            String temp = parentClassName;
            parentClassName = inheritanceMap.get(temp);
        }
        return false;
    }

    public static void print() {
        System.out.println("Class Map:");
        for (String key : classMap.keySet()) {
            SymbolMap value = classMap.get(key);
            value.print();
        }
    }
}
