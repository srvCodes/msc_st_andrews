import org.junit.Assert;
import org.junit.Test;

/**
 * Unit tests for all three agents' core behaviour.
 */
public class AgentTests {

    @Test
    public static void RPXTest() {
        char[][] world = World.TEST1.map;
        TornadoSweeper ts = new TornadoSweeper(world);

        RPX randomAgent = new RPX();
        // probe top-left corner in first step
        Cell cell1 = ts.getFirstCellsToProbe();
        boolean res = randomAgent.unCoverandMarkCell(cell1.getRow(), cell1.getCol());
        ts.step += 1;

        for (int i = 0; i < 50; i++) {
            // Since [0,0] has been probed, a randomly generated cell must not be [0,0]
            Cell cellToProbe = randomAgent.selectRandomUnprobedCell();
            Assert.assertFalse("Test 0 passed for random cell",
                    cellToProbe.getRow() == 0 && cellToProbe.getCol() == 0);
        }
    }

    @Test
    public static void SPXTest() {
        char[][] world = World.TEST1.map;
        TornadoSweeper ts = new TornadoSweeper(world);

        SPX singlePointAgent = new SPX();
        // visit [0,0], [1,1] and [2,2]
        Cell cell1 = ts.getFirstCellsToProbe();
        ts.updateAgentWorld(cell1);
        ts.step += 1;

        Cell cell2 = ts.getFirstCellsToProbe();
        ts.updateAgentWorld(cell2);
        ts.step += 1;

        Cell cell3 = new Cell(2, 2);
        ts.updateAgentWorld(cell3);
        ts.step += 1;

        // AMN should now hold for [1,0]
        boolean isVisited = singlePointAgent.checkSatisfiabilityInNeighbours(1, 0);
        Assert.assertTrue("Test 1 passed for SPX agent", isVisited);
        // also, tornado cells should be updated to denote the flagged cell
        Assert.assertTrue("Test 2 passed for SPX agent", ts.tornadoCells.contains(new Cell(1, 0)));
    }

    @Test
    public static void SATXTest() {
        char[][] world = World.TEST1.map;
        TornadoSweeper ts = new TornadoSweeper(world);

        SATX satxAgent = new SATX();
        // visit [0,0], middle cell and [2,2]
        Cell cell1 = ts.getFirstCellsToProbe();
        ts.updateAgentWorld(cell1);
        ts.step += 1;

        Cell cell2 = ts.getFirstCellsToProbe();
        ts.updateAgentWorld(cell2);
        ts.step += 1;

        Cell cell3 = new Cell(2, 2);
        ts.updateAgentWorld(cell3);
        ts.step += 1;

        // KB && !D1,0 should be unsatisfiable
        boolean isVisited = satxAgent.checkAndVisitNeighbours(1, 0);
        Assert.assertTrue("Test 3 passed for SATX agent", isVisited);

        // KB && !D2,0 should also be unsatisfiable
        Assert.assertTrue("Test 4 passed for SATX agent", satxAgent.checkAndVisitNeighbours(2, 0));
    }

    public static void main(String[] args) {
        RPXTest();
        SPXTest();
        SATXTest();
    }
}
