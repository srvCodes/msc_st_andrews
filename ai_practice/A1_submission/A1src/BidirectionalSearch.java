import java.util.*;

class ReturnBidirecResults {
    /**
     * Class for returning the solution components for bidirectional search:
     * @param all_explored_states_source: all expanded states obtained from source node
     * @param all_explored_states_goal: all expanded states reached from goal node
     * @param intersecting_node: the node where both the searches meet
     */
    public List<State> all_explored_states_source = new ArrayList<>();
    public List<State> all_explored_states_goal = new ArrayList<>();
    public State intersecting_node;

    ReturnBidirecResults(List<State> all_explored_states_source, List<State> all_explored_states_goal, State intersectingNode){
        this.all_explored_states_source = all_explored_states_source;
        this.all_explored_states_goal = all_explored_states_goal;
        this.intersecting_node = intersectingNode;
    }
}



public class BidirectionalSearch{
    private List<State> source_visited = new ArrayList<State>();
    private List<State> goal_visited =new ArrayList<>();
    private Queue<State> source_queue = new LinkedList<State>();
    private Queue<State> goal_queue = new LinkedList<>();
    public List<State> all_explored_states_source = new ArrayList<>();
    public List<State> all_explored_states_goal = new ArrayList<>();
    public List<State> expanded_nodes_src = new ArrayList<>();
    public List<State> expanded_nodes_goal = new ArrayList<>();
    List<State> all_expanded_states_from_source = new ArrayList<>();
    List<State> all_expanded_states_from_goal = new ArrayList<>();

    public static State source_state, goal_state;
    Comparator<State> sourcePolarDistanceComparator = new Comparator<State>() {
        @Override
        public int compare(State state1, State state2) {
            double radius0 = source_state.getCircle();
            double radius1 = state1.getCircle();
            double angle0 = source_state.getMeridian();
            double angle1 = state1.getMeridian();
            double polar_distance_1 = computePolarDistance(radius0, radius1, angle0, angle1);

            double radius2 = state2.getCircle();
            double angle2 = state2.getMeridian();
            double polar_distance_2 = computePolarDistance(radius0, radius2, angle0, angle2);

            return polar_distance_1 < polar_distance_2 ? -1 : ((polar_distance_1 == polar_distance_2) ? 0 : 1);
        }
    };
    Comparator<State> goalPolarDistanceComparator = new Comparator<State>() {
        @Override
        public int compare(State state1, State state2) {
            double radius0 = goal_state.getCircle();
            double radius1 = state1.getCircle();
            double angle0 = goal_state.getMeridian();
            double angle1 = state1.getMeridian();
            double polar_distance_1 = computePolarDistance(radius0, radius1, angle0, angle1);

            double radius2 = state2.getCircle();
            double angle2 = state2.getMeridian();
            double polar_distance_2 = computePolarDistance(radius0, radius2, angle0, angle2);

            return polar_distance_1 < polar_distance_2 ? -1 : ((polar_distance_1 == polar_distance_2) ? 0 : 1);
        }
    };
    PriorityQueue<State> goalPriorityQueue = new PriorityQueue<State>(sourcePolarDistanceComparator);
    PriorityQueue<State> sourcePriorityQueue = new PriorityQueue<State>(goalPolarDistanceComparator);

    public double computePolarDistance(double radius1, double radius2, double angle1, double angle2){
        // return the computed polar distance using the two states
        return Math.sqrt(radius1 * radius1 + radius2 * radius2 - 2 * radius1 * radius2 *
                Math.cos(angle2 - angle1));
    }

    public void BFS(Queue<State> queue, List<State> visited, String mode) {
        State state = queue.remove();
        System.out.print("\nCurrent " + mode + " node: (" + state.getCircle() + "," + state.getMeridian() + ")");

        System.out.print("\nList of states expanded from " + mode + " till now: { ");
        if (mode == "source") {
            for (State s : all_expanded_states_from_source) {
                System.out.print("(" + (int) s.getCircle() + "," + (int) s.getMeridian() + ") ");
            }
            all_expanded_states_from_source.add(state);
        } else if (mode == "goal") {
            for (State s : all_expanded_states_from_goal) {
                System.out.print("(" + (int) s.getCircle() + "," + (int) s.getMeridian() + ") ");
            }
            all_expanded_states_from_goal.add(state);
        }
        System.out.print("}");

        List<State> statesToBeExpanded = state.getNext();

        for (State child : statesToBeExpanded) {
            if (!visited.stream().anyMatch(o -> o.getMeridian() == child.getMeridian() &&
                    o.getCircle() == child.getCircle())) {
                visited.add(child);
                queue.add(child);
                if (mode == "source") {
                    child.source_parent = state;
                    all_explored_states_source.add(child);
                }else if (mode == "goal"){
                    child.goal_parent = state;
                    all_explored_states_goal.add(child);
                }
            }
        }
        System.out.print("\nFrontier content for " + mode + " queue: { ");
        if (mode == "source") {
            for (State s : queue) {
                System.out.print("(" + s.getCircle() + "," + s.getMeridian() + ") ");
            }
        } else if (mode == "goal"){
            for (State s : queue) {
                System.out.print("(" + s.getCircle() + "," + s.getMeridian() + ") ");
            }
        }
        System.out.println("}");
    }

