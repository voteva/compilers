package fa;

import java.util.LinkedList;
import java.util.List;

/**
 * Finite-state automata
 *
 * @param <T> - type of states of automata
 */
public class Automata<T> {

    private LinkedList<T> states;
    private List<T> startStates;

    public Automata() {
        this.states = new LinkedList<>();
        this.startStates = new LinkedList<>();
    }

    public Automata(LinkedList<T> states, List<T> startStates) {
        this.states = states;
        this.startStates = startStates;
    }

    public LinkedList<T> getStates() {
        return states;
    }

    public List<T> getStartStates() {
        return startStates;
    }

    public void setStartStates(List<T> startStates) {
        this.startStates = startStates;
    }
}
