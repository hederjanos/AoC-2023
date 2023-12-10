package day._10;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import util.common.Solver;

public class Day10SolverTest {
    private Solver<Integer> solver;

    @Test
    public void testPartOne() {
        solver = new Day10Solver("day10-test.txt");
        Assertions.assertEquals(8, solver.solvePartOne());
    }

    @Test
    public void testPartTwo() {
        solver = new Day10Solver("day10-test.txt");
    }
}
