package day._5;

import util.common.Solver;

import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Day5Solver extends Solver<Long> {
    private final List<Long> seeds;
    private final RangeMaps rangeMaps;

    public Day5Solver(String fileName) {
        super(fileName);
        seeds = initSeeds();
        rangeMaps = RangeMaps.from(puzzle);
    }

    private List<Long> initSeeds() {
        return Pattern.compile("\\d+").matcher(puzzle.getFirst()).results()
                .map(r -> Long.parseLong(r.group()))
                .toList();
    }

    @Override
    public Long solvePartOne() {
        return seeds.stream()
                .mapToLong(rangeMaps::getLocationFrom)
                .min()
                .orElseThrow();
    }

    @Override
    public Long solvePartTwo() {
        RangeMaps reversedMaps = rangeMaps.reverse();

        List<Range> seedRanges = IntStream.range(0, seeds.size() - 1)
                .filter(j -> j % 2 == 0)
                .mapToObj(j -> new Range(seeds.get(j), seeds.get(j) + seeds.get(j + 1) - 1))
                .toList();

        for (long testLocation = 0; ; testLocation++) {
            long mappedSeed = reversedMaps.getLocationFrom(testLocation);

            for (Range range : seedRanges) {
                if (range.contains(mappedSeed)) {
                    return testLocation;
                }
            }
        }
    }

    private record RangeMaps(List<RangeMap> rangeMaps) {
        static RangeMaps from(List<String> puzzle) {
            List<RangeMap> rangeMaps = new ArrayList<>();
            RangeMap rangeMap = null;

            for (int i = 2; i < puzzle.size(); i++) {
                if (puzzle.get(i).isEmpty()) {
                    rangeMaps.add(rangeMap);
                    continue;
                }
                boolean alphabetic = Character.isAlphabetic(puzzle.get(i).charAt(0));
                if (alphabetic) {
                    rangeMap = new RangeMap(puzzle.get(i).split(" ")[0]);
                } else {
                    String[] nums = puzzle.get(i).split(" ");
                    long destStart = Long.parseLong(nums[0]);
                    long srcStart = Long.parseLong(nums[1]);
                    long range = Long.parseLong(nums[2]);
                    rangeMap.addRangePair(new Range(srcStart, srcStart + range - 1), new Range(destStart, destStart + range - 1));
                }
                if (i == puzzle.size() - 1) {
                    rangeMaps.add(rangeMap);
                }
            }
            return new RangeMaps(rangeMaps);
        }

        long getLocationFrom(long src) {
            long current = src;
            for (RangeMap map : rangeMaps) {
                current = map.getDestination(current);
            }
            return current;
        }

        RangeMaps reverse() {
            List<RangeMap> reversedMaps = new ArrayList<>(rangeMaps);
            Collections.reverse(reversedMaps);
            List<RangeMap> flippedMaps = reversedMaps.stream()
                    .map(rangeMap -> {
                        RangeMap newRangeMap = new RangeMap(rangeMap.name);
                        rangeMap.rangePairs.forEach(pair -> newRangeMap.addRangePair(pair.dest, pair.src));
                        return newRangeMap;
                    })
                    .collect(Collectors.toList());
            return new RangeMaps(flippedMaps);
        }
    }

    private static class RangeMap {
        private final String name;

        private final List<RangePair> rangePairs = new ArrayList<>();

        RangeMap(String name) {
            this.name = name;
        }

        void addRangePair(Range src, Range dest) {
            rangePairs.add(new RangePair(src, dest));
        }

        long getDestination(long number) {
            for (RangePair pair : rangePairs) {
                if (pair.src().contains(number)) {
                    long diff = number - pair.src().start();
                    return pair.dest().start() + diff;
                }
            }
            return number;
        }
    }

    private record RangePair(Range src, Range dest) {
    }

    private record Range(long start, long end) {
        boolean contains(long number) {
            return number >= start && number <= end;
        }
    }
}
