package day._13;

import util.common.Solver;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Day13Solver extends Solver<Integer> {
    public Day13Solver(String fileName) {
        super(fileName);
        puzzle.add("");
    }

    @Override
    public Integer solvePartOne() {
        return solve(false);
    }

    private int solve(boolean repair) {
        int sum = 0;
        int lineCounter = 0;
        while (lineCounter < puzzle.size()) {
            List<String> pattern = new ArrayList<>();
            while (!puzzle.get(lineCounter).isEmpty()) {
                pattern.add(puzzle.get(lineCounter));
                lineCounter++;
            }
            int[][] locationsInRows = new int[pattern.size()][pattern.get(0).length()];
            int[][] locationInCols = new int[pattern.get(0).length()][pattern.size()];
            for (int i = 0; i < pattern.size(); i++) {
                String row = pattern.get(i);
                for (int j = 0; j < row.length(); j++) {
                    if (row.charAt(j) == '#') {
                        locationsInRows[i][j] = 1;
                        locationInCols[j][i] = 1;
                    }
                }
            }
            int reflection = (!repair) ? findReflection(locationsInRows, -1) : repairAndFindReflection(locationsInRows);
            if (reflection != -1) {
                sum += (reflection + 1) * 100;
            } else {
                reflection = (!repair) ? findReflection(locationInCols, -1) : repairAndFindReflection(locationInCols);
                if (reflection != -1) {
                    sum += reflection + 1;
                }
            }
            lineCounter++;
        }
        return sum;
    }

    private int findReflection(int[][] locations, int orig) {
        int reflection = -1;
        for (int j = 0; j < locations.length - 1; j++) {
            if (Arrays.equals(locations[j], locations[j + 1])) {
                int min = Math.min(j + 1, locations.length - j - 1);
                int[][] partOne;
                int[][] partTwo;
                if (min == j + 1) {
                    partOne = copy(locations, 0, j + 1);
                    partTwo = copy(locations, j + 1, j + 1 + min);
                } else {
                    partOne = copy(locations, j + 1, locations.length);
                    partTwo = copy(locations, j + 1 - min, j + 1);
                }
                if (j != orig && areMirrors(partOne, partTwo)) {
                    reflection = j;
                    break;
                }
            }
        }
        return reflection;
    }

    private int[][] copy(int[][] array, int from, int to) {
        int[][] newArray = new int[to - from][array[0].length];
        for (int i = 0; i < to - from; i++) {
            newArray[i] = Arrays.copyOfRange(array[from + i], 0, array[i].length);
        }
        return newArray;
    }

    private boolean areMirrors(int[][] array, int[][] other) {
        for (int i = 0; i < array.length; i++) {
            if (!Arrays.equals(array[i], other[array.length - i - 1])) {
                return false;
            }
        }
        return true;
    }

    @Override
    public Integer solvePartTwo() {
        return solve(true);
    }

    private int repairAndFindReflection(int[][] origLocations) {
        int reflection = -1;
        int origReflection = findReflection(origLocations, -1);
        for (int i = 0; i < origLocations.length - 1; i++) {
            List<int[][]> repairedLocations = createRepairedLocations(origLocations, origLocations[i], i);
            if (!repairedLocations.isEmpty()) {
                for (int[][] locations : repairedLocations) {
                    reflection = findReflection(locations, origReflection);
                    if (reflection != -1) {
                        break;
                    }
                }
            }
            if (reflection != -1) {
                break;
            }
        }
        return reflection;
    }

    private List<int[][]> createRepairedLocations(int[][] locations, int[] array, int k) {
        List<int[][]> repairedLocations = new ArrayList<>();
        for (int i = 0; i < locations.length; i++) {
            if (k != i) {
                int[] similarityData = examineSimilarity(array, locations[i]);
                if (similarityData != null) {
                    int[][] newLocations = copy(locations, 0, locations.length);
                    newLocations[i][similarityData[0] != -1 ? similarityData[0] : similarityData[1]] = similarityData[0] != -1 ? 0 : 1;
                    repairedLocations.add(newLocations);
                }
            }
        }
        return repairedLocations;
    }

    private int[] examineSimilarity(int[] array, int[] other) {
        int numberOfDiffs = 0;
        int[] diffData = null;
        for (int i = 0; i < array.length; i++) {
            if (array[i] != other[i]) {
                if (numberOfDiffs != 0) {
                    return null;
                }
                numberOfDiffs++;
                diffData = new int[]{-1, -1};
                diffData[array[i] == 0 ? 0 : 1] = i;
            }
        }
        return diffData;
    }
}
