import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Class to represent a network node: its name, values, parents, children and observed probabilities.
 */
public class RandomVariable {
    public final String name;
    public List<RandomVariable> parents;
    public List<RandomVariable> children;
    public Map<String, DomainValue> domain;
    public Map<ObservationCondition, Double> probabilities;
    public BayesianNetwork network;

    public RandomVariable(String name) {
        this.name = name;
        parents = new ArrayList<RandomVariable>();
        children = new ArrayList<RandomVariable>();
        domain = new HashMap<String, DomainValue>();
        probabilities = null;
    }

    public RandomVariable(BayesianNetwork net, String name) {
        this(name);
        this.network = net;
    }

    public void printConditionalProbabilities() {
        System.out.print("\n For node '" + name + "': {");
        for (Map.Entry<ObservationCondition, Double> entry : probabilities.entrySet()) {
            for (Observation o : entry.getKey()) {
                System.out.print(" Parent: " + o.node.name + " Value: " + o.value);
            }
            System.out.print("}, Probability: " + entry.getValue() + "\n");
        }
    }

    /**
     * Parse a string into an Observation object.
     *
     * @param line - e.g. "co=true"
     */
    public Observation parseObservation(String line) {
        line = line.replaceAll("\\s+", "");
        String[] e = line.split("=");
        if (e.length != 2)
            throw new CustomError("Expected \"variable=value\", received " + line);

        if (network.hasNode(e[0]))
            return new Observation(network.getNode(e[0]), e[1]);
        else
            throw new CustomError("No such variable <" + e[0] + ">.");
    }

    /**
     * Parse a string into an ObservationCondition object.
     *
     * @param line - e.g. "TCP=T, weather=sunny"
     */
    public ObservationCondition parseCondition(String line) {
        line = line.replaceAll("\\s+", "");
        String[] cond = line.split(",");
        if (cond.length != parents.size() + 1)
            throw new CustomError(
                    "Number of events (" + cond.length + ") mismatches required number (" + parents.size() + 1 + ").");

        List<Observation> conditionList = new ArrayList<Observation>();
        for (String event : cond) {
            conditionList.add(parseObservation(event));
        }

        return new ObservationCondition(conditionList);
    }

    /**
     * Index the probability by a condition
     */
    public Double getProbability(String cond) {
        return probabilities.get(parseCondition(cond));
    }

    /**
     * Add parent while initializing the network (from the string name)
     *
     * @param name is the parent's name
     */
    public void addParent(String name) {
        try {
            addParent(network.getNode(name));
        } catch (CustomError e) {
            throw new CustomError("The specified parent node '" + name + "' does not exist!");
        }
    }

    public void addParent(RandomVariable parent) {
        if (parent != null)
            parents.add(parent);
        else
            throw new CustomError("The specified parent node does not exist!");
    }

    public void addValue(String name) {
        if (name == null)
            throw new CustomError("Please enter a valid name.");
        if (domain.containsKey(name))
            throw new CustomError("Value with name \"" + name + "\"already exists.");
        domain.put(name, new DomainValue(name, this));
    }

    /**
     * @param line is in format "visibility = true, weather = sunny : 0.8"
     */
    public void addProbability(String line) {
        if (probabilities == null) {
            probabilities = new HashMap<ObservationCondition, Double>();

            List<RandomVariable> varList = new ArrayList<RandomVariable>(parents);
            varList.add(this);

            for (ObservationCondition c : allConditions(varList))
                probabilities.put(c, 0.0);
        }
        line = line.replaceAll("\\s+", "");

        String[] desc = line.split(":"); // where desc (description) is like
        // ["a=true,weather=sunny", "0.8"]
        if (desc.length != 2)
            throw new CustomError("Only one ':' in an description allowed.");

        Double probability = Double.parseDouble(desc[1]);

        ObservationCondition condition = parseCondition(desc[0]);

        if (probabilities.containsKey(condition)) {
            probabilities.put(condition, probability);
        } else {
            throw new CustomError("Provided condition mismatch.");
        }
    }

    public DomainValue getValue(String name) {
        return domain.get(name);
    }

    /**
     * Method to generate all possible combinations with parents for computing conditional probabilities.
     *
     * @param varList is the list containing current node plus all its parents
     * @return a list of ObservationCondition
     */
    public static List<ObservationCondition> allConditions(List<RandomVariable> varList) {
        List<ObservationCondition> result = new ArrayList<ObservationCondition>();
        makeCombinationsRecursively(varList, result, new ArrayList<Observation>());
        return result;
    }

    /**
     * Method to get all combinations of variable outcome recursively
     *
     * @param src    is the list of current node and its parents
     * @param dest   is list of all observed node values and their probabilities
     * @param walked is the list of nodes for which the loop has identified all possible observations.
     */
    private static void makeCombinationsRecursively(List<RandomVariable> src, List<ObservationCondition> dest, List<Observation> walked) {
        // termination condition when all nodes in src have been visited.
        if (src.size() == walked.size()) {
            dest.add(new ObservationCondition(walked));
            return;
        }
        // Last variable in src is the current node
        RandomVariable current = src.get(walked.size());
        // add an observation for each possible domain value of the node and its parents.
        for (DomainValue v : current.domain.values()) {
            List<Observation> w = new ArrayList<Observation>(walked);
            w.add(new Observation(current, v));
            makeCombinationsRecursively(src, dest, w);
        }
    }

    public String toString() {
        String ret = name + ": [";
        if (!parents.isEmpty()) {
            for (RandomVariable v : parents) {
                ret += v.name + ", ";
            }
            ret = ret.substring(0, ret.length() - 2);
        }
        ret += "]";
        return ret;
    }
}
