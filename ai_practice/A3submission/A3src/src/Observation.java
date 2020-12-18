/**
 * Class to represent a node and its observed value.
 */
public class Observation implements Comparable<Observation> {
	public final RandomVariable node;
	public final DomainValue value;

	/**
	 * Constructor when initializing network.
	 * @param node is the variable being observed
	 * @param outcome is its observed value.
	 */
	public Observation(RandomVariable node, String outcome) {
		if (node.domain.containsKey(outcome)) {
			this.node = node;
			this.value = node.domain.get(outcome);
		} else
			throw new CustomError("Variable '" + node.name + "' does not contain the value '" + outcome + "'.");
	}

	/**
	 * Constructor called while querying.
	 * @param node is the variable being observed
	 * @param outcome is its observed value
	 */
	public Observation(RandomVariable node, DomainValue outcome) {
		if (node.domain.containsValue(outcome)) {
			this.node = node;
			this.value = outcome;
		} else
			throw new CustomError("null value not accepted for events.");
	}

	public boolean equals(Observation other) {
		return other.node == node && other.value == value;
	}

	public int hashCode() {
		return node.hashCode() * value.hashCode();
	}

	public boolean equals(Object other) {
		if (other instanceof Observation)
			return equals((Observation) other);
		return false;
	}

	@Override
	public int compareTo(Observation other) {
		if (node == other.node) {
			return value.compareTo(other.value);
		} else {
			return node.name.compareTo(other.node.name);
		}
	}

	public String toString() {
		return node.name + " = " + value.name;
	}
}
