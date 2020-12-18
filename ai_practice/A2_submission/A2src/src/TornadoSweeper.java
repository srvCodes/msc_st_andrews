import java.util.ArrayList;
import java.util.List;

/**
 * Parent class with all attributes defining state of the game.
 */
public class TornadoSweeper {
    protected static char[][] originalWorld;
    protected static char[][] agentWorld;
    protected static List<Cell> visited = new ArrayList<Cell>();
    protected static List<Cell> toBeVisited = new ArrayList<Cell>();
    protected static int numOfTornadoes;
    protected static int worldLength;
    protected static int step = 0;
    protected static boolean midCellProbed;
    protected static List<Cell> tornadoCells = new ArrayList<Cell>();

    /**
     * Constructor
     *
     * @param map is the board
     */
    public TornadoSweeper(char[][] map) {
        resetGame();
        worldLength = map.length;
        // original world is for reference to the board
        originalWorld = new char[worldLength][worldLength];
        // agent world contains the agent's view of board - initialized with '?'
        agentWorld = new char[worldLength][worldLength];
        setNumOfTornadoes();
        System.out.print("Setting the agent's world!!");
        for (int i = 0; i < worldLength; i++) {
            for (int j = 0; j < worldLength; j++) {
                originalWorld[i][j] = map[i][j];
                agentWorld[i][j] = '?';
                Cell cell = new Cell(i, j);
                // add all cells to be visited initially
                toBeVisited.add(cell);
            }
        }
    }

    /**
     * reset states of game just to avoid clashes with previous states of static variables
     */
    public void resetGame() {
        step = 0;
        midCellProbed = false;
        visited.clear();
        toBeVisited.clear();
        tornadoCells.clear();
    }

    public static List<Cell> getVisited() {
        return visited;
    }

    public static int getStep() {
        return step;
    }

    public static void setStep(int step) {
        TornadoSweeper.step = step;
    }

    public static boolean isMidCellProbed() {
        return midCellProbed;
    }

    public static void setMidCellProbed(boolean midCellProbed) {
        TornadoSweeper.midCellProbed = midCellProbed;
    }

    public static char[][] getAgentWorld() {
        return agentWorld;
    }

    public static void setAgentWorld(char[][] agentWorld) {
        TornadoSweeper.agentWorld = agentWorld;
    }

    public static void setVisited(List<Cell> visited) {
        TornadoSweeper.visited = visited;
    }

    public static List<Cell> getToBeVisited() {
        return toBeVisited;
    }

    public static void setToBeVisited(List<Cell> toBeVisited) {
        TornadoSweeper.toBeVisited = toBeVisited;
    }


    public TornadoSweeper() {
    }

    public static int getNumOfTornadoes() {
        return numOfTornadoes;
    }

    public static void setNumOfTornadoes(int numOfTornadoes) {
        TornadoSweeper.numOfTornadoes = numOfTornadoes;
    }

    /**
     * Figure out the number of tornadoes.
     */
    public void setNumOfTornadoes() {
        if (worldLength == 5) {
            this.numOfTornadoes = 5;
        } else if (worldLength == 7) {
            this.numOfTornadoes = 10;
        } else if (worldLength == 11) {
            this.numOfTornadoes = 28;
        } else if (worldLength == 3) {
            this.numOfTornadoes = 3;
        }
    }

    /**
     * Check if the cell is probed or not.
     *
     * @param row is row of the cell
     * @param col is column of the cell
     * @return
     */
    public boolean isCellProbed(final int row, final int col) {
        return visited.stream().anyMatch(o -> o.getRow() == row
                && o.getCol() == col);
    }

    /**
     * Get content of cell in original world.
     *
     * @param row is row of the cell
     * @param col is column of the cell
     * @return
     */
    public char getContentOfCell(int row, int col) {
        return originalWorld[row][col];
    }

    /**
     * Remove cell from list of cells based on row and column match
     *
     * @param myList is list of cells
     * @param cell   is the cell to be removed
     * @return
     */
    public List<Cell> removeCell(final List<Cell> myList, final Cell cell) {
        myList.removeIf(obj -> obj.getRow() == cell.getRow() && obj.getCol() == cell.getCol());
        return myList;
    }

