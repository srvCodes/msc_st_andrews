import java.util.Iterator;

/**
 * Class implementing commonly used static functions by both inference methods.
 */
public class GeneralMethods {

	/**
	 * Method to convert queried variables to values, i.e. car becomes CAR =T while -car becomes CAR =F.
	 * @param str is the value in the query
	 * @return converted string
	 */
	public static String convert(String str) {
		String s = str.toUpperCase();
		if (s.startsWith("-")) {
			s = s.substring(1) + "=F";
		}
		else {
			s = s + "=T";
		}
		System.out.print("\nInequality for value " + str + " is: " + s);
		return s;
	}

	/**
	 * Method to match all user-supplied values in query to their nodes and assign value 'T' or 'F'
	 * @param query is an individual query
	 * @return converted string, e.g. input = P(ea|-co), return => "EA=T | CO=F"
	 */
	public static String mapQueryToDomain(String query) {
		String[] q = query.split("\\(")[1].split("\\)")[0].split("\\|");
//		System.out.print("\nTo be converted:" + query + "\n Length: " + q.length + " " + q[0] + " " + q[1]);
		String ret = GeneralMethods.convert(q[0]) + " | ";

		if (q.length > 1) {
			String[] evidences = q[1].split(",");
//			System.out.print("\n evidences length: " + evidences.length);
			for (int i = 0; i < evidences.length; i++) {
				ret += GeneralMethods.convert(evidences[i]) + ", ";
			}
			if (evidences.length > 0)
				ret = ret.substring(0, ret.length() - 2);
		}
		System.out.print("\ndone");
		return ret;
	}
}
