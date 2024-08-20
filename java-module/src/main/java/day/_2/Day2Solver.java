package day._2;

import util.common.Solver;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Day2Solver extends Solver<Integer> {
    public Day2Solver(String filename) {
        super(filename);
    }

    @Override
    public Integer solvePartOne() {
        Map<String, Integer> config = Map.of("red", 12, "green", 13, "blue", 14);
        return puzzle.stream().map(Game::from).filter(game -> game.isValid(config)).mapToInt(Game::gameId).sum();
    }

    @Override
    public Integer solvePartTwo() {
        return puzzle.stream().map(Game::from).mapToInt(Game::getPower).sum();
    }

    private record Game(int gameId, Map<String, Integer> colorMap) {
        static Game from(String line) {
            Pattern gamePattern = Pattern.compile("^Game\\s(\\d+):(.+)$");
            Pattern cubeStatePattern = Pattern.compile("^(\\d+)\\s(\\w+)$");

            int gameId = 0;
            String subsets = "";

            Matcher gameMatcher = gamePattern.matcher(line);
            if (gameMatcher.find()) {
                gameId = Integer.parseInt(gameMatcher.group(1));
                subsets = gameMatcher.group(2);
            }

            Map<String, Integer> colorCounts = new HashMap<>();
            String[] sets = subsets.split(";");
            for (String set : sets) {
                String[] states = set.split(",");
                for (String state : states) {
                    Matcher stateMatcher = cubeStatePattern.matcher(state.trim());
                    if (stateMatcher.find()) {
                        int count = Integer.parseInt(stateMatcher.group(1));
                        String color = stateMatcher.group(2);
                        colorCounts.compute(color, (col, oldCount) -> oldCount == null ? count : Math.max(count, oldCount));
                    }
                }
            }
            return new Game(gameId, colorCounts);
        }

        boolean isValid(Map<String, Integer> config) {
            return colorMap.entrySet().stream()
                    .allMatch(entry -> config.getOrDefault(entry.getKey(), 0) >= entry.getValue());
        }

        int getPower() {
            int redCount = colorMap.getOrDefault("red", 1);
            int greenCount = colorMap.getOrDefault("green", 1);
            int blueCount = colorMap.getOrDefault("blue", 1);
            return redCount * greenCount * blueCount;
        }
    }
}