package day._18;

import org.junit.jupiter.api.Test;
import util.common.Solver;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class Day18SolverTest {
    private Solver<Long> solver;

    @Test
    public void testPartOne() {
        solver = new Day18Solver("day18-test.txt");
        assertEquals(62, solver.solvePartOne());
    }

    @Test
    public void testPartTwo() {
        solver = new Day18Solver("day18-test.txt");
    }
}
