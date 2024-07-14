package day._17;

import util.common.Solver;
import util.coordinate.Coordinate;
import util.coordinate.Direction;

import java.util.*;

public class Day17Solver extends Solver<Integer> {
    private final int[][] heatLossMap;

    public Day17Solver(String fileName) {
        super(fileName);
        heatLossMap = parseHeatLossMap();
    }

    private int[][] parseHeatLossMap() {
        int[][] heatLossMap = new int[puzzle.size()][puzzle.get(0).length()];
        for (int i = 0; i < puzzle.size(); i++) {
            for (int j = 0; j < puzzle.get(0).length(); j++) {
                heatLossMap[i][j] = Character.getNumericValue(puzzle.get(i).charAt(j));
            }
        }
        return heatLossMap;
    }

    @Override
    public Integer solvePartOne() {
        return calculateMinimumHeatLoss(3, 1);
    }

    public Integer calculateMinimumHeatLoss(int maxBlocks, int minBlocks) {
        Map<Path, Integer> pathHistory = new HashMap<>();
        Path path = new Path(new Coordinate(0, 0), Direction.RIGHT, 0, 0);
        pathHistory.put(path, 0);
        Queue<Path> pathQueue = new PriorityQueue<>();
        pathQueue.add(path);
        Coordinate target = new Coordinate(puzzle.get(0).length() - 1, puzzle.size() - 1);
        int minHeatLoss = Integer.MAX_VALUE;
        while (!pathQueue.isEmpty()) {
            Path polled = pathQueue.poll();
            if (polled.coordinate.equals(target)) {
                minHeatLoss = polled.heatLoss;
                break;
            }
            Direction[] values = Direction.values();
            for (int j = 0; j < values.length; j++) {
                Direction direction = values[j];
                if (j % 2 == 0 && !direction.equals(polled.direction.getOppositeDirection())) {
                    Coordinate current = polled.coordinate;
                    int counter = polled.counter;
                    int heatLoss = polled.heatLoss;
                    for (int i = 0; i < maxBlocks; i++) {
                        Coordinate next = current.moveByDirection(direction);
                        if (isCoordinateInBounds(next)) {
                            current = next;
                            heatLoss += heatLossMap[next.getX()][next.getY()];
                            if (direction.equals(polled.direction)) {
                                if (counter >= maxBlocks) {
                                    break;
                                } else {
                                    counter++;
                                }
                            } else {
                                counter = (i == 0) ? 1 : counter + 1;
                            }
                            if (counter >= minBlocks) {
                                Path newPath = new Path(next, direction, counter, heatLoss);
                                if (!pathHistory.containsKey(newPath) || pathHistory.get(newPath) > newPath.heatLoss) {
                                    pathQueue.add(newPath);
                                    pathHistory.put(newPath, newPath.heatLoss);
                                }
                            }
                        } else {
                            break;
                        }
                    }
                }
            }
        }
        return minHeatLoss;
    }

    private boolean isCoordinateInBounds(Coordinate coordinate) {
        return coordinate.getX() < heatLossMap[0].length && coordinate.getX() >= 0 && coordinate.getY() < heatLossMap.length && coordinate.getY() >= 0;
    }

    @Override
    public Integer solvePartTwo() {
        return calculateMinimumHeatLoss(10, 4);
    }

    private static class Path implements Comparable<Path> {
        Coordinate coordinate;
        Direction direction;
        int counter;
        int heatLoss;

        Path(Coordinate coordinate, Direction direction, int counter, int heatLoss) {
            this.coordinate = coordinate;
            this.direction = direction;
            this.counter = counter;
            this.heatLoss = heatLoss;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Path)) return false;
            Path path = (Path) o;
            return Objects.equals(coordinate, path.coordinate) && direction == path.direction && counter == path.counter;
        }

        @Override
        public int hashCode() {
            return Objects.hash(coordinate, direction, counter);
        }

        @Override
        public int compareTo(Path o) {
            return Integer.compare(heatLoss, o.heatLoss);
        }
    }
}
