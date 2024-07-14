package day._13;

import org.junit.jupiter.api.Test;
import util.common.Solver;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class Day13SolverTest {
    private Solver<Integer> solver;

    @Test
    public void testPartOne() {
        solver = new Day13Solver("day13-test.txt");
        assertEquals(405, solver.solvePartOne());
    }

    @Test
    public void testPartTwo() {
        solver = new Day13Solver("day13-test.txt");
        assertEquals(400, solver.solvePartTwo());
    }
}
