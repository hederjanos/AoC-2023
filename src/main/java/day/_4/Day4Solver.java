package day._4;

import util.common.Solver;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Day4Solver extends Solver<Integer> {
    private final Pattern cardPattern = Pattern.compile("^Card\\s+(\\d+):");
    private final String BAR = "\\|";
    private final String NUMBER = "\\d+";
    private final Set<Card> cards;

    public Day4Solver(String fileName) {
        super(fileName);
        cards = puzzle.stream().map(this::parseACard).collect(Collectors.toSet());
    }

    private Card parseACard(String line) {
        Matcher matcher = cardPattern.matcher(line);
        int cardId = 0;
        if (matcher.find()) {
            cardId = Integer.parseInt(matcher.group(1));
        }
        Set<Integer> winningNums = null;
        Set<Integer> ownedNums = null;
        String[] groups = line.replaceFirst(cardPattern.pattern(), "").split(BAR);
        for (int i = 0; i < groups.length; i++) {
            String group = groups[i];
            Set<Integer> integers = new HashSet<>();
            Matcher matcher1 = Pattern.compile(NUMBER).matcher(group);
            while (matcher1.find()) {
                integers.add(Integer.parseInt(matcher1.group()));
            }
            if (i == 0) {
                winningNums = integers;
            } else {
                ownedNums = integers;
            }
        }
        return new Card(cardId, winningNums, ownedNums);
    }

    @Override
    public Integer solvePartOne() {
        return cards.stream().mapToInt(card -> (int) Math.pow(2, card.getNumberOfMatches() - 1)).sum();
    }

    @Override
    public Integer solvePartTwo() {
        return null;
    }

    private static class Card {
        int id;
        Set<Integer> winningNums;
        Set<Integer> ownedNums;

        public Card(int id, Set<Integer> winningNums, Set<Integer> ownedNums) {
            this.id = id;
            this.winningNums = winningNums;
            this.ownedNums = ownedNums;
        }

        int getNumberOfMatches() {
            return (int) winningNums.stream().filter(n -> ownedNums.contains(n)).count();
        }
    }
}
