
public class DomainValue implements Comparable<DomainValue> {
	public final RandomVariable variable;
	public final String name;

	DomainValue(String name, RandomVariable variable) {
		if (name == null || variable == null)
			throw new CustomError("Invalid Value content with name: " + name + " and variable: " + variable);
		this.name = name;
		this.variable = variable;
	}

	@Override
	public int compareTo(DomainValue other) {
		return other.name.compareTo(name);
	}

	public String toString() {
		return name;
	}
}
