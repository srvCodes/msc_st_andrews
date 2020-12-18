/**
 * Main class
 */
public class A2main {
    /**
     * Function to call.
     *
     * @param args : args[0] is algorithm <SPX|SATX|RPX> while args[1] is the board ID
     */
    public static void main(String[] args) {
        String alogrithm = args[0];
        String worldName = args[1];

        char[][] world = World.valueOf(worldName).map;

        // print the original world
        Board board = new Board(world);
        System.out.print("Original world: ");
        board.printBoard();

        // call the superclass to initialize the states of the game
        TornadoSweeper ts = new TornadoSweeper(world);
        if (alogrithm.equals("RPX")) {
            RPX randomAgent = new RPX();
            randomAgent.randomProbe();
        } else if (alogrithm.equals("SPX")) {
            SPX singlePointAgent = new SPX();
            singlePointAgent.singlePointProbe();
        } else if (alogrithm.equals("SATX")) {
            SATX satAgent = new SATX();
            satAgent.checkSatisfiabilityAndMove();
        }
    }
}
