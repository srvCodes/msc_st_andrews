import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * Unit tests for Tornado class
 */
public class TestTornadoSweeper {

    @Test
    public static void testNumOfTornadoes() {
        char[][] world = World.TEST1.map;
        TornadoSweeper ts = new TornadoSweeper(world);

        Assert.assertTrue("Test 1 passed for no. of tornadoes.", ts.getNumOfTornadoes() == 3);
    }

    @Test
    public static void testVisitedCells() {
        char[][] world = World.TEST1.map;
        TornadoSweeper ts = new TornadoSweeper(world);


        Cell cellToProbe = new Cell(2,2);
        ts.updateAgentWorld(cellToProbe);

        Cell v1 = new Cell(2,1);
        Cell v2 = new Cell(1,1);
        Cell v3 = new Cell(1,2);

        List<Cell> expectedVisitedCells = new ArrayList<>();
        expectedVisitedCells.add(cellToProbe);
        expectedVisitedCells.add(v2);
        expectedVisitedCells.add(v3);
        expectedVisitedCells.add(v1);

        Assert.assertArrayEquals("Test 2 passed for visited cells", expectedVisitedCells.toArray(), ts.getVisited().toArray());
        Assert.assertTrue("Test 3 passed for updating agent world", ts.getAgentWorld()[2][1] == '2');
    }

    @Test
    public static void testGetFirstCellsToProbe() {
        char[][] world = World.M10.map;
        TornadoSweeper ts1 = new TornadoSweeper(world);
        Assert.assertTrue(ts1.getStep() == 0);

        Cell firstCellToProbe = ts1.getFirstCellsToProbe();
        ts1.updateAgentWorld(firstCellToProbe);
        Assert.assertTrue("Test 4 passed for probing top-left corner cell on first step",
                firstCellToProbe.getRow() == 0 && firstCellToProbe.getCol() == 0);

        // increment step counter for the game
        ts1.setStep(ts1.getStep() + 1);
        Cell secondCellToProbe = ts1.getFirstCellsToProbe();
        ts1.updateAgentWorld(secondCellToProbe);
        Assert.assertTrue("Test 5 passed for probing cell in centre on second step",
                secondCellToProbe.getRow() == 3 && secondCellToProbe.getCol() == 3);
    }

    public static void main(String[] args) {
        testNumOfTornadoes();
        testVisitedCells();
        testGetFirstCellsToProbe();
    }
}
