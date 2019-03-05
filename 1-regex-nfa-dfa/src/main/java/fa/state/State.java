package fa.state;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class State {

    private int stateNum;
    private boolean isFinalState;
    private Map<Character, List<State>> transitions;

    public State(int stateNum) {
        this.stateNum = stateNum;
        this.transitions = new HashMap<>();
    }

    public void addTransition(char token, State nextState) {
        addTransitions(token, Collections.singletonList(nextState));
    }

    public void addTransitions(char token, List<State> nextStates) {
        List<State> tokenTransitions = this.transitions.get(token);

        if (tokenTransitions == null) {
            tokenTransitions = new ArrayList<>();
        }

        tokenTransitions.addAll(nextStates);
        this.transitions.put(token, tokenTransitions);
    }

    public List<State> getTransitions(char c) {
        if (this.transitions.get(c) == null) {
            return Collections.emptyList();
        }
        return this.transitions.get(c);
    }

    public void removeTransitions(char c) {
        if (this.transitions.get(c) != null) {
            this.transitions.remove(c);
        }
    }

    public int getStateNum() {
        return stateNum;
    }

    public void setStateNum(int stateNum) {
        this.stateNum = stateNum;
    }

    public boolean isFinalState() {
        return isFinalState;
    }

    public void setFinalState(boolean finalState) {
        isFinalState = finalState;
    }

    public Map<Character, List<State>> getTransitions() {
        return transitions;
    }

    public void setTransitions(Map<Character, List<State>> transitions) {
        this.transitions = transitions;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (!State.class.isAssignableFrom(obj.getClass())) {
            return false;
        }
        final State other = (State) obj;
        return this.stateNum == other.stateNum;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 53 * hash + this.stateNum;
        return hash;
    }
}
