package creator;

import fa.Automata;
import fa.state.DfaState;
import fa.state.State;
import util.CollectionUtils;
import util.TokenUtils;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.stream.Collectors;

public class DfaCreator {

    public static Automata<State> create(Automata<State> nfa) {
        if (CollectionUtils.isEmpty(nfa.getStates())) {
            return new Automata<>();
        }
        removeEpsilonTransitions(nfa);

        int stateNum = 0;
        Queue<DfaState> statesQueue = new LinkedList<>();
        LinkedList<DfaState> newStates = new LinkedList<>();
        List<State> newStartStates = new LinkedList<>();

        DfaState newStartState = new DfaState(stateNum++, new HashSet<>(nfa.getStartStates()));
        newStartState.setFinalState(containsFinalState(newStartState.getInnerStates()));

        statesQueue.add(newStartState);
        newStates.add(newStartState);
        newStartStates.add(newStartState);

        Set<Character> allLiteralTokens = getAllLiteralTokens(nfa);

        while (!statesQueue.isEmpty()) {
            DfaState currentState = statesQueue.poll();

            for (Character token : allLiteralTokens) {
                Set<State> achievableStates = currentState.getInnerStates()
                        .stream()
                        .map(innerState -> innerState.getTransitions(token))
                        .flatMap(List::stream)
                        .collect(Collectors.toSet());

                if (CollectionUtils.isEmpty(achievableStates)) {
                    continue;
                }

                boolean isPresent = false;
                for (DfaState s : newStates) {
                    if (s.getInnerStates().containsAll(achievableStates)
                            && achievableStates.containsAll(s.getInnerStates())) {
                        isPresent = true;
                        currentState.addTransition(token, s);
                        break;
                    }
                }

                if (isPresent) {
                    continue;
                }

                DfaState newState = new DfaState(stateNum++, achievableStates);
                newState.setFinalState(containsFinalState(achievableStates));

                currentState.addTransition(token, newState);
                statesQueue.add(newState);
                newStates.add(newState);
            }
        }

        return new Automata<>(
                newStates.stream()
                        .map(s -> (State) s)
                        .collect(Collectors.toCollection(LinkedList::new)),
                newStartStates);
    }

    private static boolean containsFinalState(Set<State> states) {
        return states.stream().anyMatch(State::isFinalState);
    }

    private static Set<Character> getAllLiteralTokens(Automata<State> nfa) {
        return nfa.getStates().stream()
                .map(s -> s.getTransitions().entrySet()
                        .stream()
                        .map(Map.Entry::getKey)
                        .collect(Collectors.toList()))
                .flatMap(List::stream)
                .filter(t -> t != TokenUtils.EPSILON_TOKEN)
                .collect(Collectors.toSet());
    }

    private static void removeEpsilonTransitions(Automata<State> nfa) {
        for (State state : nfa.getStates()) {
            while (!CollectionUtils.isEmpty(state.getTransitions(TokenUtils.EPSILON_TOKEN))) {
                List<State> achievableStates = state.getTransitions(TokenUtils.EPSILON_TOKEN);
                state.removeTransitions(TokenUtils.EPSILON_TOKEN);

                for (State achievableState : achievableStates) {
                    achievableState.getTransitions().forEach(state::addTransitions);

                    if (achievableState.isFinalState()) {
                        state.setFinalState(true);
                    }
                }
            }
        }
    }
}
