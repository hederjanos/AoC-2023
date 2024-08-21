package day._6;

import org.junit.jupiter.api.Test;
import util.common.Solver;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class Day6SolverTest {
    private Solver<Long> solver;

    @Test
    public void testPartOne() {
        solver = new Day6Solver("day6-test.txt");
        assertEquals(288, solver.solvePartOne());
    }

    @Test
    public void testPartTwo() {
        solver = new Day6Solver("day6-test.txt");
        assertEquals(71503, solver.solvePartTwo());
    }
}
