import java.security.spec.ECField;
import java.util.Arrays;

public class A1main {
    /**
     * Accepts command line arguments, resolves it to get following information:
     *  java A1main <DFS|BFS|AStar|BestF|Bidirec> <N> <d\_s, angle\_s> <d\_g, angle\_g> [BFS|BestF]* (where, * = optional)
     * Initiates the search based on the algorithm specified.
     */
    public static int[] resolveInputString(String inputs) {
        /**
         * Extracts circle and angle values from input string.
         * @param inputs: String of form "d, angle"
         * @return array of two integers: circle and angle.
         */
        String[] values = inputs.split(",");
        int[] input_values = Arrays.stream(values).mapToInt(Integer::parseInt).toArray();
        return input_values;
    }
    public static void main(String[] args) {
        String algorithm = args[0];
        int noOfCircles = Integer.parseInt(args[1]) - 1;
        String sourceNodeString = args[2];
        String goalNodeString = args[3];
        int[] sourceNodeInfo = resolveInputString(sourceNodeString);
        int[] goalNodeInfo = resolveInputString(goalNodeString);

        // check the validity of the inputs since no flight should reach 0th world
        if (sourceNodeInfo[0] == 0) {
            System.out.println("Aircraft can't fly over the pole, and will now be grounded!");
            System.exit(0);
        } else if (goalNodeInfo[0] == 0) {
            System.out.println("Aircraft can't reach the goal since it can't fly over the pole!");
            System.exit(0);
        }

        // create new start and goal state instances for search
        State start = new State(sourceNodeInfo[0], sourceNodeInfo[1], noOfCircles);
        State goal = new State(goalNodeInfo[0], goalNodeInfo[1], noOfCircles);


        if (algorithm.equals("Bidirec")) {
            // initiate search for bidirectional with type of method: either BFS or BestF
            try {
                goal.initiateSearch(algorithm, start, goal, args[4]);
            } catch (ArrayIndexOutOfBoundsException e) {
                System.out.println(e);
                System.out.println("How to run the program:\n");
                System.out.println("For Bidirectional search: java A1main <DFS|BFS|AStar|BestF|Bidirec> <N> <d\\_s,angle\\_s> " +
                        "<d\\_g,angle\\_g> <BFS|BestF>\n");
                System.out.println("For all other search: java A1main <DFS|BFS|AStar|BestF|Bidirec> <N> <d\\_s," +
                        "angle\\_s> <d\\_g,angle\\_g>\n");
            }
        } else {
            // initiate search for rest of methods without a fifth argument
            goal.initiateSearch(algorithm, start, goal, null);
        }

    }
    }
