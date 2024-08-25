package day._9;

import util.common.Solver;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class Day9Solver extends Solver<Long> {
    private final List<List<Long>> histories;

    public Day9Solver(String fileName) {
        super(fileName);
        histories = parseInput();
    }

    private List<List<Long>> parseInput() {
        return puzzle.stream()
                .map(line -> {
                    String[] numbers = line.split(" ");
                    return Arrays.stream(numbers)
                            .map(Long::parseLong)
                            .collect(Collectors.toList());
                })
                .collect(Collectors.toList());
    }

    @Override
    public Long solvePartOne() {
        return histories.stream().mapToLong(this::processHistory).sum();
    }

    private long processHistory(List<Long> history) {
        return calculateDiffLists(history)
                .stream()
                .mapToLong(nums -> nums.get(nums.size() - 1))
                .sum();
    }

    private List<List<Long>> calculateDiffLists(List<Long> history) {
        List<List<Long>> diffLists = new ArrayList<>();
        diffLists.add(history);
        while (!isAllZero(history)) {
            List<Long> diffs = new ArrayList<>();
            for (int i = 1; i < history.size(); i++) {
                diffs.add(history.get(i) - history.get(i - 1));
            }
            diffLists.add(diffs);
            history = diffs;
        }
        return diffLists;
    }

    private boolean isAllZero(List<Long> history) {
        return history.stream().allMatch(num -> num == 0);
    }

    @Override
    public Long solvePartTwo() {
        return histories.stream()
                .map(history -> {
                    List<Long> copy = new ArrayList<>(history);
                    Collections.reverse(copy);
                    return copy;
                })
                .mapToLong(this::processHistory)
                .sum();
    }
}
