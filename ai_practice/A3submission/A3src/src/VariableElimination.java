import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class VariableElimination implements InferenceAlgo{

	BayesianNetwork network;

	/**
	 * Constructor, specify the net work to be used.
	 *
	 */
	public VariableElimination(BayesianNetwork network) {
		this.network = network;
	}

	/**
	 * The ask method, will execute variable elimination algorithm on the input
	 * network and return the result value in string.
	 *
	 * @param query
	 *            - a String in the format "A = a1 | B = b2, C = c1", the
	 *            spacing is not important.
	 * @return - the result value in string.
	 */
	@Override
	public String processQuery(String query) {
		String[] q = query.split("\\|");
		return processQuery(q[0], q[1]);
	}

	public void getAllNodesInRoutes(String queryVar, String evidenceVar, List<String>Nodes) {
		if(!Nodes.contains(queryVar)) {
			Nodes.add(queryVar);
		}
		List<RandomVariable> parents = network.allNodes.get(queryVar).parents;
		for (RandomVariable parent: parents) {
			if (parent.name.equals(evidenceVar)) {
				if (!Nodes.contains(parent.name)) {
					Nodes.add(parent.name);
				}
				return;
			}
			getAllNodesInRoutes(parent.name, evidenceVar, Nodes);
		}
	}
	/**
	 * The underline variable elimination implementation.
	 */
	public String processQuery(String queriedVar, String evidences) {
		// Get target event and evidence objects.
		Observation target = network.parseObservation(queriedVar);
		ObservationCondition evidence = network.parseCondition(evidences);
		List<String> nodesInRoutes = new ArrayList<>();
		for (Observation o: evidence) {
			System.out.print("\nEvidence: " + o.node.name + " Value:" + o.value + " ");
			getAllNodesInRoutes(target.node.name, o.node.name, nodesInRoutes);
		}
		nodesInRoutes.forEach(System.out::println);
		// To eliminate in reverse topological ordering.
		// Assume the insertion order of the network is topologically sorted,
		// which is the case in our implementation.
		List<RandomVariable>order = new ArrayList<>();
		for (RandomVariable v : network.allNodes.values()) {
//			if (nodesInRoutes.contains(v.name)) {
//				System.out.print("\n" + v.name);
				order.add(0, v);
//			}
		}
		// For each variable, make it into a factor.
		List<Factorize> factors = new ArrayList<Factorize>();
		for (RandomVariable v : order) {
			System.out.print("\nFactor for variable: '" + v.name + "':");
			factors.add(new Factorize(v, evidence));

			// if the variable is a hidden variable, then perform sum out
			if (target.node != v && !evidence.mention(v)) {
				System.out.print("\nBefore factorization: ");
				for (Factorize f : factors) {
					f.printFactor();
				}
				System.out.print("\nEliminating variable '" + v.name + "' as it is neither evidence nor queried.");	
				Factorize temp = factors.get(0);
				for (int i = 1; i < factors.size(); i++)
					temp = temp.join(factors.get(i));
				System.out.print("\nAfter factorization: ");
				for (Factorize f: factors) {
					f.printFactor();
				}
				temp.eliminate(v);
				factors.clear();
				factors.add(temp);
			}
		}

		// Point wise product of all remaining factors.
		Factorize result = factors.get(0);
		for (int i = 1; i < factors.size(); i++)
			result = result.join(factors.get(i));

		// Normalize the result factor
		result.normalise();

		// Return the result matching the query in string format.
		return String.format("%.6f", result.p.get(new ObservationCondition(Arrays.asList(target))));
	}
}