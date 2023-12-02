package day._1;

import org.junit.jupiter.api.Test;
import util.common.Solver;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class Day1SolverTest {

    private Solver<Integer> day1Solver;

    @Test
    void testPartOne() {
        day1Solver = new Day1Solver("day1-test1.txt");
        assertEquals(142, day1Solver.solvePartOne());
    }

    @Test
    void testPartTwo() {
        day1Solver = new Day1Solver("day1-test2.txt");
        assertEquals(281, day1Solver.solvePartTwo());
    }

}
