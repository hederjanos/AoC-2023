package day._2;

import util.common.Solver;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Day2Solver extends Solver<Integer> {
    private final List<Game> games;

    public Day2Solver(String filename) {
        super(filename);
        games = puzzle.stream().map(Game::from).toList();
    }

    @Override
    public Integer solvePartOne() {
        Map<String, Integer> config = Map.of("red", 12, "green", 13, "blue", 14);
        return games.stream().filter(game -> game.isValid(config)).mapToInt(Game::gameId).sum();
    }

    @Override
    public Integer solvePartTwo() {
        return games.stream().mapToInt(Game::getPower).sum();
    }

    private static final class Game {
        private static final Pattern GAME_PATTERN = Pattern.compile("^Game\\s+(\\d+):");
        private static final Pattern CUBE_PATTERN = Pattern.compile("(\\d+)\\s+(\\w+)");

        private final int gameId;
        private final Map<String, Integer> colorMap;

        Game(int gameId, Map<String, Integer> colorMap) {
            this.gameId = gameId;
            this.colorMap = Map.copyOf(colorMap);
        }

        static Game from(String line) {
            int gameId = 0;
            Map<String, Integer> colorCounts = new HashMap<>();

            Matcher gameMatcher = GAME_PATTERN.matcher(line);
            if (gameMatcher.find()) {
                gameId = Integer.parseInt(gameMatcher.group(1));
            }

            Matcher cubeMatcher = CUBE_PATTERN.matcher(line);
            while (cubeMatcher.find()) {
                int count = Integer.parseInt(cubeMatcher.group(1));
                String color = cubeMatcher.group(2);

                colorCounts.merge(color, count, Math::max);
            }

            return new Game(gameId, colorCounts);
        }

        static Game fromWitStreamAPI(String line) {
            int gameId = Pattern.compile("^Game\\s+(\\d+):")
                    .matcher(line)
                    .results()
                    .map(match -> Integer.parseInt(match.group(1)))
                    .findFirst()
                    .orElse(0);

            Map<String, Integer> colorCounts = Pattern.compile("(\\d+)\\s+(\\w+)")
                    .matcher(line)
                    .results()
                    .collect(Collectors.toMap(
                            match -> match.group(2),
                            match -> Integer.parseInt(match.group(1)),
                            Math::max
                    ));

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

        int gameId() {
            return gameId;
        }
    }
}