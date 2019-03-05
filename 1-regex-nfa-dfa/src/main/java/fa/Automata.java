package fa;

import java.util.LinkedList;

/**
 * Finite-state automata
 *
 * @param <T> - type of states of automata
 */
public class Automata<T> {

    private LinkedList<T> states;

    public Automata() {
        this.states = new LinkedList<>();
    }

    public Automata(LinkedList<T> states) {
        this.states = states;
    }

    public LinkedList<T> getStates() {
        return states;
    }
}
