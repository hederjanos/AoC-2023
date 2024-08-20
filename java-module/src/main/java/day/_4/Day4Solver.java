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
        return Arrays.stream(cards).mapToInt(card -> (int) Math.pow(2, card.getNumberOfMatches() - 1)).sum();
    }

    @Override
    public Integer solvePartTwo() {
        int[] counter = new int[cards.length];
        Arrays.fill(counter, 1);
        for (int i = 0; i < counter.length; i++) {
            int matches = cards[i].getNumberOfMatches();
            for (int j = 1; j <= matches; j++) {
                if (i + j < counter.length) {
                    counter[i + j] += counter[i];
                }
            }
        }
        return Arrays.stream(counter).sum();
    }

    private record Card(Set<Integer> winningNums, Set<Integer> ownedNums) {
        static Card from(String line) {
            String[] numGroups = line.split(":", 2)[1].split("\\|", 2);
            return new Card(extractInts(numGroups[0]), extractInts(numGroups[1]));
        }

        static Set<Integer> extractInts(String numbers) {
            Matcher numberMatcher = Pattern.compile("\\d+").matcher(numbers);
            Set<Integer> integers = new HashSet<>();
            while (numberMatcher.find()) {
                integers.add(Integer.parseInt(numberMatcher.group()));
            }
            return integers;
        }

        int getNumberOfMatches() {
            return (int) winningNums.stream().filter(ownedNums::contains).count();
        }
    }
}
