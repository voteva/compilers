import syntaxtree.*;
import visitor.GJDepthFirst;

import java.util.*;

public class ClassVisitor extends GJDepthFirst<String, String> {

    public ClassVisitor() {
        Main.classMap = new LinkedHashMap<>();
        Main.inheritanceMap = new LinkedHashMap<>();
    }

    /**
     * f0 -> "class"
     * f1 -> Identifier()
     * f2 -> "{"
     * f3 -> "public"
     * f4 -> "static"
     * f5 -> "void"
     * f6 -> "main"
     * f7 -> "("
     * f8 -> "String"
     * f9 -> "["
     * f10 -> "]"
     * f11 -> Identifier()
     * f12 -> ")"
     * f13 -> "{"
     * f14 -> ( VarDeclaration() )*
     * f15 -> ( Statement() )*
     * f16 -> "}"
     * f17 -> "}"
     */
    public String visit(MainClass n, String argu) {
        String _ret = null;
        String className = n.f1.accept(this, argu);
        Main.classMap.put(className, new SymbolMap(className));
        return _ret;
    }

    /**
     * f0 -> "class"
     * f1 -> Identifier()
     * f2 -> "{"
     * f3 -> ( VarDeclaration() )*
     * f4 -> ( MethodDeclaration() )*
     * f5 -> "}"
     */
	/*if class is already declared throw error 
		else insert it at classMap and inheritanceMap*/
    public String visit(ClassDeclaration n, String argu) {
        String _ret = null;
        String className = n.f1.accept(this, argu);

        if (Main.classMap.containsKey(className)) {
            throw new RuntimeException("Class Name:" + className + " already exists");
        } else {
            Main.classMap.put(className, new SymbolMap(className));
            Main.inheritanceMap.put(className, null);
        }
        return _ret;
    }

    /**
     * f0 -> "class"
     * f1 -> Identifier()
     * f2 -> "extends"
     * f3 -> Identifier()
     * f4 -> "{"
     * f5 -> ( VarDeclaration() )*
     * f6 -> ( MethodDeclaration() )*
     * f7 -> "}"
     */
    public String visit(ClassExtendsDeclaration n, String argu) {
        String _ret = null;
        String className = n.f1.accept(this, argu);
        String parentClassName = n.f3.accept(this, argu);
        if (Main.classMap.containsKey(className)) {
            throw new RuntimeException("Class Name:" + className + " already exists");
        } else {
            if (Main.classMap.containsKey(parentClassName)) {
                Main.classMap.put(className, new SymbolMap(className));
                Main.inheritanceMap.put(className, parentClassName);
            } else {
                throw new RuntimeException("Class " + className + " cannot extend:" + parentClassName + " because it doesn't exist");
            }

        }
        return _ret;
    }

    /**
     * f0 -> <IDENTIFIER>
     */
    public String visit(Identifier n, String argu) {
        n.f0.accept(this, argu);
        return n.f0.toString();
    }
}