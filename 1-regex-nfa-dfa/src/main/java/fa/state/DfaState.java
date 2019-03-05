package fa.state;

import java.util.Set;

public class DfaState extends State {

    private Set<State> innerStates;

    public DfaState(int stateNum, Set<State> innerStates) {
        super(stateNum);
        this.innerStates = innerStates;

        for (State state : innerStates) {
            if (state.isFinalState()) {
                this.setFinalState(true);
                break;
            }
        }
    }

    public Set<State> getInnerStates() {
        return innerStates;
    }
}
