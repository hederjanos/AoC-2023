package day._14;

import org.junit.jupiter.api.Test;
import util.common.Solver;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class Day14SolverTest {
    private Solver<Integer> solver;

    @Test
    public void testPartOne() {
        solver = new Day14Solver("day14-test.txt");
        assertEquals(136, solver.solvePartOne());
    }

    @Test
    public void testPartTwo() {
        solver = new Day14Solver("day14-test.txt");
        assertEquals(64, solver.solvePartTwo());
    }
}
