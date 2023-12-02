package day._2;

import util.common.Solver;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CubeConundrumSolver extends Solver<Integer> {
    private final Pattern gamePattern = Pattern.compile("^Game\\s(\\d+):(.+)$");
    private final Pattern cubeStatePattern = Pattern.compile("^(\\d+)\\s(\\w+)+$");
    private final String SUBSET_SEPARATOR = ";";
    private final String CUBE_STATE_SEPARATOR = ",";
    private final Set<CubeState> config = Set.of(new CubeState(Color.red, 12), new CubeState(Color.green, 13), new CubeState(Color.blue, 14));

    public CubeConundrumSolver(String filename) {
        super(filename);
    }

    @Override
    public Integer solvePartOne() {
        return puzzle.stream()
                .map(this::parseAGame)
                .filter(gameDetails -> gameDetails.isValid(config))
                .mapToInt(g -> g.gameId)
                .sum();
    }

    private GameDetails parseAGame(String line) {
        int gameId = 0;
        String subsets = "";

        Matcher gameMatcher = gamePattern.matcher(line);
        while (gameMatcher.find()) {
            gameId = Integer.parseInt(gameMatcher.group(1));
            subsets = gameMatcher.group(2);
        }

        String[] sets = subsets.split(SUBSET_SEPARATOR);
        Set<CubeSubSet> cubeSubSets = new HashSet<>();
        for (String set : sets) {
            String[] states = set.split(CUBE_STATE_SEPARATOR);
            Set<CubeState> cubeStates = new HashSet<>();
            for (String state : states) {
                String strip = state.strip();
                Matcher stateMatcher = cubeStatePattern.matcher(strip);
                while (stateMatcher.find()) {
                    cubeStates.add(new CubeState(Color.valueOf(stateMatcher.group(2)), Integer.parseInt(stateMatcher.group(1))));
                }
            }
            cubeSubSets.add(new CubeSubSet(cubeStates));
        }
        return new GameDetails(gameId, cubeSubSets);
    }

    @Override
    protected Integer solvePartTwo() {
        return null;
    }

    private static class GameDetails {
        int gameId;
        Set<CubeSubSet> subsets;

        GameDetails(int gameId, Set<CubeSubSet> subsets) {
            this.gameId = gameId;
            this.subsets = subsets;
        }

        boolean isValid(Set<CubeState> config) {
            for (CubeSubSet subset : subsets) {
                for (CubeState state : subset.states) {
                    for (CubeState configState : config) {
                        if (configState.color.equals(state.color) && configState.count < state.count) {
                            return false;
                        }
                    }
                }
            }
            return true;
        }
    }

    private static class CubeSubSet {
        Set<CubeState> states;

        CubeSubSet(Set<CubeState> states) {
            this.states = states;
        }
    }

    private static class CubeState {
        Color color;
        int count;

        CubeState(Color color, int count) {
            this.color = color;
            this.count = count;
        }
    }

    private enum Color {
        red, green, blue;
    }

}
