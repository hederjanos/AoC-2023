package day._11;

import util.common.Solver;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.LongStream;

public class Day11Solver extends Solver<Long> {
    private final Universe universe;

    public Day11Solver(String fileName) {
        super(fileName);
        universe = Universe.from(puzzle);
    }

    @Override
    public Long solvePartOne() {
        return universe.expand(2).calculateSumOfTheShortestPaths();
    }

    @Override
    public Long solvePartTwo() {
        return universe.expand(1_000_000).calculateSumOfTheShortestPaths();
    }

    private static final class Universe {
        private long minX = Long.MAX_VALUE;
        private long maxX = Long.MIN_VALUE;
        private long minY = Long.MAX_VALUE;
        private long maxY = Long.MIN_VALUE;
        private final List<Coordinate> galaxies;

        public Universe(List<Coordinate> galaxies) {
            this.galaxies = galaxies;
            for (Coordinate c : galaxies) {
                minX = Math.min(minX, c.x);
                maxX = Math.max(maxX, c.x);
                minY = Math.min(minY, c.y);
                maxY = Math.max(maxY, c.y);
            }
        }

        static Universe from(List<String> puzzle) {
            List<Coordinate> galaxies = IntStream.range(0, puzzle.size())
                    .boxed()
                    .flatMap(i -> IntStream.range(0, puzzle.get(i).length())
                            .filter(j -> puzzle.get(i).charAt(j) == '#')
                            .mapToObj(j -> new Coordinate(j, i)))
                    .toList();
            return new Universe(galaxies);
        }

        long width() {
            return maxX - minX;
        }

        long length() {
            return maxY - minY;
        }

        Universe expand(int expansion) {
            Set<Long> xs = galaxies.stream().map(c -> c.x).collect(Collectors.toSet());
            Set<Long> ys = galaxies.stream().map(c -> c.y).collect(Collectors.toSet());

            Set<Long> emptyXs = LongStream.range(0, width()).boxed().collect(Collectors.toSet());
            emptyXs.removeAll(xs);

            Set<Long> emptyYs = LongStream.range(0, length()).boxed().collect(Collectors.toSet());
            emptyYs.removeAll(ys);

            List<Coordinate> newGalaxies = galaxies.stream()
                    .map(c -> {
                        long countX = emptyXs.stream().filter(x -> x < c.x).count();
                        long countY = emptyYs.stream().filter(y -> y < c.y).count();
                        return new Coordinate(c.x + countX * (expansion - 1), c.y + countY * (expansion - 1));
                    })
                    .toList();
            return new Universe(newGalaxies);
        }

        long calculateSumOfTheShortestPaths() {
            return IntStream.range(0, galaxies.size())
                    .boxed()
                    .mapToLong(i -> {
                        Coordinate current = galaxies.get(i);
                        return IntStream.range(i + 1, galaxies.size())
                                .mapToObj(galaxies::get)
                                .mapToLong(other -> Math.abs(current.x - other.x) + Math.abs(current.y - other.y))
                                .sum();
                    })
                    .sum();
        }
    }

    private record Coordinate(long x, long y) {
    }
}
