import syntaxtree.*;
import visitor.GJDepthFirst;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;

/*
prints corresponding LLVM code to outputFile for each node visited
*/
public class IRvisitor extends GJDepthFirst<String, String> {
    private SymbolMap currentClass;
    private KotlinSymbol currentMethod;
    private PrintWriter outputFile;
    private int varCounter;//reset at each function
    private int labelCounter;//reset at each function
    private int indentCounter;

    /*prints s on outputFile*/
    private void emit(String s) {
        outputFile.print(this.print_indent() + s);
    }

    /*returns string with as many tabs as indentCounter*/
    private String print_indent() {
        String ret_value = "";
        for (int i = 0; i < indentCounter; i++) {
            ret_value += "\t";
        }
        return ret_value;
    }

    /*returns a new LLVM parameter*/
    private String getVar() {
        String newVar = "%_" + varCounter;
        varCounter++;
        return newVar;
    }

    /*returns a new label*/
    private String getLabel(String type) {
        String newLabel = type + labelCounter;
        labelCounter++;
        return newLabel;
    }

    /*convert type to corresponding LLVM type*/
    private static String typeToLLVM(String type) {
        if (type.equals("bool")) {
            return "i1";
        } else if (type.equals("int")) {
            return "i32";
        } else if (type.equals("int[]")) {
            return "i32*";
        } else {
            return "i8*";
        }
    }

    /*convert list of parameter types to corresponding LLVM types*/
    private static String parameterTypeToLLVM(String argList) {
        if (argList == null) {
            return null;
        }
        String ret_value = "";
        String[] types = argList.split(",");
        for (String type : types) {
            if (ret_value.length() == 0) {
                ret_value += typeToLLVM(type);
            } else {
                ret_value += ("," + typeToLLVM(type));
            }
        }
        return ret_value;
    }

    /*constructor prints vtable declarations and standard LLVM code to .ll file*/
    public IRvisitor(PrintWriter pw) {
        /*initialize fields*/
        currentMethod = null;
        currentClass = null;
        outputFile = pw;
        varCounter = 0;
        labelCounter = 0;
        indentCounter = 0;
        /*create vtables(one per class)*/
        int number_of_classes = Main.classMap.size();
        String[] vtable_decl = new String[number_of_classes];//table with vtable declaration for each class
        int class_counter = 0;
        /*for each class create a string for its declaration*/
        for (Map.Entry<String, SymbolMap> cur_Class : Main.classMap.entrySet()) {
            String class_name = cur_Class.getKey();
            if (class_counter == 0) {
                vtable_decl[class_counter] = "@." + class_name + "_vtable = global [0 x i8*] []\n";
                class_counter++;
                continue;
            }
            vtable_decl[class_counter] = "@." + class_name + "_vtable = global ";
            int method_counter = 0;
            SymbolMap sym_table = cur_Class.getValue();
            List<KotlinSymbol> method_list = new ArrayList<KotlinSymbol>();//list of class's methods
            /*for each method in class create a substring to insert to the vtable declaration*/
            for (Map.Entry<String, List<KotlinSymbol>> sym_table_entry : sym_table.entrySet()) {
                String symbol_name = sym_table_entry.getKey();
                List<KotlinSymbol> symbol_list = sym_table_entry.getValue();
                for (KotlinSymbol symbol : symbol_list) {
                    /*if symbol is a method*/
                    if (symbol.getCategory().equals("METHOD")) {
                        /*fix name of symbol*/
                        if (!symbol_name.contains(".")) {
                            symbol_name = class_name + "." + symbol_name;
                        }
                        method_counter++;
                        /*convert return type to corresponding LLVM type*/
                        String ret_type = typeToLLVM(symbol.getType());
                        /*same with parameters' types*/
                        String parameter_type_list = "(i8*";
                        String rest = parameterTypeToLLVM(symbol.parametersToString());
                        if (rest != null) {
                            parameter_type_list += ("," + rest + ")*");
                        } else {
                            parameter_type_list += ")*";
                        }
                        /*set method's substring and add it to method_list*/
                        symbol.setVtableString("i8* bitcast (" + ret_type + " " + parameter_type_list + " @" + symbol_name + " to i8*)");
                        method_list.add(symbol);
                    }
                }
            }
            vtable_decl[class_counter] += ("[" + method_counter + " x i8*] [ ");
            sym_table.setMethodCounter(method_counter);
			/*insert each method's substring to the vtable declaration string
			 (in order determined by each method's offset)*/
            Collections.sort(method_list, KotlinSymbol.offsetComparator);
            int subst_counter = 0;
            for (KotlinSymbol method : method_list) {
                if ((subst_counter + 1) == method_counter) {
                    vtable_decl[class_counter] += (method.getVtableString());
                } else {
                    vtable_decl[class_counter] += (method.getVtableString() + " , ");
                }
                subst_counter++;
            }
            vtable_decl[class_counter] += " ]\n";
            class_counter++;
        }
        /*print vtable declaration for each class*/
        for (String temp : vtable_decl) {
            emit(temp);
        }
        emit("\n\n");
        /*print stantard code*/
        emit("declare i8* @calloc(i32, i32)\n"
                + "declare i32 @printf(i8*, ...)\n"
                + "declare void @exit(i32)\n"
                + "\n"
                + "@_cint = constant [4 x i8] c\"%d\\0a\\00\"\n"
                + "@_cOOB = constant [15 x i8] c\"Out of bounds\\0a\\00\"\n"
                + "define void @print_int(i32 %i) {\n"
                + "\t%_str = bitcast [4 x i8]* @_cint to i8*\n"
                + "\tcall i32 (i8*, ...) @printf(i8* %_str, i32 %i)\n"
                + "\tret void\n"
                + "}\n"
                + "\n"
                + "define void @throw_oob() {\n"
                + "\t%_str = bitcast [15 x i8]* @_cOOB to i8*\n"
                + "\tcall i32 (i8*, ...) @printf(i8* %_str)\n"
                + "\tcall void @exit(i32 1)\n"
                + "\tret void\n"
                + "}\n\n\n\n");
    }

