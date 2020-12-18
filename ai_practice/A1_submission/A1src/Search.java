import java.util.List;

public interface Search {
    /**
     * Common interface implemented by all search methods (except bidirectional search).
     * @param source is the source state.
     * @param goal is the goal state.
     * @return a list of all the explored states, whether they contribute to the actual path or not.
     */
    public List<State> search(State source, State goal);
}

