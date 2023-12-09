package day._9;

import org.junit.jupiter.api.Test;
import util.common.Solver;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class Day9SolverTest {
    private Solver<Long> solver;

    @Test
    public void testPartOne() {
        solver = new Day9Solver("day9-test.txt");
        assertEquals(114, solver.solvePartOne());
    }

    @Test
    public void testPartTwo() {
        solver = new Day9Solver("day9-test.txt");
        assertEquals(2, solver.solvePartTwo());
    }
}
