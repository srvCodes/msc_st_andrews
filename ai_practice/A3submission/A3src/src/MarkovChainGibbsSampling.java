import java.util.*;
import java.util.Map.Entry;


public class MarkovChainGibbsSampling implements InferenceAlgo {

	Random r; // random value generator
	BayesianNetwork bn;
	int numOfIterations = 1500;

	
	public MarkovChainGibbsSampling(BayesianNetwork bn) {
		this.bn = bn;
		this.r = new Random();
	}

	/**
	 * Normalise the probability distribution of a variable's domain
	 * @param distribution - a map of value to probability distribution for each value of randomly sampled variable
	 */
	public void normaliseDistribution(Map<String, Double> distribution) {
		double sum = 0.0;
		for (Double d : distribution.values()) sum += d;
		for (Entry<String, Double> entry : distribution.entrySet()) {
			// normalize and convert to percentage
			entry.setValue(100.0 * (entry.getValue() / sum));
		}
	}

	/**
	 * Method to compute probability for a variable given its value and it's parents value in sample space
	 * 
	 * @param var - X_i, a random variable
	 * @param value - a possible outcome of X_i, i.e. x_i
	 * @param state - the current state, represented as a map of <node_name -> value>
	 * @return P(x_i | Parents(X_i))
	 */
	public double computePrbGivenParent(RandomVariable var, String value, Map<String, String> state)
			throws CustomError {
		List<String> cond = new ArrayList<String>();
		// make a string of conditions including the variable and its parents
		cond.add(var.name + "=" + value);
		for (RandomVariable parent : var.parents) {
			String parent_value = state.get(parent.name);
			cond.add(parent.name + "=" + parent_value);
		}
		// get probability for the list of conditions
		return bn.query(var.name, String.join(",", cond));
	}

	/**
	 * Method to perform the general Gibbs sampling step, i.e. sampling X_i from P(x_i | markov_blanket(X_i))
	 * given the current state configuration using formula:
	 * alpha * P(x_i | parents(X_i)) * PRODUCT_OVER: P(y_j | Parents(Y_j)): for each child Y_j of X_i
	 * 
	 * @param randomSampledVar - Xi, from whose domain a new value is sampled
	 * @param state - the current configuration of all variables' values
	 * @return xi
	 */
	public String getSample(RandomVariable randomSampledVar, Map<String, String> state) throws CustomError {
		// prob_distribution is the map of {ValueName : probability}
		Map<String, Double> prob_distribution = new HashMap<String, Double>();
		// calculate probability of each possible outcome of var
		for (String value : randomSampledVar.domain.keySet()) {
			double p = 0.0;
			// 1. P(x_i | Parents(X_i))
			p += computePrbGivenParent(randomSampledVar, value, state);
			// update value of variable in sample state
			state.put(randomSampledVar.name, new String(value));
			// 2. PRODUCT_OVER: P(y_j | Parents(Y_j)), for each child Y_j of X_i
			for (RandomVariable child : randomSampledVar.children) {
				p *= computePrbGivenParent(child, state.get(child.name), state);
			}
			prob_distribution.put(value, p);
		}
		normaliseDistribution(prob_distribution);

		// Approach 1: simply return the entry with max value: required far more no. of iterations for convergence
//		return  prob_distribution.entrySet().stream().max((entry1, entry2) -> entry1.getValue() > entry2.getValue()
//				? 1 : -1).get().getKey();
//		 generate a random double value to

		// Approach 2: generate a random no. 'r' (1-100) and return the key with a better chance of value greater than r
		double i = r.nextInt(100) + r.nextDouble();
		Double cummulation = 0.0;
		for (Entry<String, Double> ent : prob_distribution.entrySet()) {
			cummulation += ent.getValue();
			if (cummulation >= i)
				return ent.getKey();
		}

		return null; // this should never be returned
	}
	
	/**
	 * Method for gibbs sampling on the Bayesian network.
	 * 
	 * @param cause - name of the queried variable, eg. "EA"
	 * @param specifications - the list of query condition as well as evidences as strings, eg. {"EA=T", "CO=T", "VS=F"}
	 * @return answer to the query. 
	 */
	public String processQuery(String cause, List<String> specifications) throws CustomError {
		double probability;
		try {
			// see if the query can be answered directly
			return String.format("%.6f", bn.query(cause, String.join(",", specifications)));
			
		} catch (Exception e) { // otherwise we need to perform inference
			// this is mapping of the whole sample space < name : value >
			Map<String, String> state = new LinkedHashMap<String, String>(); 
			String queryValue = null;
			List<String> evidenceVariables = new ArrayList<String>();
			// put the evidence and its value to the sample space
			for (String spec : specifications) {
				String name = spec.split("=")[0];
				// check for the query condition since we have also added it to list of evidences
				if (!name.equals(cause)) {
					System.out.print("\nAdding variable '" + name + "' to evidence!" );
					String value = spec.split("=")[1];
					state.put(name, value);
					evidenceVariables.add(name);
				} else {
					// get the query value - 'T' for EA='T'
					queryValue = spec.split("=")[1];
				}
			}
			// get all non-evidence nodes
			List<String> nonEvidenceVars = new ArrayList<String>();
			for (String name : bn.allNodes.keySet()) {
				if (!evidenceVariables.contains(name)) {
					nonEvidenceVars.add(name);
					// get possible domain values for this node
					Object[] tmp = bn.getNode(name).domain.keySet().toArray();
					// choose a random value from the domain and add the mapping to sample space
					state.put(name, (String) tmp[r.nextInt(tmp.length)]);
				}
			}
			System.out.print("\nList of non-evidence variables: " + nonEvidenceVars);
			// Iterate through the list of non-evidences and 'flip' values (keeping evidence fixed)
			int counter = 0;  // to maintain count of occurrences of the queried value
			for (int i = 0; i < numOfIterations; ++i) {
				System.out.print("\nIteration: " + i);
				// randomly sample a value for one of the non-evidence variables X_i
				 Collections.shuffle(nonEvidenceVars);
				 RandomVariable var = bn.getNode(nonEvidenceVars.get(0));

				// draw a new sample using the variable's markov blanket
				String newVal = getSample(var, state);
				// update the variable's value in sample space
				state.put(var.name, newVal);
				// increase counter if the flipped value is equal to queried value
				if (state.get(cause).equals(queryValue)) {
					++counter;
					System.out.print("\nProbability of flipped value being queried value: " + (double)counter/i);
				}
			}
			probability = (double) counter/ numOfIterations;
			return String.format("%.6f", probability);
		}
	}
	
	@Override
	/**
	 * The ask method, will perform Gibbs sampling algorithm on the input
	 * network and return the result probability in string.
	 * 
	 * @param query - a String in the format "A = a1 | B = b2, C = c1", the spacing is not important.
	 * @return - the answer to the query
	 */
	public String processQuery(String query) {
		System.out.print("\nQuery: " + query);
		query = query.replaceAll("\\s+", "");
		String[] contents = query.split("\\|");
		List<String> specifications = new ArrayList<String>();
		// get the queried variable's name
		String queriedVar = contents[0].substring(0, contents[0].indexOf('='));
		// add the query condition to the evidence list since we need to flip its value too with each iteration
		specifications.add(contents[0]);
		// get all the comma-separated evidence
		if (contents.length > 1) {
			String[] tmp = contents[1].split(",");
			for (String elem : tmp) {
				specifications.add(GeneralMethods.convert(elem));
			}
		}
		System.out.print("\nComplete list of specifications: " + specifications);
		return processQuery(queriedVar, specifications);
	}
}
