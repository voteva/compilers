import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

class KotlinSymbol {

    private String name;
    private String category;    //METHOD,F_VAR,M_VAR,M_PAR
    private String type;        //boolean,int,int[],identifier,public static void(main)
    private List<String> paramList;
    private Map<String, KotlinSymbol> methodVarMap; //map for local variables of method
    private int offset;
    private String vtableString;

    public KotlinSymbol(String n, String c, String t, List<String> pl, int offs) {
        name = n;
        category = c;
        type = t;
        paramList = pl;
        offset = offs;
        if (category.equals("METHOD")) {
            methodVarMap = new LinkedHashMap<>();
            if (paramList != null) {
                for (String param : paramList) {
                    String[] token = param.split("\\s+");
                    if (methodVarMap.containsKey(token[1])) {
                        throw new RuntimeException("Parameter name:" + token[1] + " of method " + name + " already exists");
                    }
                    KotlinSymbol method_param = new KotlinSymbol(token[1], "M_PAR", token[0], null, 0);
                    methodVarMap.put(token[1], method_param);
                }
            }
        }
    }

    public String getVtableString() {
        return vtableString;
    }

    public void setVtableString(String s) {
        vtableString = s;
    }

    public Map<String, KotlinSymbol> getMethodMap() {
        return methodVarMap;
    }

    public String getName() {
        return name;
    }

    public String getCategory() {
        return category;
    }

    public String getType() {
        return type;
    }

    public List<String> getParamList() {
        return paramList;
    }

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offs) {
        offset = offs;
    }

    public void insertLocalVar(String n, String t) {
        if (methodVarMap.containsKey(n)) {
            throw new RuntimeException("Redeclaration of local variable:" + n + " of method " + name);
        }
        KotlinSymbol local_var = new KotlinSymbol(n, "M_VAR", t, null, 0);
        methodVarMap.put(n, local_var);
    }

    public String parametersToString() {
        String ret_value = "";
        if (paramList == null) {
            return null;
        }
        for (String p : paramList) {
            String[] token = p.split("\\s+");
            if (ret_value.length() == 0) {
                ret_value += token[0];
            } else {
                ret_value += ("," + token[0]);
            }
        }
        return ret_value;
    }


    public boolean methodIsEqual(KotlinSymbol comp) {
        if (name.equals(comp.getName()) && type.equals(comp.getType())) {
            if ((paramList == null) && (comp.getParamList() == null)) {
                return true;
            } else if (((paramList == null) && (comp.getParamList() != null)) || ((paramList != null) && (comp.getParamList() == null))) {
                return false;
            } else {
                if (paramList.size() != comp.getParamList().size()) {
                    return false;
                } else {
                    for (int counter = 0; counter < paramList.size(); counter++) {
                        String[] token = paramList.get(counter).split("\\s+");
                        String[] token2 = comp.getParamList().get(counter).split("\\s+");
                        if (!(token[0].equals(token2[0]))) {
                            return false;
                        }
                    }
                    return true;
                }
            }
        }
        return false;
    }


    public void print() {
        if (paramList == null) {
            if (category.equals("METHOD")) {
                System.out.print("Symbol:" + name + "()/" + category + "/" + type + "/" + offset + " | ");
                for (Map.Entry<String, KotlinSymbol> local_var : methodVarMap.entrySet()) {
                    System.out.print("\n\t");
                    local_var.getValue().print();
                }
            } else {
                System.out.print("Symbol:" + name + "/" + category + "/" + type + "/" + offset + " | ");
            }

        } else {
            System.out.print("Symbol:" + name + "()/" + category + "/" + type + "/" + offset + "(");
            for (String p : paramList) {
                System.out.print(p + " ");
            }
            System.out.print(")");
            for (Map.Entry<String, KotlinSymbol> local_var : methodVarMap.entrySet()) {
                System.out.print("\n\t");
                local_var.getValue().print();
            }
        }

    }

    /*Comparator for sorting KotlinSymbol by offset*/
    public static Comparator<KotlinSymbol> offsetComparator = new Comparator<KotlinSymbol>() {

        public int compare(KotlinSymbol m1, KotlinSymbol m2) {

            int off1 = m1.getOffset();
            int off2 = m2.getOffset();

            /*For ascending order*/
            return off1 - off2;

            /*For descending order*/
            //rollno2-rollno1;
        }
    };
}
