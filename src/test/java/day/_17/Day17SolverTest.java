package day._17;

import org.junit.jupiter.api.Test;
import util.common.Solver;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class Day17SolverTest {
    private Solver<Integer> solver;

    @Test
    public void testPartOne() {
        solver = new Day17Solver("day17-test.txt");
        assertEquals(102, solver.solvePartOne());
    }

    @Test
    public void testPartTwo() {
        solver = new Day17Solver("day17-test.txt");
        assertEquals(94, solver.solvePartTwo());
    }
}
