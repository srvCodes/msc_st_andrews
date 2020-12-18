import javax.swing.*;
import java.util.*;

public class BreadthFirstSearch implements Search {
    /**
     * An implementation of the search interface for breadth first searching.
     */
    List<State> all_explored_states = new ArrayList<>();

    public List<State> search(State start, State goal){
        /**
         * Use BFS to return a list of all explored paths.
         * @return a list of all explored states
         */
        List<State> expanded_states = new ArrayList<>();
        List<State> visited = new ArrayList<State>();
        // queue for frontier
        Queue<State> queue = new LinkedList<State>();

        visited.add(start);
        queue.add(start);
        all_explored_states.add(start);
        boolean goal_found = false;
        int step = 0;

        while((!queue.isEmpty()) && (!goal_found)) {
            step += 1;
            System.out.print("\n\nStep: " + step);
            State state = queue.remove();
            System.out.print("\nCurrent node: (" + state.getCircle() + "," + state.getMeridian() + ")");
            // check if goal state reached
            if (state.getMeridian() == goal.getMeridian() && state.getCircle() == goal.getCircle()) {
                System.out.print("\nGoal state reached. \n");
                goal_found = true;
            } else {
                // get all possible transitions
                List<State> statesToBeExpanded = state.getNext();
                System.out.print("\nList of states expanded till now: { ");
                for (State s: expanded_states){
                    System.out.print("(" + (int) s.getCircle() + "," + (int)s.getMeridian() + ") ");
                }
                System.out.print("}");
                expanded_states.add(state);

                int numOfExpandedStates = all_explored_states.size();
                for (State child : statesToBeExpanded) {
                    // if a child not visited yet then push it to the queue.
                    if (!visited.stream().anyMatch(o -> o.getMeridian() == child.getMeridian() &&
                            o.getCircle() == child.getCircle())) {
                        visited.add(child);
                        queue.add(child);
                        // assign parent
                        child.parent = state;
                        // add this to the list of all explored states
                        all_explored_states.add(child);
                    }
                }
                for(State s: queue){
                    System.out.print("(" + s.getCircle() + "," + s.getMeridian() + ") ");
                }
                System.out.print("}");
            }
        }
        return all_explored_states;
    }
}