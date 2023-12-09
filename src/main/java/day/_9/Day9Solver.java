package day._9;

import util.common.Solver;

import java.util.ArrayList;
import java.util.Arrays;
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
        return getCache(history)
                .stream()
                .mapToLong(nums -> nums.get(nums.size() - 1))
                .sum();
    }

    private List<List<Long>> getCache(List<Long> history) {
        List<List<Long>> cache = new ArrayList<>();
        cache.add(history);
        while (!isAllZero(history)) {
            List<Long> diffs = new ArrayList<>();
            for (int i = 1; i < history.size(); i++) {
                diffs.add(history.get(i) - history.get(i - 1));
            }
            cache.add(diffs);
            history = diffs;
        }
        return cache;
    }

    private boolean isAllZero(List<Long> history) {
        return history.stream().allMatch(num -> num == 0);
    }

    @Override
    public Long solvePartTwo() {
       return histories.stream().mapToLong(this::processHistory2).sum();
    }

    private long processHistory2(List<Long> history) {
        List<List<Long>> cache = getCache(history);
        Long first = cache.get(cache.size() - 1).get(0);
        for (int i = cache.size() - 1; i > 0; i--) {
            Long prevFirst = cache.get(i - 1).get(0);
            first = prevFirst - first;
        }
        return first;
    }
}
