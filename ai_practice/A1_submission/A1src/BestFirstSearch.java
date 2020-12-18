import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

public class BestFirstSearch implements Search{
    /**
     * An implementation of the search interface for best first searching.
     */
    // static state for comparing distance of each node with the goal node.
    public static State goal_state;
    // instance of BidirectionalSearch for calling method to compute polar distance
    BidirectionalSearch polarDistance = new BidirectionalSearch();

    // define manual comparator for pushing states into priority queue based on polar distance
    Comparator<State> polarDistanceComparator = new Comparator<State>() {
        @Override
        public int compare(State state1, State state2) {
            double radius0 = goal_state.getCircle();
            double radius1 = state1.getCircle();
            double angle0 = goal_state.getMeridian();
            double angle1 = state1.getMeridian();
            double polar_distance_1 = polarDistance.computePolarDistance(radius0, radius1, angle0, angle1);

            double radius2 = state2.getCircle();
            double angle2 = state2.getMeridian();
            double polar_distance_2 = polarDistance.computePolarDistance(radius0, radius2, angle0, angle2);

            return polar_distance_1 < polar_distance_2 ? -1 : ((polar_distance_1 == polar_distance_2) ? 0 : 1);
        }
    };

    public List<State> search(State start, State goal) {
        /**
         * Use BestF to return a list of all explored paths.
         * @return a list of all explored states
         */
        // assign the state parameters of goal state to the static "goal_state" var.
        goal_state = goal;
        List<State> all_explored_states = new ArrayList<>();
        List<State> visited = new ArrayList<>();
        // frontier based on manual comparator
        PriorityQueue<State> priorityQueue = new PriorityQueue<State>(polarDistanceComparator);
        List<State> expanded_states = new ArrayList<>();

        visited.add(start);
        priorityQueue.add(start);
        all_explored_states.add(start);

        boolean found_goal = false;
        int step = 0;
        while ((!priorityQueue.isEmpty()) && (!found_goal)) {
            step += 1;
            System.out.print("\n\nStep: " + step);
            State state = priorityQueue.remove();
            System.out.print("\nCurrent node: (" + state.getCircle() + "," + state.getMeridian() + ")");


            if (state.getMeridian() == goal.getMeridian() && state.getCircle() == goal.getCircle()) {
                System.out.print("\nGoal state reached.\n");
                found_goal = true;
            }
            else {
                List<State> statesToBeExpanded = state.getNext();

                System.out.print("\nList of states expanded till now: { ");
                for (State s: expanded_states){
                    System.out.print("(" + (int) s.getCircle() + "," + (int)s.getMeridian() + ") " );
                }
                System.out.print("}");
                expanded_states.add(state);

                for (State child : statesToBeExpanded) {
                    if (!visited.stream().anyMatch(o -> o.getMeridian() == child.getMeridian() &&
                            o.getCircle() == child.getCircle())) {
                        visited.add(child);
                        priorityQueue.add(child);
                        child.parent = state;
                        all_explored_states.add(child);
                    }
                }
                System.out.print("\nFrontier content with polar distance from goal: { ");
                for(State s: priorityQueue){
                    System.out.print(" (" + s.getCircle() + "," + s.getMeridian() + "): " +
                            polarDistance.computePolarDistance(s.getCircle(), goal.getCircle(),
                                    s.getMeridian(), goal.getMeridian()));
                }
                System.out.print(" }");
            }
        }
        return all_explored_states;
    }
}
