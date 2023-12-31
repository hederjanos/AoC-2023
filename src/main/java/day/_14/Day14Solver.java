package day._14;

import util.common.Solver;

import java.util.*;

public class Day14Solver extends Solver<Integer> {
    private static final char STAT_ROCK = '#';
    private static final char DYN_ROCK = 'O';
    private static final char GROUND = '.';
    private final Platform platform;

    public Day14Solver(String fileName) {
        super(fileName);
        platform = parsePlatform();
    }

    private Platform parsePlatform() {
        Set<Coordinate> dynamicRocks = new HashSet<>();
        Set<Coordinate> staticRocks = new HashSet<>();
        for (short i = 0; i < puzzle.size(); i++) {
            String row = puzzle.get(i);
            for (short j = 0; j < row.length(); j++) {
                char charAt = row.charAt(j);
                switch (charAt) {
                    case STAT_ROCK:
                        staticRocks.add(new Coordinate(j, i));
                        break;
                    case DYN_ROCK:
                        dynamicRocks.add(new Coordinate(j, i));
                        break;
                    case GROUND:
                        break;
                    default:
                        throw new IllegalStateException();
                }
            }
        }
        return new Platform((short) puzzle.get(0).length(), (short) puzzle.size(), staticRocks, dynamicRocks);
    }

    @Override
    public Integer solvePartOne() {
        Platform tilted = platform.tiltNorth();
        return tilted.calculateLoad();
    }

    @Override
    public Integer solvePartTwo() {
        Map<Integer, Platform> steps = new HashMap<>();
        Map<Platform, Integer> cache = new HashMap<>();
        Platform current = platform;
        int end = 0;
        while (!cache.containsKey(current)) {
            steps.put(end, current);
            cache.put(current, end);
            current = doOneCycle(current);
            end++;
        }
        int start = cache.get(current);
        return steps.get(start + (1_000_000_000 - start) % (end - start)).calculateLoad();
    }

    private Platform doOneCycle(Platform platform) {
        Platform tilted = platform.tiltNorth();
        tilted = tilted.tiltWest();
        tilted = tilted.tiltSouth();
        tilted = tilted.tiltEast();
        return tilted;
    }

    private class Platform {
        short width;
        short height;
        Set<Coordinate> staticRocks;
        Set<Coordinate> dynamicRocks;

        Platform(short width, short height, Set<Coordinate> staticRocks, Set<Coordinate> dynamicRocks) {
            this.width = width;
            this.height = height;
            this.staticRocks = staticRocks;
            this.dynamicRocks = dynamicRocks;
        }

        Platform tiltNorth() {
            return tilt(true, true);
        }

        Platform tiltSouth() {
            return tilt(true, false);
        }

        Platform tiltWest() {
            return tilt(false, true);
        }

        Platform tiltEast() {
            return tilt(false, false);
        }

        private Platform tilt(boolean isHorizontal, boolean isPositive) {
            Set<Coordinate> newDynamicRocks = new HashSet<>();
            short rows = platform.height;
            short cols = platform.width;
            short[] tops = new short[isHorizontal ? cols : rows];
            Arrays.fill(tops, isPositive ? -1 : (isHorizontal ? cols : rows));
            short directionMultiplier = (short) (isPositive ? 1 : -1);
            short outerCounter = getOuterCounter(isHorizontal, isPositive, rows, cols);
            for (short i = outerCounter; outerCondition(isHorizontal, isPositive, i, rows, cols); i += directionMultiplier) {
                for (short j = 0; innerCondition(isHorizontal, j, rows, cols); j++) {
                    Coordinate coordinate = isHorizontal ? new Coordinate(j, i) : new Coordinate(i, j);
                    if (staticRocks.contains(coordinate)) {
                        tops[j] = i;
                    } else if (dynamicRocks.contains(coordinate)) {
                        short top = tops[j];
                        newDynamicRocks.add(new Coordinate(isHorizontal ? j : (short) (top + directionMultiplier), isHorizontal ? (short) (top + directionMultiplier) : j));
                        tops[j] = (short) (top + directionMultiplier);
                    }
                }
            }
            return new Platform(width, height, staticRocks, newDynamicRocks);
        }

        private short getOuterCounter(boolean isHorizontal, boolean isPositive, short rows, short cols) {
            return (short) (isPositive ? 0 : isHorizontal ? rows - 1 : cols - 1);
        }

        private boolean outerCondition(boolean isHorizontal, boolean isPositive, short i, short rows, short cols) {
            return isPositive ? isHorizontal ? i < rows : i < cols : i >= 0;
        }

        private boolean innerCondition(boolean isHorizontal, short j, short rows, short cols) {
            return isHorizontal ? j < cols : j < rows;
        }

        int calculateLoad() {
            return dynamicRocks.stream().mapToInt(c -> platform.height - c.y).sum();
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Platform)) return false;
            Platform platform = (Platform) o;
            return Objects.equals(dynamicRocks, platform.dynamicRocks);
        }

        @Override
        public int hashCode() {
            return Objects.hash(dynamicRocks);
        }

        String display() {
            StringBuilder rowSb = new StringBuilder();
            for (short i = 0; i < height; i++) {
                StringBuilder colSb = new StringBuilder();
                for (short j = 0; j < width; j++) {
                    Coordinate coordinate = new Coordinate(j, i);
                    if (staticRocks.contains(coordinate)) {
                        colSb.append(STAT_ROCK);
                    } else if (dynamicRocks.contains(coordinate)) {
                        colSb.append(DYN_ROCK);
                    } else {
                        colSb.append(GROUND);
                    }
                }
                rowSb.append(colSb.append("\n"));
            }
            return rowSb.toString();
        }
    }

    private static class Coordinate {
        short x;
        short y;

        Coordinate(short x, short y) {
            this.x = x;
            this.y = y;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Coordinate)) return false;
            Coordinate that = (Coordinate) o;
            return x == that.x && y == that.y;
        }

        @Override
        public int hashCode() {
            return Objects.hash(x, y);
        }
    }
}
