package day._5;

import util.common.Solver;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.IntStream;
import java.util.stream.LongStream;

public class Day5Solver extends Solver<Long> {
    public static final String SPACE = " ";
    private final Pattern numPattern = Pattern.compile("\\d+");
    private final List<Long> seeds;
    private final List<RangeMap> rangeMaps;

    public Day5Solver(String fileName) {
        super(fileName);
        seeds = initSeeds();
        rangeMaps = initRangeMaps();
    }

    private List<Long> initSeeds() {
        List<Long> seeds = new ArrayList<>();
        String firstLine = puzzle.get(0);
        Matcher matcher = numPattern.matcher(firstLine);
        while (matcher.find()) {
            seeds.add(Long.parseLong(matcher.group()));
        }
        return seeds;
    }

    private List<RangeMap> initRangeMaps() {
        List<RangeMap> rangeMaps = new ArrayList<>();
        RangeMap rangeMap = null;

        for (int i = 2; i < puzzle.size(); i++) {
            if (puzzle.get(i).isEmpty()) {
                rangeMaps.add(rangeMap);
                continue;
            }
            boolean alphabetic = Character.isAlphabetic(puzzle.get(i).charAt(0));
            if (alphabetic) {
                rangeMap = new RangeMap(puzzle.get(i).split(SPACE)[0]);
            } else {
                String[] nums = puzzle.get(i).split(SPACE);
                long destStart = Long.parseLong(nums[0]);
                long srcStart = Long.parseLong(nums[1]);
                long range = Long.parseLong(nums[2]);
                rangeMap.addRange(new Range(srcStart, srcStart + range - 1), new Range(destStart, destStart + range - 1));
            }
            if (i == puzzle.size() - 1) {
                rangeMaps.add(rangeMap);
            }
        }
        return rangeMaps;
    }

    @Override
    public Long solvePartOne() {
        return seeds.stream().mapToLong(this::getLocation).min().getAsLong();
    }

    private long getLocation(Long source) {
        int i = 0;
        long destination = source;
        while (i < rangeMaps.size()) {
            RangeMap rangeMap = rangeMaps.get(i);
            destination = rangeMap.getDestination(destination);
            i++;
        }
        return destination;
    }

    //TODO BRUTE FORCE, SHOULD BE OPTIMIZED
    @Override
    public Long solvePartTwo() {
        return IntStream.iterate(0, j -> j < seeds.size() - 1, j -> j + 2)
                .mapToObj(j -> new Range(seeds.get(j), seeds.get(j) + seeds.get(j + 1) - 1))
                .mapToLong(range -> LongStream.iterate(range.start, j -> j <= range.end, j -> j + 1)
                        .map(this::getLocation)
                        .min().getAsLong())
                .min().getAsLong();
    }

    private static class RangeMap {
        String name;
        Map<Range, Range> rangeMap = new HashMap<>();

        RangeMap(String name) {
            this.name = name;
        }

        long getDestination(long number) {
            Optional<Map.Entry<Range, Range>> first = rangeMap.entrySet().stream()
                    .filter(entry -> entry.getKey().getDiff(number) > -1)
                    .findFirst();
            if (first.isPresent()) {
                Range source = first.get().getKey();
                Range dest = first.get().getValue();
                return dest.start + source.getDiff(number);
            } else {
                return number;
            }
        }

        void addRange(Range src, Range dest) {
            rangeMap.put(src, dest);
        }
    }

    private static class Range {
        long start;
        long end;

        Range(long start, long end) {
            this.start = start;
            this.end = end;
        }

        long getDiff(long number) {
            if (number >= start && end >= number) {
                return number - start;
            } else {
                return -1;
            }
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Range range = (Range) o;
            return start == range.start && end == range.end;
        }

        @Override
        public int hashCode() {
            return Objects.hash(start, end);
        }
    }
}
