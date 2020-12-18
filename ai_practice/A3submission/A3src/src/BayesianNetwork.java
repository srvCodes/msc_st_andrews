import java.util.*;

public class BayesianNetwork implements Iterable<String> {

	// the complete map for string name to variable object for all nodes
	public Map<String, RandomVariable> allNodes;

	/**
	 * Constructor.
	 */
	public BayesianNetwork() {
		allNodes = new LinkedHashMap<String, RandomVariable>();
	}

	/**
	 * Add node to the network from strings - add all its parents before adding the node
	 *
	 * 
	 * @param name is a string to represent the variable name like "VS" for visibility sensor
	 * @param values is a string array representing the binary domain of the variable - T/F
	 * @param parents is string array denotes the parent nodes of this variable - e.g. 'day' for 'peakTime'
	 * @param probabilities is string array describes the conditional or unconditional
	 *            probabilities of the variable.
	 */
	public void addNode(String name, String[] parents, String[] values, String[] probabilities) {
		System.out.print("\nAdding node '" + name + "'  with domain values" + Arrays.toString(values) + " to the network.");
		RandomVariable var = new RandomVariable(this, name);
		allNodes.put(name, var);
		try {
			for (String v : values) {
				// add values to var's domain
				var.addValue(v);
			}
			for (String p : parents) {
				var.addParent(p);
			}
			System.out.print("\nParents of the node are: ");
			for (RandomVariable r: var.parents) {
				System.out.print(" \"" + r.name + "\"" );
			}
			for (String p : probabilities) {
				var.addProbability(p);
			}
			for (RandomVariable v : var.parents) {
				v.children.add(var);
			}
		} catch (CustomError e) {
			// if addition of this node results in any error, remove it [ might not be an optimal way but works for
			// the hospital traffic problem ]
			allNodes.remove(name);
			throw e;
		}
	}

	/**
	 * Check if network contains a variable of give name.
	 */
	public boolean hasNode(String name) {
		return allNodes.containsKey(name);
	}

	/**
	 * Get the variable identified by name
	 * @param name is the name of var to be extracted.
	 * @return
	 */
	public RandomVariable getNode(String name) {
		if (allNodes.containsKey(name))
			return allNodes.get(name);
		else
			throw new RuntimeException("No such variable <" + name + ">.");
	}

	/**
	 * Convert string to its observation conditions
	 * 
	 * Taking a string describing a list of events like
	 * "CO=F, DAY=W"
	 */
	public ObservationCondition parseCondition(String line) {
		line = line.replaceAll("\\s+", "");
		String[] cond = line.split(","); // where cond (conditions) is like
											// ["a=true", "weather=sunny"]

		List<Observation> conditionList = new ArrayList<Observation>();
		for (String event : cond)
			if (!event.isEmpty())
				conditionList.add(parseObservation(event));
		return new ObservationCondition(conditionList);
	}

	/**
	 * Convert string to an event
	 * 
	 * Taking a string describing a event like "a = true" Requires a equation
	 * sign, a name on its left and a value on the right.
	 */
	public Observation parseObservation(String line) {
		line = line.replaceAll("\\s+", "");
		String[] e = line.split("=");
		System.out.print("\nnode: " + e[0]);
		if (allNodes.containsKey(e[0])) {
			System.out.print("\nYes");
			return allNodes.get(e[0]).parseObservation(line);
		}
		else
			throw new RuntimeException("No such variable <" + e[0] + ">.");
	}

	/**
	 * Query the conditional probability table of the given variable
	 */
	public double query(String name, String condition) {
		return getNode(name).getProbability(condition);
	}

	@Override
	public Iterator<String> iterator() {
		return allNodes.keySet().iterator();
	}
}
