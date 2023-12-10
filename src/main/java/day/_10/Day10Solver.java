package day._10;

import util.common.Solver;
import util.coordinate.Coordinate;

import java.util.*;

public class Day10Solver extends Solver<Integer> {
    private static final char V = '|';
    private static final char H = '-';
    private static final char L = 'L';
    private static final char J = 'J';
    private static final char SEVEN = '7';
    private static final char F = 'F';
    private static final char G = '.';
    private static final char S = 'S';

    private final int width;
    private final int height;

    private Coordinate start;
    private final Map<Coordinate, Set<Coordinate>> pipeMap;

    public Day10Solver(String fileName) {
        super(fileName);
        pipeMap = parsePipeMap();
        initStart();
        width = puzzle.get(0).length();
        height = puzzle.size();
    }

    private Map<Coordinate, Set<Coordinate>> parsePipeMap() {
        Map<Coordinate, Set<Coordinate>> pipes = new HashMap<>();
        for (int i = 0; i < puzzle.size(); i++) {
            for (int j = 0; j < puzzle.get(i).length(); j++) {
                loadPipe(j, i, pipes);
            }
        }
        return pipes;
    }

    private void loadPipe(int j, int i, Map<Coordinate, Set<Coordinate>> pipes) {
        switch (puzzle.get(i).charAt(j)) {
            case V:
                pipes.putIfAbsent(new Coordinate(j, i), Set.of(new Coordinate(j, i - 1), new Coordinate(j, i + 1)));
            case H:
                pipes.putIfAbsent(new Coordinate(j, i), Set.of(new Coordinate(j - 1, i), new Coordinate(j + 1, i)));
                break;
            case L:
                pipes.putIfAbsent(new Coordinate(j, i), Set.of(new Coordinate(j, i - 1), new Coordinate(j + 1, i)));
                break;
            case J:
                pipes.putIfAbsent(new Coordinate(j, i), Set.of(new Coordinate(j, i - 1), new Coordinate(j - 1, i)));
                break;
            case SEVEN:
                pipes.putIfAbsent(new Coordinate(j, i), Set.of(new Coordinate(j, i + 1), new Coordinate(j - 1, i)));
                break;
            case F:
                pipes.putIfAbsent(new Coordinate(j, i), Set.of(new Coordinate(j, i + 1), new Coordinate(j + 1, i)));
                break;
            case S:
                start = new Coordinate(j, i);
            case G:
                break;
            default:
                throw new IllegalStateException();
        }
    }

    private void initStart() {
        Set<Coordinate> neighbours = new HashSet<>();
        for (Coordinate neighbour : start.getOrthogonalAdjacentCoordinates()) {
            Set<Coordinate> coordinates = pipeMap.get(neighbour);
            if (coordinates != null) {
                for (Coordinate coordinate : pipeMap.get(neighbour)) {
                    if (coordinate.equals(start)) {
                        neighbours.add(neighbour.copy());
                    }
                }
            }
        }
        pipeMap.putIfAbsent(start, Collections.unmodifiableSet(neighbours));
    }

    @Override
    public Integer solvePartOne() {
        return findFarthestFrom(start);
    }

    private int findFarthestFrom(Coordinate coordinate) {
        Set<Coordinate> visitedCoords = new HashSet<>();
        Deque<Path> paths = new ArrayDeque<>();
        paths.offer(new Path(coordinate, 0));
        int maxSteps = Integer.MIN_VALUE;
        while (!paths.isEmpty()) {
            Path currentPath = paths.poll();
            Coordinate currentCoordinate = currentPath.coordinate;
            maxSteps = Math.max(currentPath.steps, maxSteps);
            for (Coordinate neighbour : pipeMap.get(currentCoordinate)) {
                if (isCoordinateInBounds(neighbour)) {
                    if (!visitedCoords.contains(neighbour)) {
                        paths.offer(new Path(neighbour, currentPath.steps + 1));
                        visitedCoords.add(neighbour);
                    }
                }
            }
        }
        return maxSteps;
    }

    private boolean isCoordinateInBounds(Coordinate coordinate) {
        return coordinate.getX() < width && coordinate.getX() >= 0 && coordinate.getY() < height && coordinate.getY() >= 0;
    }

    @Override
    public Integer solvePartTwo() {
        return null;
    }

    private static class Path {
        Coordinate coordinate;
        int steps;

        public Path(Coordinate coordinate, int steps) {
            this.coordinate = coordinate;
            this.steps = steps;
        }
    }

}
