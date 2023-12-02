package day._2;

import org.junit.jupiter.api.Test;
import util.common.Solver;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class Day2SolverTest {

    private Solver<Integer> cubeConundrumSolver;

    @Test
    void testPartOne() {
        cubeConundrumSolver = new Day2Solver("day2-test1.txt");
        assertEquals(8, cubeConundrumSolver.solvePartOne());
    }

    @Test
    void testPartTwo() {
        cubeConundrumSolver = new Day2Solver("day2-test1.txt");
        assertEquals(2286, cubeConundrumSolver.solvePartTwo());
    }

}
