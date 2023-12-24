package day._11;

import util.common.Solver;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.LongStream;

public class Day11Solver extends Solver<Long> {

    private final Set<Coordinate> galaxies;

    public Day11Solver(String fileName) {
        super(fileName);
        galaxies = IntStream.range(0, puzzle.size())
                .boxed()
                .flatMap(i -> IntStream.range(0, puzzle.get(i).length())
                        .filter(j -> puzzle.get(i).charAt(j) == '#')
                        .mapToObj(j -> new Coordinate(j, i)))
                .collect(Collectors.toSet());
    }

    @Override
    public Long solvePartOne() {
        return solve(2);
    }

    private Long solve(int expansion) {
        Set<Long> xs = galaxies.stream().map(c -> c.x).collect(Collectors.toSet());
        Set<Long> ys = galaxies.stream().map(c -> c.y).collect(Collectors.toSet());

        Set<Long> emptyXs = LongStream.range(0, puzzle.get(0).length()).boxed().collect(Collectors.toSet());
        emptyXs.removeAll(xs);

        Set<Long> emptyYs = LongStream.range(0, puzzle.size()).boxed().collect(Collectors.toSet());
        emptyYs.removeAll(ys);

        List<Coordinate> newGalaxies = galaxies.stream()
                .map(c -> {
                    long countX = emptyXs.stream().filter(x -> x < c.x).count();
                    long countY = emptyYs.stream().filter(y -> y < c.y).count();
                    return new Coordinate(c.x + countX * expansion - countX, c.y + countY * expansion - countY);
                })
                .collect(Collectors.toList());

        return IntStream.range(0, newGalaxies.size())
                .boxed()
                .mapToLong(i -> {
                    Coordinate current = newGalaxies.get(i);
                    return IntStream.range(i + 1, newGalaxies.size())
                            .mapToObj(newGalaxies::get)
                            .mapToLong(other -> Math.abs(current.x - other.x) + Math.abs(current.y - other.y))
                            .sum();
                })
                .sum();
    }

    @Override
    public Long solvePartTwo() {
        return solve(1_000_000);
    }

    private static class Coordinate {
        long x;
        long y;

        public Coordinate(long x, long y) {
            this.x = x;
            this.y = y;
        }
    }
}
