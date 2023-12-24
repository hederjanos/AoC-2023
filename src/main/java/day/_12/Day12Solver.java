package day._12;

import util.common.Solver;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Day12Solver extends Solver<Long> {
    public Day12Solver(String fileName) {
        super(fileName);
    }

    @Override
    public Long solvePartOne() {
        return puzzle.stream()
                .mapToLong(line -> {
                    String[] record = line.split(" ");
                    String conditions = record[0];
                    List<Integer> groups = Arrays.stream(record[1].split(",")).map(Integer::parseInt).collect(Collectors.toList());
                    return countArrangements(conditions, groups, new HashMap<>());
                })
                .sum();
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
            return (conditions.contains("#")) ? 0 : 1;
        }

        if (conditions.length() < damagedGroups.get(0)) {
            return 0;
        } else if (conditions.length() == damagedGroups.get(0)) {
            return conditions.contains(".") ? 0 : 1;
        } else {
            int sumOfDamaged = damagedGroups.stream().reduce(Integer::sum).get();
            if (sumOfDamaged >= conditions.length()) {
                return 0;
            }
        }

        long result = 0;

        if (conditions.charAt(0) == '.' || conditions.charAt(0) == '?') {
            result += countArrangements(conditions.substring(1), damagedGroups, memo);
        }

        if (conditions.charAt(0) == '#' || conditions.charAt(0) == '?') {
            if (conditions.length() >= damagedGroups.get(0) && !conditions.substring(0, damagedGroups.get(0)).contains(".") && conditions.charAt(damagedGroups.get(0)) != '#') {
                result += countArrangements(conditions.substring(damagedGroups.get(0) + 1), new ArrayList<>(damagedGroups.subList(1, damagedGroups.size())), memo);
            }
        }

        memo.put(key, result);

        return result;
    }

    @Override
    public Long solvePartTwo() {
        return puzzle.stream()
                .mapToLong(line -> {
                    String[] record = line.split(" ");
                    String conditions = IntStream.rangeClosed(1, 5).mapToObj(i -> unfold(record[0], i, '?')).collect(Collectors.joining());
                    String groups = IntStream.rangeClosed(1, 5).mapToObj(i -> unfold(record[1], i, ',')).collect(Collectors.joining());
                    return countArrangements(conditions, Arrays.stream(groups.split(",")).map(Integer::parseInt).collect(Collectors.toList()), new HashMap<>());
                })
                .sum();
    }

    private String unfold(String string, int i, char ch) {
        String result = string;
        if (i < 5) {
            result += ch;
        }
        return result;
    }

    private static class Key {
        String conditions;
        List<Integer> damagedGroups;

        public Key(String conditions, List<Integer> damagedGroups) {
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