    public String visit(NodeListOptional n, String argu) {
        if (n.present()) {
            if (n.size() == 1)
                return n.elementAt(0).accept(this, "rvalue");
            String _ret = null;
            int _count = 0;
            for (Enumeration<Node> e = n.elements(); e.hasMoreElements(); ) {
                if (_ret == null) {
                    _ret = e.nextElement().accept(this, "rvalue");
                } else {
                    _ret = _ret + e.nextElement().accept(this, "rvalue");
                }
                _count++;
            }
            return _ret;
        } else {
            return null;
        }
    }

    public String visit(MainClass n, String argu) {
        String _ret = null;
        /*update current method and class*/
        currentClass = Main.classMap.get(n.f1.f0.toString());
        currentMethod = currentClass.get("main", "METHOD", "local");
        /*Method definition*/
        emit("define i32 @main() {\n");
        indentCounter++;
        /*Local variables declaration*/
        emit(";Local Variables Declaration\n");
        Map<String, KotlinSymbol> local_variable_map = currentMethod.getMethodMap();
        for (Map.Entry<String, KotlinSymbol> local_variable : local_variable_map.entrySet()) {
            String name = local_variable.getKey();
            String LLVM_type = typeToLLVM(local_variable.getValue().getType());
            if (local_variable.getValue().getCategory().equals("M_VAR")) {
                emit("%" + name + " = alloca " + LLVM_type + "\n");
            }
        }
        /*statements*/
        emit(";Statements\n");
        n.f15.accept(this, argu); //statements
        /*return expression*/
        emit("ret i32 0\n");
        indentCounter--;
        emit("}\n\n\n");
        return _ret;
    }

    public String visit(ClassDeclaration n, String argu) {
        String _ret = null;
        /*update current class*/
        currentClass = Main.classMap.get(n.f1.f0.toString());
        /*METHODS*/
        n.f4.accept(this, argu);
        return _ret;
    }

