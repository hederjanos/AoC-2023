package day._12;

import org.junit.jupiter.api.Test;
import util.common.Solver;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class Day12SolverTest {
    private Solver<Long> solver;

    @Test
    public void testPartOne() {
        solver = new Day12Solver("day12-test.txt");
        assertEquals(21, solver.solvePartOne());
    }

    @Test
    public void testPartTwo() {
        solver = new Day12Solver("day12-test.txt");
        assertEquals(525152, solver.solvePartTwo());
    }
}
