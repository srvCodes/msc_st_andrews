import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;


/**
 * The Factorize implementation in java
 *
 * This class is the support data structure for the RandomVariable Elimination
 * algorithm, where a factor is a matrix recording the probabilities of
 * variables.
 */
public class Factorize {

	public List<RandomVariable> variables;
	public Map<ObservationCondition, Double> p;

	public void printFactor() {
		System.out.print("\n List of random variables: ");
		for (RandomVariable var: variables) {
			System.out.print(var.name);
		}
		System.out.print("\nObservation conditions: {");
		for (Map.Entry<ObservationCondition, Double> entry: p.entrySet()) {
			for (Observation o: entry.getKey()) {
				System.out.print(" Parent: " + o.node.name + " Value: " + o.value);
			}
			System.out.print("}, Probability: " + entry.getValue() + "\n");
		}
	}
	/**
	 * Construct from variable with evidence
	 *
	 * The variable will be eliminated from the factor if it is in evidence to
	 * improve performance (so factors are kept small as their product grow
	 * exponentially in size).
	 */
	public Factorize(RandomVariable v, ObservationCondition evidence) {
		variables = new ArrayList<RandomVariable>(v.parents);
		variables.add(v);
		p = new HashMap<ObservationCondition, Double>(v.probabilities);

		for (Observation e : evidence) {
			if (variables.contains(e.node)) {
				Map<ObservationCondition, Double> newP = new HashMap<ObservationCondition, Double>();
				for (ObservationCondition c : p.keySet())
					if (c.contains(e))
						newP.put(c, p.get(c));
				p = newP;
				eliminate(e.node);
			}
		}
//		printFactor();
	}

	private Factorize(List<RandomVariable> v, Map<ObservationCondition, Double> p) {
		variables = v;
		this.p = p;
	}

	/**
	 * Index the factor by condition will return the corresponding probability.
	 */
	public Double get(ObservationCondition cond) {
		return p.get(cond);
	}

	/**
	 * Eliminate a variable from factor by sum out
	 *
	 * The variable will be deleted from the factor and its probability will be
	 * summed up by remaining variables.
	 */
	public void eliminate(RandomVariable var) {
		if (!variables.remove(var))
			throw new RuntimeException("This factor does not contain the variable <" + var.name + "> to eliminate.");

		Map<ObservationCondition, Double> newP = new HashMap<ObservationCondition, Double>();
		for (ObservationCondition cond : RandomVariable.allConditions(variables)) {
			newP.put(cond, 0.0);
		}

		for (ObservationCondition cond : newP.keySet())
			for (ObservationCondition oldC : p.keySet())
				if (oldC.contains(cond))
					newP.put(cond, newP.get(cond) + p.get(oldC));
		p = newP;
	}

	/**
	 * Join two factor by point wise product.
	 *
	 * A new factor will be generated form the two factors containing all variable involved.
	 * Its probability will be the product of the two factors.
	 */
	public Factorize join(Factorize other) {

		// Retrieve the variables;
		List<RandomVariable> newVars = new ArrayList<RandomVariable>(variables);
		newVars.addAll(other.variables);
		newVars = new ArrayList<RandomVariable>(new HashSet<RandomVariable>(newVars));

		// compute the joined probability table;
		Map<ObservationCondition, Double> newP = new HashMap<ObservationCondition, Double>();
		for (ObservationCondition cond : RandomVariable.allConditions(newVars)) {
			Double prob = 1.0;

			for (ObservationCondition c : other.p.keySet())
				if (cond.contains(c))
					prob *= other.get(c);

			for (ObservationCondition c : p.keySet())
				if (cond.contains(c))
					prob *= get(c);

			newP.put(cond, prob);
		}

		return new Factorize(newVars, newP);
	}

	/**
	 * Normalize the factor so probability sum to 1.
	 */
	public void normalise() {
		Double sumP = 0.0;
		for (Double d : p.values())
			sumP += d;

		Map<ObservationCondition, Double> newP = new HashMap<ObservationCondition, Double>();
		for (ObservationCondition c : p.keySet()) {
			newP.put(c, p.get(c) / sumP);
		}
		p = newP;
	}

	public String toString() {
		String ret = "";
		for (ObservationCondition c : p.keySet()) {
			ret += "\n" + c.toString() + ": " + p.get(c);
		}
		return ret;
	}
}