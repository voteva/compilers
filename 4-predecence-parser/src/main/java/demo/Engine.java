package demo;

import java.util.HashMap;
import java.util.Map;

public class Engine {

    Map<String, Sym> symbols;
    Context context;

    public Engine() {
        symbols = new HashMap<String, Sym>();
        context = new Context(null);
    }

    public static String itemsToString(Node[] items, String delim) {
        StringBuffer s = new StringBuffer();
        int i = 0;
        while (i < items.length) {
            s = s.append(items[i].toString());
            if (i < items.length - 1)
                s = s.append(delim);
            i++;
        }
        return s.toString();
    }

    public Node makeSym(String val) {
        if (symbols.containsKey(val))
            return symbols.get(val);
        Sym sym = new Sym(val);
        symbols.put(val, sym);
        return sym;
    }

    public Node makeBool(String val) {
        if (symbols.containsKey(val))
            return symbols.get(val);
        Sym sym = new Sym(val);
        symbols.put(val, sym);
        return sym;
    }

    public void push() {
        this.context = new Context(this.context);
    }

    // ***** Symbols *****

    public void pop() {
        this.context = this.context.parent;
    }

    public class Node {
        public Node eval() throws SemanticError {
            return this;
        }

        public Node doUnaryOp(String op) throws SemanticError {
            throw new SemanticError(String.format("Operation '%s' not implemented.", op));
        }

        public Node doBinOp(String op, Node other) throws SemanticError {
            throw new SemanticError(String.format("Operation '%s' not implemented.", op));
        }

        public boolean isTrue() {
            return true;
        }
    }

    public class Bool extends Node {
        boolean val;

        public Bool(boolean val) {
            super();
            this.val = val;
        }

        @Override
        public String toString() {
            return Boolean.toString(val);
        }

        @Override
        public Node doUnaryOp(String op) throws SemanticError {
            if ("!".equals(op)) return new Bool(!this.val);
            return super.doUnaryOp(op);
        }

        @Override
        public Node doBinOp(String op, Node other) throws SemanticError {
            Bool that = (Bool) other;
            if ("&".equals(op)) {
                return new Bool(val && that.val);
            }
            if ("|".equals(op)) {
                return new Bool(val || that.val);
            }
            return super.doBinOp(op, other);
        }

        @Override
        public boolean isTrue() {
            return this.val;
        }
    }

    public class UnaryOp extends Node {
        String op;
        Node operand;

        public UnaryOp(String op, Node operand) {
            super();
            this.op = op;
            this.operand = operand;
        }

        @Override
        public String toString() {
            return "(" + op + " " + operand.toString() + ")";
        }

        @Override
        public Node eval() throws SemanticError {
            return operand.eval().doUnaryOp(op);
        }
    }

    public class BinOp extends Node {
        String op;
        Node left;
        Node right;

        public BinOp(String op, Node left, Node right) {
            super();
            this.op = op;
            this.left = left;
            this.right = right;
        }

        @Override
        public String toString() {
            return "(" + left.toString() + op + right.toString() + ")";
        }

        @Override
        public Node eval() throws SemanticError {
            Node l = ("=".equals(op)) ? left : left.eval();
            Node r = right.eval();
            return l.doBinOp(op, r);
        }
    }

    public class Sym extends Node {
        String val;

        public Sym(String val) {
            super();
            this.val = val;
        }

        @Override
        public String toString() {
            return val;
        }

        @Override
        public Node eval() throws SemanticError {
            if (this.val.equals("true")) {
                return new Bool(true);
            }

            if (this.val.equals("false")) {
                return new Bool(false);
            }

            Node val = context.get(this);
            if (val == null) {
                throw new SemanticError(String.format("Undefined variable '%s'", this));
            }

            return val;
        }

        @Override
        public Node doBinOp(String op, Node other) throws SemanticError {
            if ("=".equals(op))
                return context.set(this, other);
            return super.doBinOp(op, other);
        }
    }

    // Context

    public class Context {
        Context parent;
        Map<Sym, Node> vars;

        public Context(Context parent) {
            this.parent = parent;
            this.vars = new HashMap<Sym, Node>();
        }

        public Node get(Sym sym) {
            if (vars.containsKey(sym))
                return vars.get(sym);
            if (parent != null)
                return parent.get(sym);
            return null;
        }

        public Node set(Sym sym, Node val) {
            vars.put(sym, val);
            return val;
        }
    }

    public class Composite extends Node {
        Sym head;
        Node[] args;

        public Composite(Sym head, Node[] args) {
            this.head = head;
            this.args = args;
        }

        @Override
        public String toString() {
            return this.head + "(" + itemsToString(args, ",") + ")";
        }

        @Override
        public Node eval() throws SemanticError {
            Node node = context.get(this.head);
            if (!(node instanceof Func))
                throw new SemanticError(String.format("'%s' is not a function.", this.head));
            Func func = (Func) node;
            if (func.args.length != this.args.length)
                throw new SemanticError("Argument mismatch.");
            Context newctx = new Context(context);
            for (int i = 0; i < func.args.length; i++)
                newctx.set(((Sym) func.args[i]), args[i].eval());
            Node result;
            Engine.this.context = newctx;
            try {
                result = func.body.eval();
            } finally {
                pop();
            }
            return result;
        }
    }

    // **** Constructor *****

    public class Func extends Node {
        protected Sym head;
        protected Node[] args;
        protected Node body;

        public Func(Sym head, Node[] args, Node body) {
            this.head = head;
            this.args = args;
            this.body = body;
        }

        @Override
        public Node eval() throws SemanticError {
            return context.set(head, this);
        }

        @Override
        public String toString() {
            return "fun " + head + "(" + itemsToString(args, ",") + ") is " + body + " end";
        }
    }
}
