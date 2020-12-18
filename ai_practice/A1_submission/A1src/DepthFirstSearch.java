import javax.swing.*;
import java.util.*;

public class DepthFirstSearch implements Search{
    /**
     * An implementation of the search interface for depth first searching.
     */
    public List<State> search(State start, State goal){
        /**
         * Use DFS to return a list of all explored paths.
         * @return a list of all explored states
         */
        List<State> all_explored_states = new ArrayList<>();
        List<State> visited = new ArrayList<State>();
        // stack used as frontier
        Stack<State> stack = new Stack<State>();
        List<State> expanded_states = new ArrayList<>();

        visited.add(start);
        stack.push(start);
        all_explored_states.add(start);
        boolean found_goal = false;
        int step = 0;

        while((!stack.isEmpty()) && (!found_goal)){
            step += 1;
            System.out.print("\n\nStep: " + step);
            State state = stack.peek();
            stack.pop();
            System.out.print("\nCurrent node: (" + state.getCircle() + "," + state.getMeridian() + ")");

            if (!visited.stream().anyMatch(o -> o.getMeridian() == state.getMeridian() &&
                    o.getCircle() == state.getCircle())){
                visited.add(state);
            }
            // check if goal reached
            if (state.getMeridian() == goal.getMeridian() && state.getCircle() == goal.getCircle()) {
                System.out.print("\nGoal state reached.\n");
                found_goal = true;
            }
            else {
                List<State> statesToBeExpanded = state.getNext();
                System.out.print("\nList of states expanded till now: { ");
                for (State s: expanded_states){
                    System.out.print("(" + (int) s.getCircle() + "," + (int)s.getMeridian() + ") ");
                }
                System.out.print("}");
                expanded_states.add(state);
                for (State child : statesToBeExpanded) {
                    if (!visited.stream().anyMatch(o -> o.getMeridian() == child.getMeridian() &&
                            o.getCircle() == child.getCircle())) {
                        stack.push(child);
                        child.parent = state;
                        all_explored_states.add(child);
                    }
                }
                System.out.print("\nFrontier content: { ");
                for(State s: stack){
                    System.out.print("(" + s.getCircle() + "," + s.getMeridian() + ") ");
                }
                System.out.print("}");
            }
        }
        return all_explored_states;
    }
}