    public String visit(ClassExtendsDeclaration n, String argu) {
        String _ret = null;
        /*update current class*/
        currentClass = Main.classMap.get(n.f1.f0.toString());
        String parentClass = n.f3.f0.toString();
        /*METHODS*/
        n.f6.accept(this, argu);
        return _ret;
    }

    public String visit(MethodDeclaration n, String argu) {
        String _ret = null;
        /*reset var counter and label counter*/
        varCounter = 0;
        labelCounter = 0;
        /*update current method*/
        String method_name = n.f2.f0.toString();
        currentMethod = currentClass.get(method_name, "METHOD", "local");

        /*LLVM Method Definition */
        String LLVM_definition_string = "";
        String LLVM_return_type = typeToLLVM(currentMethod.getType());
        String current_class_name = currentClass.getClassName();
        List<String> parameter_list = currentMethod.getParamList();
        String[] LLVM_parameter_list = null;
        if (parameter_list != null) {
            LLVM_parameter_list = new String[parameter_list.size()];
            int counter = 0;
            for (String parameter : parameter_list) {
                String[] token = parameter.split("\\s+");
                LLVM_parameter_list[counter] = " , " + typeToLLVM(token[0]) + " %." + token[1];
                counter++;
            }
        }
        LLVM_definition_string += ("define " + LLVM_return_type + " @" + current_class_name + "." + method_name + "(i8* %this");
        if (LLVM_parameter_list != null) {
            for (String LLVM_parameter : LLVM_parameter_list) {
                LLVM_definition_string += LLVM_parameter;
            }
        }
        LLVM_definition_string += ") {\n";
        emit(LLVM_definition_string);
        indentCounter++;
        emit(";Parameter Initialization\n");
        /*Parameter initialization*/
        if (parameter_list != null) {
            for (String parameter : parameter_list) {
                String[] token = parameter.split("\\s+");
                String LLVM_type = typeToLLVM(token[0]);
                String param_name = token[1];
                emit("%" + token[1] + " = alloca " + LLVM_type + "\n");
                emit("store " + LLVM_type + " %." + param_name + ", " + LLVM_type + "* %" + param_name + "\n");
            }
        }
        /*Local variables declaration*/
        emit(";Local Variables Declaration\n");
        Map<String, KotlinSymbol> local_variable_map = currentMethod.getMethodMap();
        for (Map.Entry<String, KotlinSymbol> local_variable : local_variable_map.entrySet()) {
            String name = local_variable.getKey();
            String LLVM_type = typeToLLVM(local_variable.getValue().getType());
            if (local_variable.getValue().getCategory().equals("M_VAR")) {
                emit("%" + name + " = alloca " + LLVM_type + "\n");
            }
        }
        /*statements*/
        emit(";Statements\n");
        n.f8.accept(this, argu);
        /*return instruction*/
        String ret_value_reg = n.f10.accept(this, "rvalue");
        emit("ret " + LLVM_return_type + " " + ret_value_reg + "\n");
        indentCounter--;
        emit("}\n\n\n");
        return _ret;
    }

    /*********************************Statements******************************/

    public String visit(AssignmentStatement n, String argu) {
        String _ret = null;
        /*get lvalue*/
        String lvalue = n.f0.accept(this, "lvalue");
        String[] token = lvalue.split("-");
        String lvalue_LLVM_type = token[0];
        String lvalue_reg = token[1];
        /*get rvalue*/
        String rvalue_reg = n.f2.accept(this, "rvalue");
        emit("store " + lvalue_LLVM_type + " " + rvalue_reg + " , " + lvalue_LLVM_type + "* " + lvalue_reg + "\n");
        return _ret;
    }

