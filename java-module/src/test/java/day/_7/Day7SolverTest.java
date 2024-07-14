package day._7;

import org.junit.jupiter.api.Test;
import util.common.Solver;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class Day7SolverTest {
  private Solver<Integer> solver;

  @Test
  public void testPartOne() {
    solver = new Day7Solver("day7-test.txt");
    assertEquals(6440, solver.solvePartOne());
  }

  @Test
  public void testPartTwo() {
    solver = new Day7Solver("day7-test.txt");
    assertEquals(5905, solver.solvePartTwo());
  }
}
