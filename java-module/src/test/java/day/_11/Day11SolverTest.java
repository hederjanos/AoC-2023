package day._11;

import org.junit.jupiter.api.Test;
import util.common.Solver;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class Day11SolverTest {
    private Solver<Long> solver;

    @Test
    public void testPartOne() {
        solver = new Day11Solver("day11-test.txt");
        assertEquals(374, solver.solvePartOne());
    }

    @Test
    public void testPartTwo() {
        solver = new Day11Solver("day11-test.txt");
        assertEquals(82000210, solver.solvePartTwo());
    }
}
