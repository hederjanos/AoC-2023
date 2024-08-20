package day._5;

import util.common.Solver;

import java.util.*;
import java.util.regex.Matcher;
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
        List<Long> seeds = new ArrayList<>();
        Matcher matcher = Pattern.compile("\\d+").matcher(puzzle.get(0));
        while (matcher.find()) {
            seeds.add(Long.parseLong(matcher.group()));
        }
        return seeds;
    }

    @Override
    public Long solvePartOne() {
        return seeds.stream().mapToLong(rangeMaps::getLocationFrom).min().getAsLong();
    }

    @Override
    public Long solvePartTwo() {
        RangeMaps reversedMaps = rangeMaps.reverse();

        List<Range> ranges = IntStream.range(0, seeds.size() - 1)
                .filter(j -> j % 2 == 0)
                .mapToObj(j -> new Range(seeds.get(j), seeds.get(j) + seeds.get(j + 1) - 1))
                .toList();

        long testSrc;
        for (testSrc = 0; ; testSrc++) {
            long dest = reversedMaps.getLocationFrom(testSrc);
            Optional<Range> found = ranges.stream().filter(r -> r.getDiffFromStart(dest) > -1).findFirst();
            if (found.isPresent()) {
                break;
            }
        }
        return testSrc;
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
                    rangeMap.addRange(new Range(srcStart, srcStart + range - 1), new Range(destStart, destStart + range - 1));
                }
                if (i == puzzle.size() - 1) {
                    rangeMaps.add(rangeMap);
                }
            }
            return new RangeMaps(rangeMaps);
        }

        long getLocationFrom(Long src) {
            return rangeMaps.stream()
                    .reduce(src,
                            (dest, rangeMap) ->
                                    rangeMap.findRangePair(dest)
                                            .map(pair -> rangeMap.getDestination(pair, dest))
                                            .orElse(dest),
                            (a, b) -> b);
        }

        RangeMaps reverse() {
            List<RangeMap> reversedMaps = new ArrayList<>(rangeMaps);
            Collections.reverse(reversedMaps);
            List<RangeMap> flippedMaps = reversedMaps.stream()
                    .map(rangeMap -> {
                        RangeMap newRangeMap = new RangeMap(rangeMap.name);
                        rangeMap.rangeMap.forEach((k, v) -> newRangeMap.addRange(v, k));
                        return newRangeMap;
                    })
                    .collect(Collectors.toList());
            return new RangeMaps(flippedMaps);
        }
    }

    private static class RangeMap {
        private final String name;
        private final Map<Range, Range> rangeMap = new HashMap<>();

        RangeMap(String name) {
            this.name = name;
        }

        void addRange(Range src, Range dest) {
            rangeMap.put(src, dest);
        }

        Optional<Map.Entry<Range, Range>> findRangePair(long number) {
            return rangeMap.entrySet().stream()
                    .filter(entry -> entry.getKey().getDiffFromStart(number) > -1)
                    .findFirst();
        }

        long getDestination(Map.Entry<Range, Range> rangePair, long number) {
            Range src = rangePair.getKey();
            Range dest = rangePair.getValue();
            return dest.start + src.getDiffFromStart(number);
        }
    }

    private record Range(long start, long end) {
        long getDiffFromStart(long number) {
            return number >= start && end >= number ? number - start : -1;
        }
    }
}