    public String visit(ArrayAssignmentStatement n, String argu) {
        String _ret = null;
        /*get array ptr(i32**)*/
        String lvalue = n.f0.accept(this, "lvalue");
        String[] token = lvalue.split("-");
        String array_ptr = token[1];
        /*get size of array*/
        String array = getVar();
        String size = getVar();
        emit(array + " = load i32* , i32** " + array_ptr + "\n");
        emit(size + " = load i32 , i32* " + array + "\n");
        /*get index*/
        String index = n.f2.accept(this, "rvalue");
        /*compare index and size */
        String comp = getVar();
        emit(comp + " = icmp ult i32 " + index + " , " + size + "\n");
        String ok_label = getLabel("array_assigment");
        String error_label = getLabel("array_assigment_oob");
        String cont_label = getLabel("array_assigment_continue");
        emit("br i1 " + comp + " , label %" + ok_label + " , label %" + error_label + "\n");
        /*store value at index*/
        outputFile.print(ok_label + ":\n");
        String value = n.f5.accept(this, "rvalue");
        String temp = getVar();
        emit(temp + " = add i32 " + index + " , 1\n");
        String cell = getVar();
        emit(cell + " = getelementptr i32 , i32* " + array + " , i32 " + temp + "\n");
        emit("store i32 " + value + " , i32* " + cell + "\n");
        emit("br label %" + cont_label + "\n");
        /*error code block*/
        outputFile.print(error_label + ":\n");
        emit("call void @throw_oob()\n");
        emit("br label %" + cont_label + "\n");
        /*continue code block*/
        outputFile.print(cont_label + ":\n");
        return _ret;
    }

    public String visit(IfStatement n, String argu) {
        String _ret = null;
        /*get register containing  condition value*/
        String condition_value = n.f2.accept(this, "rvalue");
        String if_label = getLabel("if");
        String else_label = getLabel("else");
        String continue_label = getLabel("if_else_continue");
        /*branch instruction*/
        emit("br i1 " + condition_value + " , label %" + if_label + " , label %" + else_label + "\n");
        outputFile.print(if_label + ":\n");
        /*generate instructions for statements inside if*/
        indentCounter++;
        n.f4.accept(this, argu);
        emit("br label %" + continue_label + "\n");
        outputFile.print(else_label + ":\n");
        /*generate instruction for statements inside else*/
        n.f6.accept(this, argu);
        emit("br label %" + continue_label + "\n");
        indentCounter--;
        outputFile.print(continue_label + ":\n");
        return _ret;
    }

    public String visit(WhileStatement n, String argu) {
        String _ret = null;
        String condition_label = getLabel("while_cond_eval");
        String while_label = getLabel("while_body");
        String continue_label = getLabel("while_continue");
        emit("br label %" + condition_label + "\n");
        outputFile.print(condition_label + ":\n");
        /*get register containing  condition value*/
        String condition_value = n.f2.accept(this, "rvalue");
        emit("br i1 " + condition_value + " , label %" + while_label + " , label %" + continue_label + "\n");
        /*while body*/
        outputFile.print(while_label + ":\n");
        indentCounter++;
        n.f4.accept(this, argu);
        emit("br label %" + condition_label + "\n");
        indentCounter--;
        /*end*/
        outputFile.print(continue_label + ":\n");
        return _ret;
    }

    public String visit(PrintStatement n, String argu) {
        String _ret = null;
        String print_reg = n.f2.accept(this, "rvalue");
        emit("call void (i32) @print_int(i32 " + print_reg + ")\n");
        return _ret;
    }

    /*********************************Exressions*****************************/

    public String visit(AndExpression n, String argu) {
        String op1_reg = n.f0.accept(this, "rvalue");
        String op2_reg = n.f2.accept(this, "rvalue");
        String result_reg = getVar();
        emit(result_reg + " = and i1 " + op1_reg + " , " + op2_reg + "\n");
        return result_reg;
    }

    public String visit(NotExpression n, String argu) {
        String op_reg = n.f1.accept(this, "rvalue");
        String result_reg = getVar();
        emit(result_reg + " = xor i1 1 , " + op_reg + "\n");
        return result_reg;
    }

    public String visit(CompareExpression n, String argu) {
        String op1_reg = n.f0.accept(this, "rvalue");
        String op2_reg = n.f2.accept(this, "rvalue");
        String result_reg = getVar();
        emit(result_reg + " = icmp slt i32 " + op1_reg + " , " + op2_reg + "\n");
        return result_reg;
    }

