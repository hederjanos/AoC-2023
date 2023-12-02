package day._2;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CubeConundrumSolverTest {

    private CubeConundrumSolver cubeConundrumSolver;

    @Test
    void testPartOne() {
        cubeConundrumSolver = new CubeConundrumSolver("day2-test1.txt");
        assertEquals(8, cubeConundrumSolver.solvePartOne());
    }

    @Test
    void testPartTwo() {
        cubeConundrumSolver = new CubeConundrumSolver("day2-test1.txt");
        assertEquals(2286, cubeConundrumSolver.solvePartTwo());
    }

}
