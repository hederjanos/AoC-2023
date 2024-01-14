package day._19;

import org.junit.jupiter.api.Test;
import util.common.Solver;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class Day19SolverTest {
    private Solver<Long> solver;

    @Test
    public void testPartOne() {
        solver = new Day19Solver("day19-test.txt");
        assertEquals(19114, solver.solvePartOne());
    }

    @Test
    public void testPartTwo() {
        solver = new Day19Solver("day19-test.txt");
        assertEquals(167409079868000L, solver.solvePartTwo());
    }
}
