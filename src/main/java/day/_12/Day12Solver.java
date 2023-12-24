package day._12;

import util.common.Solver;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Day12Solver extends Solver<Long> {
    public Day12Solver(String fileName) {
        super(fileName);
    }

    @Override
    public Long solvePartOne() {
        return puzzle.stream()
                .mapToLong(line -> {
                    String[] record = line.split(" ");
                    return countArrangements(record[0], Arrays.stream(record[1].split(",")).map(Integer::parseInt).collect(Collectors.toList()));
                })
                .sum();
    }

    public int countArrangements(String conditions, List<Integer> damagedGroups) {
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

        int result = 0;

        if (conditions.charAt(0) == '.' || conditions.charAt(0) == '?') {
            result += countArrangements(conditions.substring(1), damagedGroups);
        }

        if (conditions.charAt(0) == '#' || conditions.charAt(0) == '?') {
            if (conditions.length() >= damagedGroups.get(0) && !conditions.substring(0, damagedGroups.get(0)).contains(".") && conditions.charAt(damagedGroups.get(0)) != '#') {
                result += countArrangements(conditions.substring(damagedGroups.get(0) + 1), new ArrayList<>(damagedGroups.subList(1, damagedGroups.size())));
            }
        }
        return result;
    }

    @Override
    public Long solvePartTwo() {
        return null;
    }
}
