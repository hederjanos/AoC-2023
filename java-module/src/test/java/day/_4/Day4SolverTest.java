package day._4;

import org.junit.jupiter.api.Test;
import util.common.Solver;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class Day4SolverTest {
  private Solver<Integer> solver;

  @Test
  public void testPartOne() {
    solver = new Day4Solver("day4-test.txt");
    assertEquals(13, solver.solvePartOne());
  }

  @Test
  public void testPartTwo() {
    solver = new Day4Solver("day4-test.txt");
    assertEquals(30, solver.solvePartTwo());
  }
}
