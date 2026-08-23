package day._4;

import util.common.Solver;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Day4Solver extends Solver<Integer> {
    private final Card[] cards;

    public Day4Solver(String fileName) {
        super(fileName);
        cards = puzzle.stream().map(Card::from).toArray(Card[]::new);
    }

    @Override
    public Integer solvePartOne() {
        return Arrays.stream(cards)
                .mapToInt(Card::matches)
                .map(matches -> matches == 0 ? 0 : (1 << (matches - 1)))
                .sum();
    }

    @Override
    public Integer solvePartTwo() {
        int[] counter = new int[cards.length];
        Arrays.fill(counter, 1);

        for (int i = 0; i < counter.length; i++) {
            int matches = cards[i].matches();
            for (int j = 1; j <= matches; j++) {
                if (i + j < counter.length) {
                    counter[i + j] += counter[i];
                }
            }
        }
        return Arrays.stream(counter).sum();
    }

    private record Card(int matches) {
        private static final Pattern NUMBER_PATTERN = Pattern.compile("\\d+");

        static Card from(String line) {
            String[] numGroups = line.split(":", 2)[1].split("\\|", 2);

            Set<Integer> winningNums = extractInts(numGroups[0]);
            Set<Integer> ownedNums = extractInts(numGroups[1]);

            int matches = (int) winningNums.stream().filter(ownedNums::contains).count();

            return new Card(matches);
        }

        static Set<Integer> extractInts(String numbers) {
            Matcher numberMatcher = NUMBER_PATTERN.matcher(numbers);
            Set<Integer> integers = new HashSet<>();
            while (numberMatcher.find()) {
                integers.add(Integer.parseInt(numberMatcher.group()));
            }
            return integers;
        }
    }
}