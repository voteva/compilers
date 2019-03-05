package creator;

import fa.Automata;
import fa.state.State;
import util.RegexUtils;
import util.TokenUtils;

import java.util.Collections;
import java.util.Stack;

public class NfaCreator {

    private int stateNum = 0;
    private Stack<Automata<State>> nfaMembersStack = new Stack<>();
    private Stack<Character> operatorStack = new Stack<>();

    public Automata<State> create(String regex) {
        beforeCreate();
        regex = RegexUtils.addConcat(regex);

        for (int i = 0; i < regex.length(); i++) {
            if (TokenUtils.isLiteral(regex.charAt(i))) {
                applyLiteral(regex.charAt(i));

            } else if (operatorStack.isEmpty()) {
                operatorStack.push(regex.charAt(i));

            } else if (regex.charAt(i) == '(') {
                operatorStack.push(regex.charAt(i));

            } else if (regex.charAt(i) == ')') {
                while (!operatorStack.isEmpty() && operatorStack.peek() != '(') {
                    applyToken(operatorStack.pop());
                }
                // pop the '('
                if (!operatorStack.isEmpty()) {
                    operatorStack.pop();
                }
            } else {
                while (!operatorStack.isEmpty() && priority(regex.charAt(i), operatorStack.peek())) {
                    applyToken(operatorStack.pop());
                }
                operatorStack.push(regex.charAt(i));
            }
        }

        // clean the remaining elements in the stack
        while (!operatorStack.isEmpty()) {
            applyToken(operatorStack.pop());
        }

        Automata<State> completeNfa = new Automata<>();
        if (!nfaMembersStack.isEmpty()) {
            completeNfa = nfaMembersStack.pop();
            completeNfa.getStates().getLast().setFinalState(true);
            completeNfa.setStartStates(Collections.singletonList(completeNfa.getStates().getFirst()));
        }

        return completeNfa;
    }

    private void beforeCreate() {
        stateNum = 0;
        nfaMembersStack.empty();
        operatorStack.empty();
    }

    private void applyLiteral(char token) {
        State stateFrom = new State(stateNum++);
        State stateTo = new State(stateNum++);

        stateFrom.addTransition(token, stateTo);

        Automata<State> nfaMember = new Automata<>();
        nfaMember.getStates().addLast(stateFrom);
        nfaMember.getStates().addLast(stateTo);

        nfaMembersStack.push(nfaMember);
    }

    private void applyToken(char operator) {
        switch (operator) {
            case ('|'):
                applyUnion();
                break;
            case ('.'):
                applyConcatenation();
                break;
            case ('*'):
                applyStar();
                break;
            case ('+'):
                applyPlus();
                break;
            default:
                throw new IllegalArgumentException("Incorrect operator: " + operator);
        }
    }

    private void applyStar() {
        if (nfaMembersStack.isEmpty()) return;

        Automata<State> nfa = nfaMembersStack.pop();

        State stateFrom = new State(stateNum++);
        State stateTo = new State(stateNum++);

        stateFrom.addTransition(TokenUtils.EPSILON_TOKEN, stateTo);
        stateFrom.addTransition(TokenUtils.EPSILON_TOKEN, nfa.getStates().getFirst());

        nfa.getStates().getLast().addTransition(TokenUtils.EPSILON_TOKEN, stateTo);
        nfa.getStates().getLast().addTransition(TokenUtils.EPSILON_TOKEN, nfa.getStates().getFirst());

        nfa.getStates().addFirst(stateFrom);
        nfa.getStates().addLast(stateTo);

        nfaMembersStack.push(nfa);
    }

    private void applyPlus() {
        if (nfaMembersStack.isEmpty()) return;

        Automata<State> nfa = nfaMembersStack.pop();

        State stateFrom = new State(stateNum++);
        State stateTo = new State(stateNum++);

        stateFrom.addTransition(TokenUtils.EPSILON_TOKEN, nfa.getStates().getFirst());

        nfa.getStates().getLast().addTransition(TokenUtils.EPSILON_TOKEN, stateTo);
        nfa.getStates().getLast().addTransition(TokenUtils.EPSILON_TOKEN, nfa.getStates().getFirst());

        nfa.getStates().addFirst(stateFrom);
        nfa.getStates().addLast(stateTo);

        nfaMembersStack.push(nfa);
    }

    private void applyConcatenation() {
        if (nfaMembersStack.size() < 2) return;

        Automata<State> nfa2 = nfaMembersStack.pop();
        Automata<State> nfa1 = nfaMembersStack.pop();

        nfa1.getStates().getLast().addTransition(TokenUtils.EPSILON_TOKEN, nfa2.getStates().getFirst());

        for (State state : nfa2.getStates()) {
            nfa1.getStates().addLast(state);
        }

        nfaMembersStack.push(nfa1);
    }

    private void applyUnion() {
        if (nfaMembersStack.size() < 2) return;

        Automata<State> nfa2 = nfaMembersStack.pop();
        Automata<State> nfa1 = nfaMembersStack.pop();

        State stateFrom = new State(stateNum++);
        State stateTo = new State(stateNum++);

        stateFrom.addTransition(TokenUtils.EPSILON_TOKEN, nfa1.getStates().getFirst());
        stateFrom.addTransition(TokenUtils.EPSILON_TOKEN, nfa2.getStates().getFirst());

        nfa1.getStates().getLast().addTransition(TokenUtils.EPSILON_TOKEN, stateTo);
        nfa2.getStates().getLast().addTransition(TokenUtils.EPSILON_TOKEN, stateTo);

        nfa1.getStates().addFirst(stateFrom);
        nfa2.getStates().addLast(stateTo);

        for (State state : nfa2.getStates()) {
            nfa1.getStates().addLast(state);
        }

        nfaMembersStack.push(nfa1);
    }

    private boolean priority(char token1, char token2) {
        if (token1 == token2) {
            return true;
        }
        if (token1 == '*' || token1 == '+') {
            return false;
        }
        if (token2 == '*' || token2 == '+') {
            return true;
        }
        if (token1 == '.') {
            return false;
        }
        if (token2 == '.') {
            return true;
        }
        if (token1 == '|') {
            return false;
        }
        return true;
    }
}