    public String visit(PlusExpression n, String argu) {
        String op1_reg = n.f0.accept(this, "rvalue");
        String op2_reg = n.f2.accept(this, "rvalue");
        String result_reg = getVar();
        emit(result_reg + " = add i32 " + op1_reg + " , " + op2_reg + "\n");
        return result_reg;
    }

    public String visit(MinusExpression n, String argu) {
        String op1_reg = n.f0.accept(this, "rvalue");
        String op2_reg = n.f2.accept(this, "rvalue");
        String result_reg = getVar();
        emit(result_reg + " = sub i32 " + op1_reg + " , " + op2_reg + "\n");
        return result_reg;
    }

    public String visit(TimesExpression n, String argu) {
        String op1_reg = n.f0.accept(this, "rvalue");
        String op2_reg = n.f2.accept(this, "rvalue");
        String result_reg = getVar();
        emit(result_reg + " = mul i32 " + op1_reg + " , " + op2_reg + "\n");
        return result_reg;
    }

    public String visit(ArrayLookup n, String argu) {
        /*get register containing array's address*/
        String array_addr_reg = n.f0.accept(this, "rvalue");
        /*get register containing index*/
        String index_reg = n.f2.accept(this, "rvalue");
        /*compare size of array with index for oob check*/
        String array_size_reg = getVar();
        emit(array_size_reg + " = load i32, i32* " + array_addr_reg + "\n");
        String oob_check_reg = getVar();
        emit(oob_check_reg + " = icmp ult i32 " + index_reg + " , " + array_size_reg + "\n");
        String error_label = getLabel("arr_lookup_oob");
        String ok_label = getLabel("arr_lookup");
        String continue_label = getLabel("arr_lookup_continue");
        emit("br i1 " + oob_check_reg + " , label %" + ok_label + " , label %" + error_label + "\n");
        /*array lookup code block*/
        outputFile.print(ok_label + ":\n");
        String temp = getVar();
        emit(temp + " = add i32 " + index_reg + " , 1\n");
        String index_ptr = getVar();
        emit(index_ptr + " = getelementptr i32, i32* " + array_addr_reg + " , i32 " + temp + "\n");
        String value = getVar();
        emit(value + " = load i32, i32* " + index_ptr + "\n");
        emit("br label %" + continue_label + "\n");
        /*error code block*/
        outputFile.print(error_label + ":\n");
        emit("call void @throw_oob()\n");
        emit("br label %" + continue_label + "\n");
        /*continue code block*/
        outputFile.print(continue_label + ":\n");
        /*return wanted value*/
        return value;
    }

    public String visit(ArrayLength n, String argu) {
        /*get register containing array's address*/
        String array_addr_reg = n.f0.accept(this, "rvalue");
        String array_size_reg = getVar();
        emit(array_size_reg + " = load i32 , i32* " + array_addr_reg + "\n");
        /*return register containing array's size(i32)*/
        return array_size_reg;
    }

