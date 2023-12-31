package day._12;

import util.common.Solver;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Day12Solver extends Solver<Long> {
    private static final char OPERATIONAL = '.';
    private static final char DAMAGED = '#';
    private static final char UNKNOWN = '?';
    private static final char SPACE = ' ';
    private static final char COMMA = ',';

    public Day12Solver(String fileName) {
        super(fileName);
    }

    @Override
    public Long solvePartOne() {
        return puzzle.stream().mapToLong(this::processLine).sum();
    }

    private long processLine(String line) {
        String[] record = line.split(String.valueOf(SPACE));
        String conditions = record[0];
        List<Integer> groups = Arrays.stream(record[1].split(String.valueOf(COMMA))).map(Integer::parseInt).collect(Collectors.toList());
        return countArrangements(conditions, groups, new HashMap<>());
    }

    private long countArrangements(String conditions, List<Integer> damagedGroups, Map<Key, Long> memo) {
        Key key = new Key(conditions, damagedGroups);
        if (memo.containsKey(key)) {
            return memo.get(key);
        }

        if (conditions.isEmpty()) {
            return (damagedGroups.isEmpty()) ? 1 : 0;
        }

        if (damagedGroups.isEmpty()) {
            return (conditions.contains(String.valueOf(DAMAGED))) ? 0 : 1;
        }

        if (conditions.length() < damagedGroups.get(0)) {
            return 0;
        } else if (conditions.length() == damagedGroups.get(0)) {
            return conditions.contains(String.valueOf(OPERATIONAL)) ? 0 : 1;
        } else {
            int sumOfDamaged = damagedGroups.stream().reduce(Integer::sum).get();
            if (sumOfDamaged >= conditions.length()) {
                return 0;
            }
        }

        long result = 0;

        if (conditions.charAt(0) == OPERATIONAL || conditions.charAt(0) == UNKNOWN) {
            result += countArrangements(conditions.substring(1), damagedGroups, memo);
        }

        if (conditions.charAt(0) == DAMAGED || conditions.charAt(0) == UNKNOWN) {
            if (isDamagedGroupCanBeFitted(conditions, damagedGroups)) {
                result += countArrangements(conditions.substring(damagedGroups.get(0) + 1), new ArrayList<>(damagedGroups.subList(1, damagedGroups.size())), memo);
            }
        }

        memo.put(key, result);

        return result;
    }

    private boolean isDamagedGroupCanBeFitted(String conditions, List<Integer> damagedGroups) {
        return conditions.length() >= damagedGroups.get(0)
                && !conditions.substring(0, damagedGroups.get(0)).contains(String.valueOf(OPERATIONAL))
                && conditions.charAt(damagedGroups.get(0)) != DAMAGED;
    }

    @Override
    public Long solvePartTwo() {
        return puzzle.stream().mapToLong(this::processLineWithUnFolding).sum();
    }

    private long processLineWithUnFolding(String line) {
        String[] record = line.split(String.valueOf(SPACE));
        String conditions = IntStream.rangeClosed(1, 5).mapToObj(i -> unFold(record[0], i, UNKNOWN)).collect(Collectors.joining());
        String groups = IntStream.rangeClosed(1, 5).mapToObj(i -> unFold(record[1], i, COMMA)).collect(Collectors.joining());
        return countArrangements(conditions, Arrays.stream(groups.split(String.valueOf(COMMA))).map(Integer::parseInt).collect(Collectors.toList()), new HashMap<>());
    }

    private String unFold(String string, int i, char ch) {
        String result = string;
        if (i < 5) {
            result += ch;
        }
        return result;
    }

    private static class Key {
        String conditions;
        List<Integer> damagedGroups;

        Key(String conditions, List<Integer> damagedGroups) {
            this.conditions = conditions;
            this.damagedGroups = damagedGroups;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Key)) return false;
            Key key = (Key) o;
            return Objects.equals(conditions, key.conditions) && Objects.equals(damagedGroups, key.damagedGroups);
        }

        @Override
        public int hashCode() {
            return Objects.hash(conditions, damagedGroups);
        }
    }
}
