package day._5;

import org.junit.jupiter.api.Test;
import util.common.Solver;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class Day5SolverTest {
  private Solver<Long> solver;

  @Test
  public void testPartOne() {
    solver = new Day5Solver("day5-test.txt");
    assertEquals(35, solver.solvePartOne());
  }

  @Test
  public void testPartTwo() {
    solver = new Day5Solver("day5-test.txt");
    assertEquals(46, solver.solvePartTwo());
  }
}
