package day._15;

import org.junit.jupiter.api.Test;
import util.common.Solver;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class Day15SolverTest {
    private Solver<Integer> solver;

    @Test
    public void testPartOne() {
        solver = new Day15Solver("day15-test.txt");
        assertEquals(1320, solver.solvePartOne());

    }

    @Test
    public void testPartTwo() {
        solver = new Day15Solver("day15-test.txt");
        assertEquals(145, solver.solvePartTwo());
    }
}
