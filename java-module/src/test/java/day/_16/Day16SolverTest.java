package day._16;

import org.junit.jupiter.api.Test;
import util.common.Solver;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class Day16SolverTest {
    private Solver<Integer> solver;

    @Test
    public void testPartOne() {
        solver = new Day16Solver("day16-test.txt");
        assertEquals(46, solver.solvePartOne());
    }

    @Test
    public void testPartTwo() {
        solver = new Day16Solver("day16-test.txt");
        assertEquals(51, solver.solvePartTwo());
    }
}
