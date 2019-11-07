import syntaxtree.*;
import visitor.GJDepthFirst;

import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class SymbolVisitor extends GJDepthFirst<String, String> {
    private static String currentClass;
    private KotlinSymbol currentMethod;

    public SymbolVisitor() {
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
        /*MAIN CLASS HEAD*/
        String _ret = null;
        currentClass = n.f1.accept(this, argu);
        /*MAIN METHOD HEAD*/
        String param_name = n.f11.accept(this, argu);
        SymbolMap sym_map = Main.classMap.get(currentClass);
        String ret_type = "public static fun";
        String method_name = "main";
        List<String> param_list = new LinkedList<String>();
        param_list.add("String[] " + param_name);
        KotlinSymbol sym = new KotlinSymbol(method_name, "METHOD", ret_type, param_list, 0);
        sym_map.insert(method_name, sym);
        /*MAIN'S BODY*/
        currentMethod = sym;
        n.f14.accept(this, "method_declaration");
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
        currentClass = n.f1.accept(this, argu);
        /*get FIELDS of class*/
        n.f3.accept(this, "class_declaration");
        /*get METHODS of class*/
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
        currentClass = n.f1.accept(this, argu);
        /*get symbol map of class*/
        SymbolMap sym_map = Main.classMap.get(currentClass);
        String parentClassName = n.f3.accept(this, argu);
        SymbolMap parent_sym_map = Main.classMap.get(parentClassName);
        /*set offsets for extended class*/
        sym_map.setFieldOffset(parent_sym_map.getFieldOffset());
        sym_map.setMethodOffset(parent_sym_map.getMethodOffset());
        /*get FIELDS of current class*/
        n.f5.accept(this, "class_declaration_ext");
        /*get visible FIELDS of superclasses*/
        /*fill symbol map with fields from superclass*/
        Set<Map.Entry<String, List<KotlinSymbol>>> entry_set = parent_sym_map.entrySet();
        for (Map.Entry<String, List<KotlinSymbol>> entry : entry_set) {
            List<KotlinSymbol> value = entry.getValue();
            for (KotlinSymbol inherited_symbol : value) {
                if (!(inherited_symbol.getCategory().equals("METHOD"))) {
                    /*if not shadowed*/
                    if (!sym_map.contains(inherited_symbol.getName())) {
                        String inh_field_name = inherited_symbol.getName();
                        String key = null;
                        /*if variable is already inherited  to parent class from a superclass*/
                        if (entry.getKey().contains(".")) {
                            key = entry.getKey();
                            KotlinSymbol sym = new KotlinSymbol(inh_field_name, inherited_symbol.getCategory(), inherited_symbol.getType(), inherited_symbol.getParamList(), inherited_symbol.getOffset());
                            sym_map.insert(key, sym);
                        } else {
                            key = parentClassName + "." + inherited_symbol.getName();
                            KotlinSymbol sym = new KotlinSymbol(inh_field_name, inherited_symbol.getCategory(), inherited_symbol.getType(), inherited_symbol.getParamList(), inherited_symbol.getOffset());
                            sym_map.insert(key, sym);
                        }
                    }
                }
            }
        }
        /*get METHODS of current class*/
        n.f6.accept(this, argu);
        /*get visible METHODS of superclasses*/
        for (Map.Entry<String, List<KotlinSymbol>> entry : entry_set) {
            List<KotlinSymbol> value = entry.getValue();
            /*for each method of parent*/
            for (KotlinSymbol inherited_symbol : value) {
                if ((inherited_symbol.getCategory().equals("METHOD"))) {
                    /*if name doesnt exists in curent class(not shadowed)*/
                    if (!sym_map.contains(inherited_symbol.getName())) {
                        String inh_method_name = inherited_symbol.getName();
                        /*if inherited method is main do not insert in symbol table*/
                        if (inh_method_name.equals("main")) {
                            continue;
                        }
                        String key = null;
                        /*if method is already inherited to parent class from a superclass*/
                        if (entry.getKey().contains(".")) {
                            key = entry.getKey();
                            KotlinSymbol sym = new KotlinSymbol(inh_method_name, inherited_symbol.getCategory(), inherited_symbol.getType(), inherited_symbol.getParamList(), inherited_symbol.getOffset());
                            sym_map.insert(key, sym);
                        } else {
                            key = parentClassName + "." + inherited_symbol.getName();
                            KotlinSymbol sym = new KotlinSymbol(inh_method_name, inherited_symbol.getCategory(), inherited_symbol.getType(), inherited_symbol.getParamList(), inherited_symbol.getOffset());
                            sym_map.insert(key, sym);
                        }
                    }
					/*if name already exists in curent class
					check if it belongs to field or method or both*/
                    else {
                        List<KotlinSymbol> sym_list = sym_map.get(inherited_symbol.getName());
                        boolean method_exists = false;
                        KotlinSymbol symbol = null;
                        for (KotlinSymbol sym : sym_list) {
                            if (sym.getCategory().equals("METHOD")) {
                                method_exists = true;
                                symbol = sym;
                            }
                        }
                        /*if name belongs to field ,method from superclass is not shadowed*/
                        if (method_exists == false) {
                            String inh_method_name = inherited_symbol.getName();
                            String key = null;
                            /*if method is already inherited to parent class from a superclass*/
                            if (entry.getKey().contains(".")) {
                                key = entry.getKey();
                                KotlinSymbol sym = new KotlinSymbol(inh_method_name, inherited_symbol.getCategory(), inherited_symbol.getType(), inherited_symbol.getParamList(), inherited_symbol.getOffset());
                                sym_map.insert(key, sym);
                            } else {
                                key = parentClassName + "." + inherited_symbol.getName();
                                KotlinSymbol sym = new KotlinSymbol(inh_method_name, inherited_symbol.getCategory(), inherited_symbol.getType(), inherited_symbol.getParamList(), inherited_symbol.getOffset());
                                sym_map.insert(key, sym);
                            }
                        }
                        /*if name belongs to a method ,method from superclass is shadowed*/
                        /*check if overriden correctly*/
                        else {
                            if (symbol.methodIsEqual(inherited_symbol) == false) {
                                throw new RuntimeException("Method " + symbol.getName() + "() of class " + currentClass + " does not override parent " + symbol.getName() + "() properly");
                            }
                            symbol.setOffset(inherited_symbol.getOffset());
                        }
                    }
                }
            }
        }
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
        /*METHOD HEAD*/
        String ret_type = n.f1.accept(this, argu);
        String method_name = n.f2.accept(this, argu);
        String paramList = n.f4.accept(this, argu);
        /*get symbol map of class*/
        SymbolMap sym_map = Main.classMap.get(currentClass);
        /*do the required name checks and add to symbol map if everything is OK*/
        /*check if return type is valid type)*/
        if (!ret_type.equals("int[]") && !ret_type.equals("bool") && !ret_type.equals("int")) {
            if (!Main.classMap.containsKey(ret_type)) {
                throw new RuntimeException("Return type " + ret_type + " of method " + method_name + " does not exist");
            }
        }
        /*check if there is a method with the same name in the class(a field can have the same name)*/
        if (sym_map.contains(method_name)) {
            List<KotlinSymbol> sym_list = sym_map.get(method_name);
            for (KotlinSymbol s : sym_list) {
                if (s.getCategory().equals("METHOD")) {
                    throw new RuntimeException("Method name " + method_name + " already exists");
                }
            }
        }
        /*everything OK*/
        KotlinSymbol sym = null;
        if (paramList != null) {
            List<String> pl = new LinkedList<String>();
            String[] paramArray = paramList.split(",");
            for (String param : paramArray) {
                pl.add(param);
            }
            sym = new KotlinSymbol(method_name, "METHOD", ret_type, pl, sym_map.getMethodOffset());
            //System.out.println(paramList);
        } else {
            //no parameters->null paramList
            sym = new KotlinSymbol(method_name, "METHOD", ret_type, null, sym_map.getMethodOffset());
        }
        sym_map.insert(method_name, sym);
        /*increase method offset of class*/
        if (!methodOverrrides(sym)) {
            sym_map.setMethodOffset(sym_map.getMethodOffset() + 8);
        }
        /*METHOD BODY*/
        currentMethod = sym;
        n.f7.accept(this, "method_declaration");
        n.f8.accept(this, argu);

        n.f10.accept(this, argu);

        return _ret;
    }

    private static boolean methodOverrrides(KotlinSymbol method) {
        String parentClassName = Main.inheritanceMap.get(currentClass);
        if (parentClassName == null) {
            return false;
        }
        /*get parent class's symbol table*/
        SymbolMap parent_sym_map = Main.classMap.get(parentClassName);
        /*for each entry in parent's symbol table*/
        Set<Map.Entry<String, List<KotlinSymbol>>> parent_entry_set = parent_sym_map.entrySet();
        for (Map.Entry<String, List<KotlinSymbol>> parent_entry : parent_entry_set) {
            List<KotlinSymbol> value = parent_entry.getValue();
            /*for each method of parent*/
            for (KotlinSymbol inherited_symbol : value) {
                if ((inherited_symbol.getCategory().equals("METHOD"))) {
                    if (inherited_symbol.getName().equals(method.getName())) {
                        return true;
                    }
                }
            }

        }
        return false;
    }

    /**
     * f0 -> FormalParameter()
     * f1 -> FormalParameterTail()
     */
    public String visit(FormalParameterList n, String argu) {
        String _ret = null;
        _ret = n.f0.accept(this, argu);
        String tail = n.f1.accept(this, argu);
        if (tail != null) {
            _ret = _ret + tail;
        }
        return _ret;
    }

    /**
     * f0 -> Type()
     * f1 -> Identifier()
     */
    public String visit(FormalParameter n, String argu) {
        String _ret = null;
        String type = n.f0.accept(this, argu);
        String id = n.f1.accept(this, argu);
        /*check type corectness*/
        if (!type.equals("int[]") && !type.equals("bool") && !type.equals("int")) {
            /*if not a basic type check whether type exists*/
            if (!Main.classMap.containsKey(type)) {
                throw new RuntimeException("Type " + type + " of argument " + id + " does not exist");
            }
        }
        _ret = type + " " + id;
        return _ret;
    }

    /**
     * f0 -> ( FormalParameterTerm() )*
     */
    public String visit(FormalParameterTail n, String argu) {
        return n.f0.accept(this, argu);
    }

    /**
     * f0 -> ","
     * f1 -> FormalParameter()
     */
    public String visit(FormalParameterTerm n, String argu) {
        String _ret = null;
        _ret = "," + n.f1.accept(this, argu);
        return _ret;
    }

    /**
     * f0 -> Type()
     * f1 -> Identifier()
     * f2 -> ";"
     */
    public String visit(VarDeclaration n, String argu) {
        String _ret = null;
        String type = n.f0.accept(this, argu);
        String id = n.f1.accept(this, argu);
        SymbolMap sym_map = Main.classMap.get(currentClass);
        /*check type corectness*/
        if (!type.equals("int[]") && !type.equals("bool") && !type.equals("int")) {
            if (!Main.classMap.containsKey(type)) {
                /*if not a basic type check whether type exists*/
                throw new RuntimeException("Type " + type + " of variable " + id + " does not exist");
            }
        }
        /*var declarations inside METHOD BODY*/
        if (argu.equals("method_declaration")) {
            currentMethod.insertLocalVar(id, type);
        }
        /*var declarations inside CLASS BODY*/
        else {
            /*check name availability(methods are not inserted yet to symbol map)*/
            if (sym_map.contains(id)) {
                throw new RuntimeException("Variable name " + id + " already exists");
            } else {
                /*everything OK*/
                KotlinSymbol sym = new KotlinSymbol(id, "F_VAR", type, null, sym_map.getFieldOffset());
                sym_map.insert(id, sym);
                /*increase field offset of class accordingly*/
                int size = 0;
                if (type.equals("int")) {
                    size = 4;
                } else if (type.equals("bool")) {
                    size = 1;
                } else {
                    size = 8;
                }
                sym_map.setFieldOffset(sym_map.getFieldOffset() + size);

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

    /**
     * f0 -> "int"
     * f1 -> "["
     * f2 -> "]"
     */
    public String visit(ArrayType n, String argu) {
        n.f0.accept(this, argu);
        n.f1.accept(this, argu);
        n.f2.accept(this, argu);
        return n.f0.toString() + n.f1.toString() + n.f2.toString();
    }

    /**
     * f0 -> "bool"
     */
    public String visit(BooleanType n, String argu) {
        n.f0.accept(this, argu);
        return n.f0.toString();
    }

    /**
     * f0 -> "int"
     */
    public String visit(IntegerType n, String argu) {
        n.f0.accept(this, argu);
        return n.f0.toString();
    }
}