    public String visit(MessageSend n, String argu) {
        /*get pointer to object*/
        String obj = n.f0.accept(this, "rvalue+");
        String[] token = obj.split(",");
        String obj_type = token[0];
        //System.out.println(currentClass.getClassName()+"->"+currentMethod.getName()+"->"+n.f2.f0.toString());
        String obj_ptr = token[1];
        /*get method's name and,offset,return type,types of paremeters*/
        String method_name = n.f2.f0.toString();
        SymbolMap sym_table = Main.classMap.get(obj_type);
        KotlinSymbol method = sym_table.get(method_name, "METHOD", "local");
        if (method == null) {
            method = sym_table.get(method_name, "METHOD", "inherited");
        }
        int method_offset = method.getOffset();
        int vtable_position = method_offset / 8;
        String method_ret_type = typeToLLVM(method.getType());
        String parameter_type_list = "(i8*";
        String rest = parameterTypeToLLVM(method.parametersToString());
        if (rest != null) {
            parameter_type_list += ("," + rest + ")*");
        } else {
            parameter_type_list += ")*";
        }
        /*get registers containing values of parameters*/
        String arg_registers = n.f4.accept(this, argu);
        String arguments = "";
        if (rest != null) {
            String[] token1 = rest.split(",");
            String[] token2 = arg_registers.split(",");
            int count = 0;
            for (String tk : token1) {
                arguments += ("," + tk + " " + token2[count]);
                count++;
            }
        }
        String temp = getVar();
        emit(temp + "= bitcast i8* " + obj_ptr + " to i8***\n");
        String vtable_ptr = getVar();
        emit(vtable_ptr + "= load i8**, i8*** " + temp + "\n");
        String temp2 = getVar();
        emit(temp2 + "  = getelementptr i8*, i8** " + vtable_ptr + " , i32 " + vtable_position + "\n");
        String method_ptr = getVar();
        emit(method_ptr + " = load i8*, i8** " + temp2 + "\n");
        String temp3 = getVar();
        emit(temp3 + " = bitcast i8* " + method_ptr + " to " + method_ret_type + " " + parameter_type_list + "\n");
        String ret_value = getVar();
        emit(ret_value + " = call " + method_ret_type + " " + temp3 + "(i8* " + obj_ptr + " " + arguments + ")\n");
        if (argu.contains("+")) {
            return (method.getType()) + "," + ret_value;
        } else {
            return ret_value;
        }
    }

    public String visit(ExpressionList n, String argu) {
        String _ret = null;
        _ret = n.f0.accept(this, "rvalue");
        String tail = n.f1.accept(this, argu);
        if (tail != null) {
            _ret = _ret + tail;
        }
        return _ret;
    }

    public String visit(ExpressionTail n, String argu) {
        return n.f0.accept(this, argu);
    }

    public String visit(ExpressionTerm n, String argu) {
        String _ret = null;
        _ret = "," + n.f1.accept(this, "rvalue");
        return _ret;
    }

    /*****************************Primary Expressions***********************/

    public String visit(IntegerLiteral n, String argu) {
        String new_var = getVar();
        emit(new_var + " = add i32 0, " + n.f0.toString() + "\n");
        return new_var;
    }

    public String visit(TrueLiteral n, String argu) {
        String new_var = getVar();
        emit(new_var + " = and i1 1 , 1\n");
        return new_var;
    }

    public String visit(FalseLiteral n, String argu) {
        String new_var = getVar();
        emit(new_var + " = and i1 0 , 0\n");
        return new_var;
    }

    public String visit(ArrayAllocationExpression n, String argu) {
        emit(";Array Allocation\n");
        /*receive register containing array's allocation size*/
        String size_reg = n.f3.accept(this, "rvalue");
        String oob_check_reg = getVar();
        /*instruction to check if allocation size<0*/
        emit(oob_check_reg + " = icmp slt i32 " + size_reg + " , 0\n");
        String error_label = getLabel("arr_alloc_oob");
        String ok_label = getLabel("arr_alloc");
        /*go to corresponding label*/
        emit("br i1 " + oob_check_reg + " , label %" + error_label + " , label %" + ok_label + "\n");
        /*error code block*/
        outputFile.print(error_label + ":\n");
        emit("call void @throw_oob()\n");
        emit("br label %" + ok_label + "\n");
        /*array allocation code block*/
        outputFile.print(ok_label + ":\n");
        String new_size_reg = getVar();
        emit(new_size_reg + " = add i32 " + size_reg + " , 1\n");
        String temp = getVar();
        emit(temp + " = call i8* @calloc(i32 4, i32 " + new_size_reg + " )\n");
        String array_addr_reg = getVar();
        emit(array_addr_reg + " = bitcast i8* " + temp + " to i32*\n");
        /*store array size at the start of the array*/
        emit("store i32 " + size_reg + " , i32* " + array_addr_reg + "\n");
        /*return register containing array's address(i32*)*/
        return array_addr_reg;
    }

    public String visit(BracketExpression n, String argu) {
        String _ret = null;
        _ret = n.f1.accept(this, argu);
        return _ret;
    }

