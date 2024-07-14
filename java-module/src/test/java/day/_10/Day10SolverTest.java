package day._10;

import org.junit.jupiter.api.Test;
import util.common.Solver;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class Day10SolverTest {
    private Solver<Integer> solver;

    @Test
    public void testPartOne() {
        solver = new Day10Solver("day10-test1.txt");
        assertEquals(8, solver.solvePartOne());
    }

    @Test
    public void testPartTwo() {
        solver = new Day10Solver("day10-test2.txt");
        assertEquals(10, solver.solvePartTwo());
    }
}
