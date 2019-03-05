package validator;

import fa.Automata;
import fa.state.State;
import util.CollectionUtils;

import java.util.List;

public class DfaSequenceValidator {

    public static boolean validate(Automata<State> dfa, String inputString) {
        if (dfa.getStates().isEmpty()) {
            return inputString.isEmpty();
        }

        State state = dfa.getStartStates().get(0);
        if (inputString.isEmpty()) {
            // if the first is state is final state, so empty string is valid
            return state.isFinalState();
        }

        for (int i = 0; i < inputString.length(); i++) {
            List<State> transitionsForToken = state.getTransitions(inputString.charAt(i));
            if (CollectionUtils.isEmpty(transitionsForToken)) {
                return false;
            }

            state = transitionsForToken.get(0);
        }

        return state.isFinalState();
    }
}
