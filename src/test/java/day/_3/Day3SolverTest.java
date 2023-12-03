package day._3;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import util.common.Solver;

public class Day3SolverTest {
    private Solver<Integer> solver;

    @Test
    public void testPartOne() {
        solver = new Day3Solver("day3-test.txt");
        Assertions.assertEquals(4361, solver.solvePartOne());
    }

    @Test
    public void testPartTwo() {
        solver = new Day3Solver("day3-test.txt");
        Assertions.assertEquals(467835, solver.solvePartTwo());
    }
}
