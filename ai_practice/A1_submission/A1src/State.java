import java.util.*;

public class State {
    /**
     * Stores the information needed for building a graph and its nodes/states.
     */
    // Attributes common to all states:
    private int meridian;
    private int circle;
    private int noOfCircles;
    public State parent;
    // Additional attributes applicable to states for AStar search:
    private double fscore = 0;
    private double gscore;
    private double hscore;
    // Additional attributes applicable to states for Bidirectional search (instead of State parent)
    public State source_parent;
    public State goal_parent;

    public State(int circle, int meridian, int noOfCircles) {
        this.meridian = meridian;
        this.circle = circle;
        this.noOfCircles = noOfCircles;
    }

    public int getMeridian() {
        return meridian;
    }

    public int getCircle() {
        return circle;
    }

    public int getNoOfCircles() {
        return noOfCircles;
    }

    public void setGScore(double g) {
        this.gscore = g;
    }

    public void setHScore(double h) {
        this.hscore = h;
    }

    public void setFScore(double f) {
        this.fscore = f;
    }

    public double getFscore() {
        return fscore;
    }

    public double getGscore() {
        return gscore;
    }

    public double getHscore() {
        return hscore;
    }

    public String getLabelsForPath(State current, State previous) {
        /**
         * Determine the direction of transition using the current and the previous state.
         */
        int currentAngle = current.getMeridian();
        int previousAngle = previous.getMeridian();
        int currentCircle = current.getCircle();
        int previousCircle = previous.getCircle();
        String direction = "";

        if (currentCircle == previousCircle) {
            if (currentAngle == (previousAngle + 45) % 360) {
                direction = "H90";
            } else {
                if (currentAngle == 315 && previousAngle == 0) {
                    direction = "H270";
                } else if (currentAngle == (previousAngle - 45)) {
                    direction = "H270";
                }
            }
        } else {
            if (currentCircle == previousCircle + 1) {
                direction = "H180";
            } else {
                direction = "H360";
            }
        }
        return direction;
    }

    public void initiateSearch(String algorithm, State start, State goal, String bidirec_algo) {
        /**
         * Initiate the search based on the algorithm and trace out the path, directions and cost of final solution.
         */
        // initialize an empty instance of the "Search" interface.
        Search s = null;
        // Assign the proper implementation of the interface based on the algorithm
        if (algorithm.equals("BFS")) {
            s = new BreadthFirstSearch();
        } else if (algorithm.equals("DFS")) {
            s = new DepthFirstSearch();
        } else if (algorithm.equals("BestF")) {
            s = new BestFirstSearch();
        } else if (algorithm.equals("AStar")) {
            s = new AStarSearch();
        }

        List<State> path = new ArrayList<>();
        System.out.print(algorithm);
        if (!algorithm.equals("Bidirec")) {
            // get a list of all explored states, and trace out the path using the goal node's parents recursively.
            List<State> all_explored_states = s.search(start, goal);
            path = getPath(all_explored_states, goal);
        } else {
            // a separate instance is used for class BidirectionalSearch since it has a different return type.
            BidirectionalSearch bs = new BidirectionalSearch();
            // bidirecResult is an instance of class "returnValues"
            ReturnBidirecResults bidirecResult = bs.search(start, goal, bidirec_algo);
            // extract the path traced out by the searches from the source and the goal node
            List<State> source_path = getPath(bidirecResult.all_explored_states_source, bidirecResult.intersecting_node,
                    "source");
            List<State> goal_path = getPath(bidirecResult.all_explored_states_goal, bidirecResult.intersecting_node,
                    "goal");
            // add the paths obtained from both the searches to obtain the final result
            path.addAll(source_path);
            // sublist starting from 1st element is choosen as 0th element is common with source_path
            path.addAll(goal_path.subList(1, goal_path.size()));
        }
        System.out.print("\n =======      ==========================================================     ========\n");

        // print the final solution details: the path, directions, and the total cost of path
        List<String> directions = new ArrayList<>();
        double totalPathCost = 0;
        BidirectionalSearch polarDistance = new BidirectionalSearch();

        System.out.print("Final solution details: \nSize of path found: " + (path.size() - 1) + "\nRoute: ");
        for (int i = 0; i < path.size(); i++) {
            System.out.print("(" + path.get(i).getCircle() + "," + path.get(i).getMeridian() + ")");
            if (i != path.size() - 1) {
                System.out.print(" --> ");
            }
            if (i > 0) {
                directions.add(getLabelsForPath(path.get(i), path.get(i - 1)));
                totalPathCost += polarDistance.computePolarDistance(path.get(i).getCircle(), path.get(i - 1).getCircle(),
                        path.get(i).getMeridian(), path.get(i - 1).getMeridian());
            }
        }

        System.out.print("\nDirections: [ ");
        for (String direction : directions) {
            System.out.print(direction + " ");
        }
        System.out.println("]\nTotal path cost: " + totalPathCost);
    }

    public static State findState(List<State> all_states, State goal) {
        /**
         * returns the state from a list of states with a matching circle and meridian as that of goal state.
         */
        State goal_state = all_states.stream().filter(s -> s.getMeridian() == goal.getMeridian() &&
                s.getCircle() == goal.getCircle()).findFirst().orElse(null);
        return goal_state;
    }

    public static List<State> getPath(List<State> all_states, State goal) {
        /**
         * Uses the "parent" attribute to trace out the path of traversal starting from the "goal" state.
         */
        List<State> path = new ArrayList<>();
        for (State state = goal; state != null; ) {
            State goal_state = findState(all_states, state);
            path.add(goal_state);
            try {
                state = goal_state.parent;
            } catch (NullPointerException e) {
                state = null;
            }
        }
        // reverse the traced path to begin with the source node.
        Collections.reverse(path);
        return path;
    }

    public static List<State> getPath(List<State> all_states, State goal, String mode) {
        /**
         * Overloads the getPath() method based on no. of parameters; is called only for Bidirectional search.
         * Additional param. "mode" signifies the search from "source" or "goal" for bidirectional search.
         */
        List<State> path = new ArrayList<>();
        for (State state = goal; state != null; ) {
            State goal_state = findState(all_states, state);
            path.add(goal_state);
            try {
                if (mode == "source") {
                    state = goal_state.source_parent;
                } else if (mode == "goal") {
                    state = goal_state.goal_parent;
                }
            } catch (NullPointerException e) {
                state = null;
            }
        }
        if (mode == "source") {
            // reverse the path traced from source to get the source node as first element.
            Collections.reverse(path);
        }
        return path;
    }

    public List<State> getNext() {
        /**
         * Method to get the list of all valid transitions of a state.
         */
        List<State> possibleMeridians = new ArrayList<>();
        State state;

        int nextGreaterMeridian = (meridian + 45) % 360;
        int nextSmallerMeridian;
        if (meridian == 0) {
            nextSmallerMeridian = 315;
        } else {
            nextSmallerMeridian = meridian - 45;
        }

        state = new State(circle, nextGreaterMeridian, noOfCircles); // create new state objects and add to possibleMeridians list
        possibleMeridians.add(state); // WHAT IS this state for?

        state = new State(circle, nextSmallerMeridian, noOfCircles);
        possibleMeridians.add(state);
        // take care of falling into 0th world
        if (circle > 1) {
            state = new State(circle - 1, meridian, noOfCircles);
            possibleMeridians.add(state);
        }
        // take care of falling beyond the outermost world
        if (circle < noOfCircles) {
            state = new State(circle + 1, meridian, noOfCircles);
            possibleMeridians.add(state);
        }
        return possibleMeridians;
    }
}
