package day._12;

import util.common.Solver;

import java.util.*;

public class Day12Solver extends Solver<Long> {
    public Day12Solver(String fileName) {
        super(fileName);
    }

    @Override
    public Long solvePartOne() {
        return puzzle.stream().map(Springs::from).mapToLong(Springs::countArrangements).sum();
    }

    @Override
    public Long solvePartTwo() {
        return puzzle.stream().map(Springs::fromWithUnFolding).mapToLong(Springs::countArrangements).sum();
    }

    private record Springs(String conditions, List<Integer> damagedGroups) {
        private static final char OPERATIONAL = '.';
        private static final char DAMAGED = '#';
        private static final char UNKNOWN = '?';

        static Springs from(String line) {
            String[] record = line.split(" ");
            String conditions = record[0];
            List<Integer> groups = Arrays.stream(record[1].split(",")).map(Integer::parseInt).toList();
            return new Springs(conditions, groups);
        }

        static Springs fromWithUnFolding(String line) {
            String[] record = line.split(" ");
            String conditions = String.join(String.valueOf(UNKNOWN), Collections.nCopies(5, record[0]));
            List<Integer> groups = Arrays.stream(String.join(",", Collections.nCopies(5, record[1])).split(","))
                    .map(Integer::parseInt)
                    .toList();
            return new Springs(conditions, groups);
        }

        long countArrangements() {
            return countArrangements(conditions, damagedGroups, new HashMap<>());
        }

        private long countArrangements(String conditions, List<Integer> damagedGroups, Map<Springs, Long> memo) {
            Springs springs = new Springs(conditions, damagedGroups);
            if (memo.containsKey(springs)) {
                return memo.get(springs);
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

            memo.put(springs, result);

            return result;
        }

        private boolean isDamagedGroupCanBeFitted(String conditions, List<Integer> damagedGroups) {
            return conditions.length() >= damagedGroups.get(0)
                    && !conditions.substring(0, damagedGroups.get(0)).contains(String.valueOf(OPERATIONAL))
                    && conditions.charAt(damagedGroups.get(0)) != DAMAGED;
        }
    }
}