    public void BestFirst(List<State> visited, String mode) {
        State state = null;
        if (mode == "source") {
            state = sourcePriorityQueue.remove();
        } else if (mode == "goal"){
            state = goalPriorityQueue.remove();
        }
        System.out.print("\nCurrent " + mode + " node: (" + state.getCircle() + "," + state.getMeridian() + ")");
        List<State> statesToBeExpanded = state.getNext();

        System.out.print("\nList of states expanded from " + mode + " till now: { ");
        if (mode == "source") {
            for (State s : all_expanded_states_from_source) {
                System.out.print("(" + (int) s.getCircle() + "," + (int) s.getMeridian() + ") ");
            }
            all_expanded_states_from_source.add(state);
        } else if (mode == "goal") {
            for (State s : all_expanded_states_from_goal) {
                System.out.print("(" + (int) s.getCircle() + "," + (int) s.getMeridian() + ") ");
            }
            all_expanded_states_from_goal.add(state);
        }
        System.out.print("}");

        for (State child : statesToBeExpanded) {
            if (!visited.stream().anyMatch(o -> o.getMeridian() == child.getMeridian() &&
                    o.getCircle() == child.getCircle())) {
                visited.add(child);

                if (mode == "source") {
                    sourcePriorityQueue.add(child);
                    child.source_parent = state;
                    all_explored_states_source.add(child);
                }
                else if (mode == "goal"){
                    goalPriorityQueue.add(child);
                    child.goal_parent = state;
                    all_explored_states_goal.add(child);
                }
            }
        }
        System.out.print("\nFrontier content for " + mode + " priorityQueue: { ");
        if (mode == "source") {
            for (State s : sourcePriorityQueue) {
                System.out.print("(" + s.getCircle() + "," + s.getMeridian() + ") ");
            }
        } else if (mode == "goal"){
            for (State s : goalPriorityQueue) {
                System.out.print("(" + s.getCircle() + "," + s.getMeridian() + ") ");
            }
        }
        System.out.println("}");
    }

    public ReturnBidirecResults search (State source, State goal, String algo){
        source_state = source;
        goal_state = goal;
        // initialize intersecting state between explored paths from source and goal nodes to null
        State intersectingNode = null;
        // use a simple queue for bfs and a priority queue frontier for best-first method
        if (algo.equals("BFS")) {
            source_queue.add(source);
            goal_queue.add(goal);
        } else if (algo.equals("BestF")) {
            sourcePriorityQueue.add(source);
            goalPriorityQueue.add(goal);
        }
        // maintain separate list of visited and explored states for search from goal and source
        source_visited.add(source);
        goal_visited.add(goal);
        all_explored_states_source.add(source);
        all_explored_states_goal.add(goal);

        int step = 0;
        if (algo.equals("BFS")) {
            // use the breadth first search method
            while (!source_queue.isEmpty() && !goal_queue.isEmpty()) {
                step += 1;
                System.out.print("\n\nStep: " + step);

                int numOfSourceExpandedStates = all_explored_states_source.size();
                int numOfGoalExpandedStates = all_explored_states_goal.size();

                BFS(source_queue, source_visited, "source");

                if (numOfSourceExpandedStates == all_explored_states_source.size()) {
                    System.out.print("No source states expanded.");
                }

                BFS(goal_queue, goal_visited, "goal");

                if (numOfGoalExpandedStates == all_explored_states_goal.size()) {
                    System.out.print("No goal states expanded.");
                }
                System.out.print("source size: " + source_visited.size());
                intersectingNode = isIntersecting(source_visited, goal_visited);
                if(intersectingNode != null){
                    System.out.println("\nCommon state found: (" + intersectingNode.getCircle() + "," +
                            intersectingNode.getMeridian() + ")");
                    System.out.println("Terminating search now!");
                    break;
                }
            }
        }else if (algo.equals("BestF")) {
            // use the best first search method
            while (!sourcePriorityQueue.isEmpty() && !goalPriorityQueue.isEmpty()) {
                step += 1;
                System.out.print("\n\nStep: " + step);

                int numOfSourceExpandedStates = all_explored_states_source.size();
                int numOfGoalExpandedStates = all_explored_states_goal.size();

                BestFirst(source_visited, "source");
                if (numOfSourceExpandedStates == all_explored_states_source.size()) {
                    System.out.print("No source states expanded.");
                }

                BestFirst(goal_visited, "goal");
                if (numOfGoalExpandedStates == all_explored_states_goal.size()) {
                    System.out.print("No goal states expanded.");
                }
                intersectingNode = isIntersecting(source_visited, goal_visited);
                if(intersectingNode != null){
                    System.out.println("\nCommon state found: (" + intersectingNode.getCircle() + "," +
                            intersectingNode.getMeridian() + ")");
                    System.out.println("Terminating search now!");
                    break;
                }
            }
        }
//        System.out.print(intersectingNode.getCircle() + " " + intersectingNode.getMeridian());
        return new ReturnBidirecResults(all_explored_states_source, all_explored_states_goal, intersectingNode);
    }

    public State isIntersecting(List<State> source_visited, List<State> goal_visited){
        /**
         * Identify if there is a common node between list of states explored from source and goal nodes.
         * @param source_visited: list of states explored from source state
         * @param goal_visited: list of states explored from goal state
         * @return the state, if common else null
         */
        State intersectingNode = null;
        for(State src: source_visited){
            State common_state = goal_visited.stream().filter(s -> s.getMeridian() == src.getMeridian() &&
                    s.getCircle() == src.getCircle()).findFirst().orElse(null);
            if (common_state != null){
                intersectingNode = common_state;
                break;
            }
        }
        return intersectingNode;
    }
}
