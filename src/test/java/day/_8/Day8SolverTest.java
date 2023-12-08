package day._8;

import org.junit.jupiter.api.Test;
import util.common.Solver;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class Day8SolverTest {
  private Solver<Long> solver;

  @Test
  public void testPartOne() {
    solver = new Day8Solver("day8-test1.txt");
    assertEquals(6, solver.solvePartOne());
  }

  @Test
  public void testPartTwo() {
    solver = new Day8Solver("day8-test2.txt");
    assertEquals(6, solver.solvePartTwo());
  }
}
