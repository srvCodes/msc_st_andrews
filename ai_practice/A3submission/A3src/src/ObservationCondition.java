
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * Class to represent all possible observations of a node in sorted order.
 */
public class ObservationCondition implements Iterable<Observation> {
	private Observation[] observations;

	/**
	 * Constructor.
	 * @param l is the list of possible observations.
	 */
	public ObservationCondition(List<Observation> l) {
		Collections.sort(l);
		observations = l.toArray(new Observation[0]);
	}

	/**
	 * Test supporting map keying.
	 * @param other
	 * @return
	 */
	public boolean equals(ObservationCondition other) {
		if (observations.length != other.observations.length)
			return false;
		for (int i = 0; i < observations.length; i++)
			if (!observations[i].equals(other.observations[i]))
				return false;
		return true;
	}

	public boolean equals(Object other) {
		if (other instanceof ObservationCondition)
			return equals((ObservationCondition) other);
		return false;
	}

	public int hashCode() {
		int ret = 1;
		for (Observation e : observations)
			ret *= e.hashCode();
		return ret;
	}

	/**
	 * Check if the given observation holds.
	 * @param observation is to be checked
	 * @return true if holds.
	 */
	public boolean contains(Observation observation) {
		for (Observation e : observations)
			if (e.equals(observation))
				return true;
		return false;
	}

	public boolean contains(ObservationCondition other) {
		for (Observation e : other.observations)
			if (!contains(e))
				return false;
		return true;
	}

	public boolean mention(RandomVariable var) {
		for (Observation e : observations)
			if (e.node == var)
				return true;
		return false;
	}

	public String toString() {
		String ret = "[";
		for (Observation e : observations)
			ret += e + ", ";
		if (observations.length > 0)
			ret = ret.substring(0, ret.length() - 2);
		ret += "]";
		return ret;
	}

	@Override
	public Iterator<Observation> iterator() {
		return new ConditionItoreror();
	}

	public class ConditionItoreror implements Iterator<Observation> {

		private int index;

		public ConditionItoreror() {
			index = 0;
		}

		@Override
		public boolean hasNext() {
			return index < observations.length;
		}

		@Override
		public Observation next() {
			index++;
			return observations[index - 1];
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}

	}

}
