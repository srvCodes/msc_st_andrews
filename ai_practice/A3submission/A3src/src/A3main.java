import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Main class to initialize the bayesian networks and perform inference using either VE or MC.
 */
public class A3main {

    /**
     * Main method.
     *
     * @param args : command-line arguments - args[0] = the inference algorithm: Variable elimination or Markov chain
     *             args[1] = number of queries the user wants to give
     *             args[2].. args[n] = the actual queries in all lowercase.
     */
    public static void main(String[] args) {
        try {
            int argNo = 0;
            String networkName = args[argNo++];
            String algorithm = args[argNo++];
            System.out.print("\nInitializing the network ..");
            BayesianNetwork network = null;
            if (networkName.equals("BNpart2")) {
                network = initializeBNPart2();
            } else if (networkName.equals("multiplyConnected")) {
                network = initializeNetworkOne();
            } else if (networkName.equals("singlyConnected")) {
                network = initializeNetworkTwo();
            }

            String lineHighlighter = "================ ".repeat(8);
            System.out.print("\n" + lineHighlighter);
            System.out.print("\nNetwork built! Conditional probabilities are: ");
            for (Map.Entry<String, RandomVariable> entry : network.allNodes.entrySet()) {
                entry.getValue().printConditionalProbabilities();
            }
            System.out.print("\n" + lineHighlighter);

            InferenceAlgo inferenceAlgo;
            List<String> results = new ArrayList<>();

            if (algorithm.equals("VE")) {
                inferenceAlgo = new VariableElimination(network);
            } else if (algorithm.equals("MC")) {
                inferenceAlgo = new MarkovChainGibbsSampling(network);
            } else {
                throw new CustomError("Please specify an inference algorithm: VE or MC?");
            }
            String query = args[argNo++];
            System.out.print("\n\nProcessing query: " + query);
            String result = inferenceAlgo.processQuery(GeneralMethods.mapQueryToDomain(query));

            System.out.print("\n");
            System.out.print("\nAnswer to query: " + result);

            System.out.print("\n");
        } catch (Exception e) {
            throw new CustomError("\nUsage: \n 1. Variable elimination: java A3main <VE> <numOfQueries> <Query1> <Query2> .." +
                    "\n 2. Markov Chain Monte CARlo: java A3main <MC> <numOfQueries> <numOfSamples> <Query1> <Query2> ..");
        }
    }

    public static BayesianNetwork initializeBNPart2() {
        BayesianNetwork net = new BayesianNetwork();
        net.addNode("FIRE", new String[0], new String[]{"T", "F"}, new String[]{"FIRE = T: 0.01", "FIRE = F: 0.99"});
        net.addNode("SMOKE", new String[]{"FIRE"}, new String[]{"T", "F"}, new String[]{
                "SMOKE = T, FIRE = T: 0.9",
                "SMOKE = F, FIRE = T: 0.1",
                "SMOKE = T, FIRE = F: 0.01",
                "SMOKE = F, FIRE = F: 0.99"
        });
        net.addNode("ALARM", new String[]{"FIRE"}, new String[]{"T", "F"}, new String[]{
                "ALARM = T, FIRE = T: 0.5",
                "ALARM = F, FIRE = T: 0.5",
                "ALARM = T, FIRE = F: 0.85",
                "ALARM = F, FIRE = F: 0.15"
        });
        net.addNode("LEAVING", new String[]{"ALARM"}, new String[]{"T", "F"}, new String[]{
                "LEAVING = T, ALARM = T: 0.88",
                "LEAVING = F, ALARM = T: 0.12",
                "LEAVING = T, ALARM = F: 0.0",
                "LEAVING = F, ALARM = F: 1.0",
        });
        net.addNode("REPORT", new String[]{"LEAVING"}, new String[]{"T", "F"}, new String[]{
                "REPORT = T, LEAVING = T: 0.75",
                "REPORT = F, LEAVING = T: 0.25",
                "REPORT = T, LEAVING = F: 0.01",
                "REPORT = F, LEAVING = F: 0.99",
        });
        return net;
    }

    public static BayesianNetwork initializeNetworkOne() {
        BayesianNetwork net = new BayesianNetwork();
        net.addNode("DAY", new String[0], new String[]{"T", "F"}, new String[]{"DAY = T: 0.78", "DAY = F: 0.22"});
        net.addNode("PT", new String[]{"DAY"}, new String[]{"T", "F"}, new String[]{
                "PT = T, DAY=T: 0.375",
                "PT = F, DAY=T: 0.625",
                "PT = T, DAY=F: 0.05",
                "PT = F, DAY=F: 0.95",
        });
        net.addNode("CAR", new String[]{"DAY"}, new String[]{"T", "F"}, new String[]{
                "CAR = T, DAY = T: 0.6",
                "CAR = F, DAY = T: 0.4",
                "CAR = T, DAY = F: 0.3",
                "CAR = F, DAY = F: 0.7"
        });
        net.addNode("TF", new String[]{"PT", "CAR"}, new String[]{"T", "F"}, new String[]{
                "TF = T, PT = T, CAR = T: 0.8",
                "TF = F, PT = T, CAR = T: 0.2",
                "TF = T, PT = T, CAR = F: 0.6",
                "TF = F, PT = T, CAR = F: 0.4",
                "TF = T, PT = F, CAR = T: 0.55",
                "TF = F, PT = F, CAR = T: 0.45",
                "TF = T, PT = F, CAR = F: 0.15",
                "TF = F, PT = F, CAR = F: 0.85"
        });
        net.addNode("CO", new String[]{"TF"}, new String[]{"T", "F"}, new String[]{
                "CO = T, TF = T: 0.95",
                "CO = F, TF = T: 0.05",
                "CO = T, TF = F: 0.05",
                "CO = F, TF = F: 0.95"
        });
        return net;
    }

    public static BayesianNetwork initializeNetworkTwo() {
        BayesianNetwork net = new BayesianNetwork();
        net.addNode("DAY", new String[0], new String[]{"T", "F"}, new String[]{"DAY = T: 0.78", "DAY = F: 0.22"});
        net.addNode("CAR", new String[]{"DAY"}, new String[]{"T", "F"}, new String[]{
                "CAR = T, DAY = T: 0.6",
                "CAR = F, DAY = T: 0.4",
                "CAR = T, DAY = F: 0.3",
                "CAR = F, DAY = F: 0.7"
        });
        net.addNode("TF", new String[]{"CAR"}, new String[]{"T", "F"}, new String[]{
                "TF = T, CAR = T: 0.8",
                "TF = F, CAR = T: 0.2",
                "TF = T, CAR = F: 0.6",
                "TF = F, CAR = F: 0.4"
        });
        net.addNode("CO", new String[]{"TF"}, new String[]{"T", "F"}, new String[]{
                "CO = T, TF = T: 0.95",
                "CO = F, TF = T: 0.05",
                "CO = T, TF = F: 0.05",
                "CO = F, TF = F: 0.95"
        });

        net.addNode("TCP", new String[]{"CO"}, new String[]{"T", "F"}, new String[]{
                "TCP = T, CO = T: 0.8",
                "TCP = F, CO = T: 0.2",
                "TCP = T, CO = F: 0.25",
                "TCP = F, CO = F: 0.75",
        });

        net.addNode("ESA", new String[]{"TCP"}, new String[]{"T", "F"}, new String[]{
                "ESA = T, TCP = T: 0.9",
                "ESA = F, TCP = T: 0.1",
                "ESA = T, TCP = F: 0.1",
                "ESA = F, TCP = F: 0.9",
        });
        return net;
    }
}
