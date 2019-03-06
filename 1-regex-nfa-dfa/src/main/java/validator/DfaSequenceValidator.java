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

        State currentState = dfa.getStartStates().get(0);
        if (inputString.isEmpty()) {
            // if the first state is final then empty string is valid
            return currentState.isFinalState();
        }

        for (int i = 0; i < inputString.length(); i++) {
            List<State> transitionsForToken = currentState.getTransitions(inputString.charAt(i));
            if (CollectionUtils.isEmpty(transitionsForToken)) {
                return false;
            }

            currentState = transitionsForToken.get(0);
        }

        return currentState.isFinalState();
    }
}
