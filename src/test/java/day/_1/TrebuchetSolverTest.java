package day._1;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TrebuchetSolverTest {

    private TrebuchetSolver trebuchetSolver;

    @Test
    void testPartOne() {
        trebuchetSolver = new TrebuchetSolver("day1-test1.txt");
        assertEquals(142, trebuchetSolver.solvePartOne());
    }

    @Test
    void testPartTwo() {
        trebuchetSolver = new TrebuchetSolver("day1-test2.txt");
        assertEquals(281, trebuchetSolver.solvePartTwo());
    }

}
