import syntaxtree.*;
import visitor.GJDepthFirst;

import java.util.Enumeration;
import java.util.Map;

public class TypeCheckVisitor extends GJDepthFirst<String, String> {
    private SymbolMap currentClass;
    private KotlinSymbol currentMethod;

    public TypeCheckVisitor() {
        currentClass = null;
        currentMethod = null;
    }

    public String visit(NodeListOptional n, String argu) {
        if (n.present()) {
            if (n.size() == 1)
                return n.elementAt(0).accept(this, argu);
            String _ret = null;
            int _count = 0;
            for (Enumeration<Node> e = n.elements(); e.hasMoreElements(); ) {
                if (_ret == null) {
                    _ret = e.nextElement().accept(this, argu);
                } else {
                    _ret = _ret + e.nextElement().accept(this, argu);
                }
                _count++;
            }
            return _ret;
        } else {
            return null;
        }
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
        String class_name = n.f1.accept(this, "NAME");
        currentClass = Main.classMap.get(class_name);
        currentMethod = currentClass.get("main", "METHOD", "local");
        if (currentMethod == null) {
            throw new RuntimeException("(@main)currentMethod == null");
        }
        /*MAIN METHOD BODY*/
        /*Typeckeck statements*/
        n.f15.accept(this, argu);
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
    public String visit(ClassDeclaration n, String argu) {
        String _ret = null;
        String class_name = n.f1.accept(this, "NAME");
        currentClass = Main.classMap.get(class_name);
        /*typeckeck methods' expressions*/
        n.f4.accept(this, argu);
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
        String class_name = n.f1.accept(this, "NAME");
        String parent_class_name = n.f3.accept(this, "NAME");
        currentClass = Main.classMap.get(class_name);
        /*typeckeck methods' expressions*/
        n.f6.accept(this, argu);
        return _ret;
    }


    /**
     * f0 -> "public"
     * f1 -> Type()
     * f2 -> Identifier()
     * f3 -> "("
     * f4 -> ( FormalParameterList() )?
     * f5 -> ")"
     * f6 -> "{"
     * f7 -> ( VarDeclaration() )*
     * f8 -> ( Statement() )*
     * f9 -> "return"
     * f10 -> Expression()
     * f11 -> ";"
     * f12 -> "}"
     */
    public String visit(MethodDeclaration n, String argu) {
        String _ret = null;
        String method_name = n.f2.accept(this, "NAME");
        currentMethod = currentClass.get(method_name, "METHOD", "local");
        if (currentMethod == null) {
            throw new RuntimeException("(@" + method_name + ")currentMethod == null");
        }
        /*typeckeck statement expressions*/
        n.f8.accept(this, argu);
        /*typeckeck return expression.*/
        String return_type = n.f10.accept(this, argu);
        if (!currentMethod.getType().equals(return_type)) {
            /*if not equal check for inheritance relationship*/
            if (Main.isParentClass(currentMethod.getType(), return_type) == false) {
                throw new RuntimeException("Returned value is of type " + return_type + " instead of " + currentMethod.getType() + " ( @->" + currentClass.getClassName() + "->" + currentMethod.getName() + "() )");
            }
        }
        return _ret;
    }


    /*************************************************Statements********************************************************/
    /**
     * f0 -> Identifier()
     * f1 -> "="
     * f2 -> Expression()
     * f3 -> ";"
     */
    public String visit(AssignmentStatement n, String argu) {
        String _ret = null;
        String id = n.f0.f0.toString();
        String lvalue_type = n.f0.accept(this, "VAR");
        String rvalue_type = n.f2.accept(this, argu);
        //System.out.println(lvalue_type);
        /*typeckeck for basic types*/
        if (lvalue_type.equals("int[]") || lvalue_type.equals("bool") || lvalue_type.equals("int")) {
            if (!lvalue_type.equals(rvalue_type)) {
                throw new RuntimeException("Incompatible type assignment.Symbol " + id + " is not of type " + rvalue_type + "::" + lvalue_type + "=" + rvalue_type + " ( @->" + currentClass.getClassName() + "->" + currentMethod.getName() + "() )");
            }
        }
        /*typeckeck for user defined types*/
        if (!lvalue_type.equals(rvalue_type)) {
            if (rvalue_type.equals("int[]") || rvalue_type.equals("bool") || rvalue_type.equals("int")) {
                throw new RuntimeException("Incompatible type assignment.Symbol " + id + " is not of type " + rvalue_type + "::" + lvalue_type + "=" + rvalue_type + " ( @->" + currentClass.getClassName() + "->" + currentMethod.getName() + "() )");
            }
            /*check for inheritance relationship*/
            boolean is_child = false;
            String rvalue_parent = Main.inheritanceMap.get(rvalue_type);
            while (rvalue_parent != null) {
                if (rvalue_parent.equals(lvalue_type)) {
                    is_child = true;
                }
                String temp = rvalue_parent;
                rvalue_parent = Main.inheritanceMap.get(temp);
            }
            if (is_child == false) {
                throw new RuntimeException("Incompatible type assignment.Symbol " + id + " is not of type " + rvalue_type + "::" + lvalue_type + "=" + rvalue_type + " ( @->" + currentClass.getClassName() + "->" + currentMethod.getName() + "() )");
            }
        }
        return _ret;
    }


    /**
     * f0 -> Identifier()
     * f1 -> "["
     * f2 -> Expression()
     * f3 -> "]"
     * f4 -> "="
     * f5 -> Expression()
     * f6 -> ";"
     */
    public String visit(ArrayAssignmentStatement n, String argu) {
        String _ret = null;
        String id = n.f0.f0.toString();
        String lvalue_type = n.f0.accept(this, "VAR");
        if (!lvalue_type.equals("int[]")) {
            throw new RuntimeException("Symbol " + id + " is not of type int[] (it's " + lvalue_type + " instead) ( @->" + currentClass.getClassName() + "->" + currentMethod.getName() + "() )");
        }

        String index_type = n.f2.accept(this, argu);
        if (!index_type.equals("int")) {
            throw new RuntimeException("Index of array " + id + " is not of type int (it's " + index_type + " instead)( @->" + currentClass.getClassName() + "->" + currentMethod.getName() + "() )");
        }

        String rvalue_type = n.f5.accept(this, argu);
        if (!rvalue_type.equals("int")) {
            throw new RuntimeException("Incompatible assignement to int array " + id + " (" + rvalue_type + " instead of int) ( @->" + currentClass.getClassName() + "->" + currentMethod.getName() + "() )");
        }

        return _ret;
    }

    /**
     * f0 -> "if"
     * f1 -> "("
     * f2 -> Expression()
     * f3 -> ")"
     * f4 -> Statement()
     * f5 -> "else"
     * f6 -> Statement()
     */
    public String visit(IfStatement n, String argu) {
        String _ret = null;
        String condition_type = n.f2.accept(this, argu);
        if (!condition_type.equals("bool")) {
            throw new RuntimeException("Incompatible type inside if condition (must be bool instead of " + condition_type + ") ( @->" + currentClass.getClassName() + "->" + currentMethod.getName() + "() )");
        }
        n.f4.accept(this, argu);
        n.f6.accept(this, argu);
        return _ret;
    }

    /**
     * f0 -> "while"
     * f1 -> "("
     * f2 -> Expression()
     * f3 -> ")"
     * f4 -> Statement()
     */
    public String visit(WhileStatement n, String argu) {
        String _ret = null;
        String condition_type = n.f2.accept(this, argu);
        if (!condition_type.equals("bool")) {
            throw new RuntimeException("Incompatible type inside if while (must be bool instead of " + condition_type + ") ( @->" + currentClass.getClassName() + "->" + currentMethod.getName() + "() )");
        }
        n.f4.accept(this, argu);
        return _ret;
    }

    /**
     * f0 -> "println"
     * f1 -> "("
     * f2 -> Expression()
     * f3 -> ")"
     * f4 -> ";"
     */
    public String visit(PrintStatement n, String argu) {
        String _ret = null;
        String expr_type = n.f2.accept(this, argu);
        if (!expr_type.equals("int")) {
            throw new RuntimeException("Incompatible type inside print statement (must be int instead of " + expr_type + ") ( @->" + currentClass.getClassName() + "->" + currentMethod.getName() + "() )");
        }
        return _ret;
    }


    /****************************************************Expressions**********************************************************/
    /**
     * f0 -> Clause()
     * f1 -> "&&"
     * f2 -> Clause()
     */
    public String visit(AndExpression n, String argu) {
        String _ret = null;
        String c1_type = n.f0.accept(this, argu);
        String c2_type = n.f2.accept(this, argu);
        if (!c1_type.equals("bool")) {
            throw new RuntimeException("Incompatible type: cannot perform AND operation on type " + c1_type + " ( @->" + currentClass.getClassName() + "->" + currentMethod.getName() + "() )");
        }
        if (!c2_type.equals("bool")) {
            throw new RuntimeException("Incompatible type: cannot perform AND operation on type " + c2_type + " ( @->" + currentClass.getClassName() + "->" + currentMethod.getName() + "() )");
        }
        return "bool";
    }


    /**
     * f0 -> "!"
     * f1 -> Clause()
     */
    public String visit(NotExpression n, String argu) {
        String _ret = null;
        String clause_type = n.f1.accept(this, argu);
        if (!clause_type.equals("bool")) {
            throw new RuntimeException("Incompatible type: cannot perform NOT operation on type " + clause_type + " ( @->" + currentClass.getClassName() + "->" + currentMethod.getName() + "() )");
        }
        return "bool";
    }


    /**
     * f0 -> PrimaryExpression()
     * f1 -> "<"
     * f2 -> PrimaryExpression()
     */
    public String visit(CompareExpression n, String argu) {
        String _ret = null;
        String expr1_type = n.f0.accept(this, argu);
        String expr2_type = n.f2.accept(this, argu);
        if (!expr1_type.equals("int")) {
            throw new RuntimeException("Incompatible type: cannot perform COMPARE operation on type " + expr1_type + " ( @->" + currentClass.getClassName() + "->" + currentMethod.getName() + "() )");
        }
        if (!expr2_type.equals("int")) {
            throw new RuntimeException("Incompatible type: cannot perform COMPARE operation on type " + expr2_type + " ( @->" + currentClass.getClassName() + "->" + currentMethod.getName() + "() )");
        }
        return "bool";
    }


    /**
     * f0 -> PrimaryExpression()
     * f1 -> "+"
     * f2 -> PrimaryExpression()
     */
    public String visit(PlusExpression n, String argu) {
        String _ret = null;
        String expr1_type = n.f0.accept(this, argu);
        String expr2_type = n.f2.accept(this, argu);
        if (!expr1_type.equals("int")) {
            throw new RuntimeException("Incompatible type: cannot perform PLUS operation on type " + expr1_type + " ( @->" + currentClass.getClassName() + "->" + currentMethod.getName() + "() )");
        }
        if (!expr2_type.equals("int")) {
            throw new RuntimeException("Incompatible type: cannot perform PLUS operation on type " + expr2_type + " ( @->" + currentClass.getClassName() + "->" + currentMethod.getName() + "() )");
        }
        return "int";
    }


    /**
     * f0 -> PrimaryExpression()
     * f1 -> "-"
     * f2 -> PrimaryExpression()
     */
    public String visit(MinusExpression n, String argu) {
        String _ret = null;
        String expr1_type = n.f0.accept(this, argu);
        String expr2_type = n.f2.accept(this, argu);
        if (!expr1_type.equals("int")) {
            throw new RuntimeException("Incompatible type: cannot perform MINUS operation on type " + expr1_type + " ( @->" + currentClass.getClassName() + "->" + currentMethod.getName() + "() )");
        }
        if (!expr2_type.equals("int")) {
            throw new RuntimeException("Incompatible type: cannot perform MINUS operation on type " + expr2_type + " ( @->" + currentClass.getClassName() + "->" + currentMethod.getName() + "() )");
        }
        return "int";
    }


    /**
     * f0 -> PrimaryExpression()
     * f1 -> "*"
     * f2 -> PrimaryExpression()
     */
    public String visit(TimesExpression n, String argu) {
        String _ret = null;
        String expr1_type = n.f0.accept(this, argu);
        String expr2_type = n.f2.accept(this, argu);
        if (!expr1_type.equals("int")) {
            throw new RuntimeException("Incompatible type: cannot perform TIMES operation on type " + expr1_type + " ( @->" + currentClass.getClassName() + "->" + currentMethod.getName() + "() )");
        }
        if (!expr2_type.equals("int")) {
            throw new RuntimeException("Incompatible type: cannot perform TIMES operation on type " + expr2_type + " ( @->" + currentClass.getClassName() + "->" + currentMethod.getName() + "() )");
        }
        return "int";
    }


    /**
     * f0 -> PrimaryExpression()
     * f1 -> "["
     * f2 -> PrimaryExpression()
     * f3 -> "]"
     */
    public String visit(ArrayLookup n, String argu) {
        String _ret = null;
        String expr1_type = n.f0.accept(this, "VAR");
        String expr2_type = n.f2.accept(this, argu);
        if (!expr1_type.equals("int[]")) {
            throw new RuntimeException("Incompatible type: cannot perform ARRAY LOOKUP operation on type " + expr1_type + " ( @->" + currentClass.getClassName() + "->" + currentMethod.getName() + "() )");
        }
        if (!expr2_type.equals("int")) {
            throw new RuntimeException("Incompatible type: cannot perform ARRAY LOOKUP operation on type " + expr2_type + " ( @->" + currentClass.getClassName() + "->" + currentMethod.getName() + "() )");
        }
        return "int";
    }


    /**
     * f0 -> PrimaryExpression()
     * f1 -> "."
     * f2 -> "length"
     */
    public String visit(ArrayLength n, String argu) {
        String _ret = null;
        String expr_type = n.f0.accept(this, argu);
        if (!expr_type.equals("int[]")) {
            throw new RuntimeException("Incompatible type: cannot perform ARRAY LENGHT operation on type " + expr_type + " ( @->" + currentClass.getClassName() + "->" + currentMethod.getName() + "() )");
        }
        return "int";
    }


    /**
     * f0 -> PrimaryExpression()
     * f1 -> "."
     * f2 -> Identifier()
     * f3 -> "("
     * f4 -> ( ExpressionList() )?
     * f5 -> ")"
     */
    public String visit(MessageSend n, String argu) {
        String _ret = null;
        String object_type = n.f0.accept(this, argu);
        if (object_type.equals("int") || object_type.equals("int[]") || object_type.equals("bool")) {
            throw new RuntimeException("Cannot call function " + n.f2.f0.toString() + "() on basic type " + object_type + " ( @->" + currentClass.getClassName() + "->" + currentMethod.getName() + "() )");
        }
        String method_ret_type = null;
        String method_type = n.f2.accept(this, "METHOD@" + object_type);
        String arguments = n.f4.accept(this, argu);
        if (method_type.contains("-")) {
            String[] token = method_type.split("-");
            method_ret_type = token[0];
            /*typecheck arguments*/
            String[] argu_token = token[1].split(",");
            String[] argu_token2 = arguments.split(",");
            if (argu_token.length != argu_token2.length) {
                throw new RuntimeException("Arguments passed to function " + n.f2.f0.toString() + "() dont match in number ( @->" + currentClass.getClassName() + "->" + currentMethod.getName() + "() )");
            }
            /*if some argument types dont match check whether there is an inheritance relationship between them*/
            for (int counter = 0; counter < argu_token.length; counter++) {
                if (!(argu_token[counter].equals(argu_token2[counter]))) {
                    if (Main.isParentClass(argu_token[counter], argu_token2[counter]) == false) {
                        throw new RuntimeException("Arguments passed to function " + n.f2.f0.toString() + "() dont match in type ( @->" + currentClass.getClassName() + "->" + currentMethod.getName() + "() )");
                    }
                }
            }
        } else {
            if (arguments != null) {
                throw new RuntimeException("Cannot call function " + n.f2.f0.toString() + "() with arguments ( @->" + currentClass.getClassName() + "->" + currentMethod.getName() + "() )");
            }
            method_ret_type = method_type;
        }
        return method_ret_type;
    }

    /***********************************************Primary Expressions************************************************/
    /**
     * f0 -> <IDENTIFIER>
     */
    public String visit(Identifier n, String argu) {
        n.f0.accept(this, argu);
        String id = n.f0.toString();
        if (argu == null || argu.equals("VAR")) {
            /*search for variable with name id and return type*/
            /*first search id in local variables.*/
            Map<String, KotlinSymbol> method_map = currentMethod.getMethodMap();
            if (method_map.containsKey(id)) {
                return method_map.get(id).getType();
            }
            /*If not found search class fields*/
            KotlinSymbol field = currentClass.get(id, "F_VAR", "local");
            if (field != null) {
                return field.getType();
            }
            /*If not found at class fields search inherited class fields*/
            field = currentClass.get(id, "F_VAR", "inherited");
            if (field != null) {
                return field.getType();
            }
            /*If not found throw error*/
            throw new RuntimeException("Cannot find symbol " + id + "( @->" + currentClass.getClassName() + "->" + currentMethod.getName() + "() )");
        }

        if (argu.contains("METHOD")) {
            /*search for method with name id and return type+parameters*/
            /*first search id in methods declared in  class.*/
            String[] argu_token = argu.split("@");
            SymbolMap wanted_class = Main.classMap.get(argu_token[1]);
            KotlinSymbol method = wanted_class.get(id, "METHOD", "local");
            if (method != null) {
                String params = method.parametersToString();
                if (params != null) {
                    return method.getType() + "-" + params;
                } else {
                    return method.getType();
                }
            }
            /*If not found at class,search inherited methods*/
            method = wanted_class.get(id, "METHOD", "inherited");
            if (method != null) {
                String params = method.parametersToString();
                if (params != null) {
                    return method.getType() + "-" + params;
                } else {
                    return method.getType();
                }
            }
            /*If not found throw error*/
            throw new RuntimeException("Cannot find method " + id + "( @->" + currentClass.getClassName() + "->" + currentMethod.getName() + "() )");
        }

        if (argu.equals("NAME")) {
            /*return identifier name*/
            return id;
        }
        return null;
    }


    /**
     * f0 -> <INTEGER_LITERAL>
     */
    public String visit(IntegerLiteral n, String argu) {
        n.f0.accept(this, argu);
        return "int";
    }


    /**
     * f0 -> "true"
     */
    public String visit(TrueLiteral n, String argu) {
        n.f0.accept(this, argu);
        return "bool";
    }


    /**
     * f0 -> "false"
     */
    public String visit(FalseLiteral n, String argu) {
        n.f0.accept(this, argu);
        return "bool";
    }


    /**
     * f0 -> "this"
     */
    public String visit(ThisExpression n, String argu) {
        n.f0.accept(this, argu);
        return currentClass.getClassName();
    }


    /**
     * f0 -> "new"
     * f1 -> "int"
     * f2 -> "["
     * f3 -> Expression()
     * f4 -> "]"
     */
    public String visit(ArrayAllocationExpression n, String argu) {
        String _ret = null;
        if (argu != null) {
            if (argu.equals("VAR")) {
                throw new RuntimeException(" Cannot have array allocation on array lookup expression");
            }
        }
        String index_type = n.f3.accept(this, argu);
        if (!index_type.equals("int")) {
            throw new RuntimeException(" Allocation size of array is not of type int (it's " + index_type + " instead)( @->" + currentClass.getClassName() + "->" + currentMethod.getName() + "() )");
        }
        return "int[]";

    }


    /**
     * f0 -> "new"
     * f1 -> Identifier()
     * f2 -> "("
     * f3 -> ")"
     */
    public String visit(AllocationExpression n, String argu) {
        String _ret = null;
        String class_name = n.f1.accept(this, "NAME");
        if (!Main.classMap.containsKey(class_name)) {
            throw new RuntimeException("Type " + class_name + " does not exist( @->" + currentClass.getClassName() + "->" + currentMethod.getName() + "() )");
        }
        return class_name;
    }

    /**
     * f0 -> "("
     * f1 -> Expression()
     * f2 -> ")"
     */
    public String visit(BracketExpression n, String argu) {
        String _ret = null;
        _ret = n.f1.accept(this, argu);
        return _ret;
    }

    /**
     * f0 -> Expression()
     * f1 -> ExpressionTail()
     */
    public String visit(ExpressionList n, String argu) {
        String _ret = null;
        _ret = n.f0.accept(this, argu);
        String tail = n.f1.accept(this, argu);
        if (tail != null) {
            _ret = _ret + tail;
        }
        return _ret;
    }

    /**
     * f0 -> ( ExpressionTerm() )*
     */
    public String visit(ExpressionTail n, String argu) {
        return n.f0.accept(this, argu);
    }

    /**
     * f0 -> ","
     * f1 -> Expression()
     */
    public String visit(ExpressionTerm n, String argu) {
        String _ret = null;
        _ret = "," + n.f1.accept(this, argu);
        return _ret;
    }
}