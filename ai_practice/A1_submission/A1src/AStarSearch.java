import java.util.*;


public class AStarSearch implements Search{
    /**
     * An implementation of the search interface for A* searching.
     */
    List<State> all_explored_states = new ArrayList<>();
    // define a manual comparator for adding states to frontier based on F-scores.
    Comparator<State> costComparator = new Comparator<State>() {
        @Override
        public int compare(State state1, State state2) {
            return Double.compare(state1.getFscore(), state2.getFscore());
        }
    };

    public double getCostFromParent(State parent, State child){
        /**
         * Compute polar distance between parent and child states.
         */
        double radius1 = parent.getCircle();
        double radius2 = child.getCircle();
        double angle1 = parent.getMeridian();
        double angle2 = child.getMeridian();

        BidirectionalSearch polarDistance = new BidirectionalSearch();
        double polar_distance = polarDistance.computePolarDistance(radius1, radius2, angle1, angle2);
//        System.out.println("Polar distance of " + (int)(radius2) + "," + (int)(angle2)+ " from " + (int)(radius1) + ","
//                + (int)(angle1) + " is: " + polar_distance);
        return polar_distance;
    }

    public List<State> search(State start, State goal) {
        /**
         * Use AStar to return a list of all explored paths.
         * @return a list of all explored states
         */
        List<State> visited = new ArrayList<>();
        PriorityQueue<State> priorityQueue = new PriorityQueue<State>(costComparator);
        List<State> expanded_states = new ArrayList<>();

        // set g-score of source node to 0
        start.setGScore(0);
        // set h-score of source node to be distance from the goal.
        start.setHScore(getCostFromParent(goal, start));
        // set f-score of source node to be same as h-score since g(source) = 0
        start.setFScore(start.getHscore());
        // add to frontier and all explored states
        priorityQueue.add(start);
        all_explored_states.add(start);

        boolean found = false;
        priorityQueue.add(start);
        int step = 0;

        while ((!priorityQueue.isEmpty()) && (!found)) {
            State state = priorityQueue.remove();
            if(visited.stream().anyMatch(o -> o.getMeridian() == state.getMeridian() &&
                    o.getCircle() == state.getCircle())) {
                continue;
            }
            step += 1;
            System.out.print("\n\nStep: " + step);
            visited.add(state);

            System.out.print("\nCurrent node: (" + state.getCircle() + "," + state.getMeridian() + ")");

            // check for goal reached
            if (state.getMeridian() == goal.getMeridian() && state.getCircle() == goal.getCircle()) {
                System.out.print("\nGoal state reached.\n");
                found = true;
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
                    // compute g, h and f-scores for the child
                    double cost = getCostFromParent(state, child);
                    double temp_g_score = state.getGscore() + cost;
                    child.setHScore(getCostFromParent(child, goal));
                    double temp_f_score = temp_g_score + child.getHscore();

                    // check if the F-score of this child node is less than the currently assigned F-score to update it.
                    if ((visited.stream().anyMatch(o -> o.getMeridian() == child.getMeridian() &&
                            o.getCircle() == child.getCircle())) && (temp_f_score >= child.getFscore())) {
                        continue;
                    } else if ((!priorityQueue.stream().anyMatch(o -> o.getMeridian() == child.getMeridian() &&
                            o.getCircle() == child.getCircle())) || (temp_f_score < child.getFscore())) {
                        child.parent = state;
                        all_explored_states.add(child);
                        // update the g and f-scores if these are lesser than currently assigned ones.
                        child.setGScore(temp_g_score);
                        child.setFScore(temp_f_score);
                        // remove if any previous instance of child node is already in the frontier
                        if (priorityQueue.stream().anyMatch(o -> o.getMeridian() == child.getMeridian() &&
                                o.getCircle() == child.getCircle())) {
                            priorityQueue.remove(child);
                        }
                        priorityQueue.add(child);
                    }
                }
                System.out.print("\nFrontier content with F-scores: { ");
                for(State s: priorityQueue){
                    System.out.print(" (" + s.getCircle() + "," + s.getMeridian() + "): " + s.getFscore());
                }
                System.out.print(" }");
            }
        }
        return all_explored_states;
    }
}
