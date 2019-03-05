package creator;

import fa.Automata;
import fa.state.State;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Builds minimal FA using Brzhozovskii's algorithm
 */
public class MinDfaCreator {

    public static Automata<State> create(Automata<State> fa) {
        return DfaCreator.create(revert(DfaCreator.create(revert(fa))));
    }

    private static Automata<State> revert(Automata<State> fa) {
        Map<Integer, State> helperMap = new HashMap<>();

        List<State> newStartStates = new LinkedList<>();
        LinkedList<State> newStates = fa.getStates()
                .stream()
                .map(s -> {
                    State newState = new State(s.getStateNum());
                    helperMap.put(s.getStateNum(), newState);

                    if (s.isFinalState()) {
                        newStartStates.add(newState);
                    }
                    if (fa.getStartStates().contains(s)) {
                        newState.setFinalState(true);
                    }
                    return newState;
                })
                .collect(Collectors.toCollection(LinkedList::new));

        for (State state : fa.getStates()) {
            state.getTransitions().forEach((k, v) -> {
                for (State transitionState : v) {
                    State stateFrom = helperMap.get(transitionState.getStateNum());
                    State stateTo = helperMap.get(state.getStateNum());
                    stateFrom.addTransition(k, stateTo);
                }
            });
        }

        return new Automata<>(newStates, newStartStates);
    }
}