    /**
     * Recursive function to probe all neighbours of a cell as long as we keep getting a neighbour with '0'
     *
     * @param cell is the cell to be probed
     */
    public void recursivelyProbeNeighbours(Cell cell) {
        System.out.print("\nCell [" + cell.getRow() + "," + cell.getCol() + "] contains '0', now recursively probing" +
                " its neighbours!");
        List<Cell> allNeighbours = cell.getNeighbours(originalWorld.length);
        for (int i = 0; i < allNeighbours.size(); i++) {
            Cell neighbour = allNeighbours.get(i);
            int row = neighbour.getRow();
            int col = neighbour.getCol();
            if (!isCellProbed(row, col)) {
                System.out.print("\n\nStep: " + step);
                char cellContent = getContentOfCell(row, col);
                System.out.print("\nProbe x y for uncovering the cell in [" + row + "," + col + "] coordinates" +
                        " with content: '" + cellContent + "'");
                visited.add(neighbour);
                toBeVisited = removeCell(toBeVisited, neighbour);
                agentWorld[row][col] = cellContent;
                Board board = new Board(agentWorld);
                System.out.print("\nAgent's view of the world at step " + step + ":");
                board.printBoard();
                if (cellContent == '0') {
                    recursivelyProbeNeighbours(neighbour);
                }
                if (i == allNeighbours.size() - 1) {
                    return;
                }
            }
        }
        return;
    }

    /**
     * Method to probe the [0,0] and the middle cell in first two steps of the game.
     *
     * @return
     */
    public Cell getFirstCellsToProbe() {
        int rowToProbe = -1;
        int colToProbe = -1;
        if (step == 0) {
            rowToProbe = 0;
            colToProbe = 0;
        } else if (!midCellProbed) {
            rowToProbe = (int) Math.floor(worldLength / 2.0);
            colToProbe = (int) Math.floor(worldLength / 2.0);
            this.midCellProbed = true;
        }
        Cell cellToProbe = new Cell(rowToProbe, colToProbe);
        return cellToProbe;
    }

    /**
     * Update agent world with content.
     *
     * @param row         is the row of the cell to be updated
     * @param col         is the column of the cell to be updated
     * @param cellContent is the content to be placed
     */
    public void probeCell(int row, int col, char cellContent) {
        System.out.print("\nProbing cell [" + row + "," + col + "] for uncovering content: " + cellContent);
        agentWorld[row][col] = cellContent;
    }

    /**
     * Place the content of a cell into the corresponding cell on agent's view of board
     *
     * @param cellToProbe is the cell whose content is to be placed.
     */
    public void updateAgentWorld(Cell cellToProbe) {
        int row = cellToProbe.getRow();
        int col = cellToProbe.getCol();
        char cellContent = getContentOfCell(row, col);
        probeCell(row, col, cellContent);
        visited.add(cellToProbe);
        toBeVisited = removeCell(toBeVisited, cellToProbe);
        if (cellContent == '0') {
            recursivelyProbeNeighbours(cellToProbe);
        }
    }

    public boolean isTornado(char cellContent) {
        return cellContent == 't';
    }

    /**
     * Prints the tornadoes found by the agent as well as the agent's view of the world.
     */
    public void printBoard() {
        System.out.print("\nCells that have been flagged as tornadoes so far: ");
        for (Cell cell : tornadoCells) {
            System.out.print("[" + cell.getRow() + "," + cell.getCol() + "]");
        }
        Board board = new Board(agentWorld);
        System.out.print("\nAgent's view of the world at step " + step + ":");
        board.printBoard();
    }

    /**
     * Flags a cell and adds it to the list of tornadoes.
     *
     * @param row is the row of the cell
     * @param col is the column of the cell
     */
    public void flagCell(int row, int col) {
        System.out.print("\nFlagging cell [" + row + "," + col + "] for danger!");
        agentWorld[row][col] = 'F';
        Cell cellToProbe = new Cell(row, col);
        tornadoCells.add(cellToProbe);
        printBoard();
    }

    /**
     * Method to place a flag in the rest of the unmarked cells after all but #T cells have been probed.
     * Called only when the agent wins the game, to show its full view of the world.
     */
    public void makeFinalBoard() {
        System.out.print("\n\nNow preparing final board (by placing flags yet to be visited) " +
                "to show you what the agent thinks the world looks like..");
        for (Cell cell : toBeVisited) {
            if (agentWorld[cell.getRow()][cell.getCol()] == '?') {
                agentWorld[cell.getRow()][cell.getCol()] = 'F';
            }
        }
        System.out.print("\nFinal view of the agent's world: ");
        Board board = new Board(agentWorld);
        board.printBoard();
    }
}