    public String visit(Identifier n, String argu) {
        String id = n.f0.toString();
        if (argu == null) {
            return id;
        }
		/*identifier as lvalue->return address
		  identifier as rvalue->return value*/
        if (argu.contains("lvalue")) {
            /*return register containing address of identifier's value */
            /*id is either local variable or field or inherited field*/
            /*first search id in local variables.*/
            Map<String, KotlinSymbol> method_map = currentMethod.getMethodMap();
            if (method_map.containsKey(id)) {
                String LLVM_type = typeToLLVM(method_map.get(id).getType());
                return LLVM_type + "-%" + id;
            }
            /*If not found search class fields*/
            KotlinSymbol field = currentClass.get(id, "F_VAR", "local");
            /*If not found at class fields search inherited class fields*/
            if (field == null) {
                field = currentClass.get(id, "F_VAR", "inherited");
            }
            String LLVM_type = typeToLLVM(field.getType());
            int offset = field.getOffset() + 8;
            String field_ptr = getVar();
            emit(field_ptr + " = getelementptr i8, i8* %this, i32 " + offset + "\n");
            String temp = getVar();
            emit(temp + " = bitcast i8* " + field_ptr + " to " + LLVM_type + "*\n");
            return LLVM_type + "-" + temp;
        } else if (argu.contains("rvalue")) {
            /*return register containing value of identifier*/
            /*id is either local variable or field or inherited field*/
            /*first search id in local variables.*/
            Map<String, KotlinSymbol> method_map = currentMethod.getMethodMap();
            if (method_map.containsKey(id)) {
                String LLVM_type = typeToLLVM(method_map.get(id).getType());
                String value = getVar();
                emit(value + " = load " + LLVM_type + " , " + LLVM_type + "* %" + id + "\n");
                if (argu.contains("+")) {
                    return (method_map.get(id).getType()) + "," + value;
                } else {
                    return value;
                }
            }
            /*If not found search class fields*/
            KotlinSymbol field = currentClass.get(id, "F_VAR", "local");
            /*If not found at class fields search inherited class fields*/
            if (field == null) {
                field = currentClass.get(id, "F_VAR", "inherited");
            }
            String LLVM_type = typeToLLVM(field.getType());
            int offset = field.getOffset() + 8;
            String field_ptr = getVar();
            emit(field_ptr + " = getelementptr i8, i8* %this, i32 " + offset + "\n");
            String temp = getVar();
            emit(temp + " = bitcast i8* " + field_ptr + " to " + LLVM_type + "*\n");
            String value = getVar();
            emit(value + " = load " + LLVM_type + " , " + LLVM_type + "* " + temp + "\n");
            if (argu.contains("+")) {
                return (field.getType()) + "," + value;
            } else {
                return value;
            }
        } else {
            return id;
        }
    }

    public String visit(AllocationExpression n, String argu) {
        String object_type = n.f1.f0.toString();
        /*allocate space for object*/
        int object_size = Main.classMap.get(object_type).getFieldOffset();
        int vtable_size = Main.classMap.get(object_type).getMethodCounter();
        object_size += 8;
        String temp = getVar();
        emit(temp + " = call i8* @calloc(i32 1,i32 " + object_size + ")\n");
        /*bitcast pointer*/
        String obj_ptr = getVar();
        emit(obj_ptr + " = bitcast i8* " + temp + " to i8***\n");
        /*get vtable pointer*/
        String vtable_ptr = getVar();
        emit(vtable_ptr + " = getelementptr [" + vtable_size + " x i8*], [" + vtable_size + " x i8*]* @." + object_type + "_vtable, i32 0, i32 0\n");
        /*store vtable address at the start of object*/
        emit("store i8** " + vtable_ptr + " , i8*** " + obj_ptr + "\n");
        /*return pointer to new object(i8*) */
        if (argu.contains("+")) {
            return object_type + "," + temp;
        } else {
            return temp;
        }
    }

    public String visit(ThisExpression n, String argu) {
        if (argu.contains("+")) {
            return (currentClass.getClassName()) + ",%this";
        } else {
            return "%this";
        }
    }
}

