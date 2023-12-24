package day._10;

import util.common.Solver;
import util.coordinate.Coordinate;

import java.util.*;

public class Day10Solver extends Solver<Integer> {
    private static final char V = '|';
    private static final char H = '-';
    private static final char L = 'L';
    private static final char J = 'J';
    private static final char $ = '7';
    private static final char F = 'F';
    private static final char G = '.';
    private static final char S = 'S';

    private final int width;
    private final int height;

    private Coordinate start;
    private final Map<Coordinate, Set<Coordinate>> pipes = new HashMap<>();
    private final Set<Coordinate> junks = new HashSet<>();

    public Day10Solver(String fileName) {
        super(fileName);
        width = puzzle.get(0).length();
        height = puzzle.size();
        parsePipeMap();
        initStart();
    }

    private void parsePipeMap() {
        for (int i = 0; i < puzzle.size(); i++) {
            for (int j = 0; j < puzzle.get(i).length(); j++) {
                loadPipe(j, i);
            }
        }
    }

    private void loadPipe(int j, int i) {
        Coordinate coordinate = new Coordinate(j, i);

        boolean isValidFrom1 = false;
        boolean isValidFrom2 = false;

        switch (puzzle.get(i).charAt(j)) {
            case V:
                if (isCoordinateInBounds(j, i - 1)) {
                    char charAt = puzzle.get(i - 1).charAt(j);
                    isValidFrom1 = charAt == V || charAt == $ || charAt == F || charAt == S;
                }

                if (isCoordinateInBounds(j, i + 1)) {
                    char charAt = puzzle.get(i + 1).charAt(j);
                    isValidFrom2 = charAt == V || charAt == L || charAt == J || charAt == S;
                }

                if (isValidFrom1 && isValidFrom2) {
                    pipes.putIfAbsent(coordinate, Set.of(new Coordinate(j, i - 1), new Coordinate(j, i + 1)));
                } else {
                    junks.add(coordinate);
                }
                break;
            case H:
                if (isCoordinateInBounds(j - 1, i)) {
                    char charAt = puzzle.get(i).charAt(j - 1);
                    isValidFrom1 = charAt == H || charAt == L || charAt == F || charAt == S;
                }

                if (isCoordinateInBounds(j + 1, i)) {
                    char charAt = puzzle.get(i).charAt(j + 1);
                    isValidFrom2 = charAt == H || charAt == J || charAt == $ || charAt == S;
                }

                if (isValidFrom1 && isValidFrom2) {
                    pipes.putIfAbsent(coordinate, Set.of(new Coordinate(j - 1, i), new Coordinate(j + 1, i)));
                } else {
                    junks.add(coordinate);
                }
                break;
            case L:
                if (isCoordinateInBounds(j, i - 1)) {
                    char charAt = puzzle.get(i - 1).charAt(j);
                    isValidFrom1 = charAt == V || charAt == $ || charAt == F || charAt == S;
                }

                if (isCoordinateInBounds(j + 1, i)) {
                    char charAt = puzzle.get(i).charAt(j + 1);
                    isValidFrom2 = charAt == H || charAt == J || charAt == $ || charAt == S;
                }

                if (isValidFrom1 && isValidFrom2) {
                    pipes.putIfAbsent(coordinate, Set.of(new Coordinate(j, i - 1), new Coordinate(j + 1, i)));
                } else {
                    junks.add(coordinate);
                }
                break;
            case J:
                if (isCoordinateInBounds(j, i - 1)) {
                    char charAt = puzzle.get(i - 1).charAt(j);
                    isValidFrom1 = charAt == V || charAt == $ || charAt == F || charAt == S;
                }

                if (isCoordinateInBounds(j - 1, i)) {
                    char charAt = puzzle.get(i).charAt(j - 1);
                    isValidFrom2 = charAt == H || charAt == L || charAt == F || charAt == S;
                }

                if (isValidFrom1 && isValidFrom2) {
                    pipes.putIfAbsent(coordinate, Set.of(new Coordinate(j, i - 1), new Coordinate(j - 1, i)));
                } else {
                    junks.add(coordinate);
                }
                break;
            case $:
                if (isCoordinateInBounds(j, i + 1)) {
                    char charAt = puzzle.get(i + 1).charAt(j);
                    isValidFrom1 = charAt == V || charAt == L || charAt == J || charAt == S;
                }

                if (isCoordinateInBounds(j - 1, i)) {
                    char charAt = puzzle.get(i).charAt(j - 1);
                    isValidFrom2 = charAt == H || charAt == L || charAt == F || charAt == S;
                }

                if (isValidFrom1 && isValidFrom2) {
                    pipes.putIfAbsent(coordinate, Set.of(new Coordinate(j, i + 1), new Coordinate(j - 1, i)));
                } else {
                    junks.add(coordinate);
                }
                break;
            case F:
                if (isCoordinateInBounds(j, i + 1)) {
                    char charAt = puzzle.get(i + 1).charAt(j);
                    isValidFrom1 = charAt == V || charAt == L || charAt == J || charAt == S;
                }

                if (isCoordinateInBounds(j + 1, i)) {
                    char charAt = puzzle.get(i).charAt(j + 1);
                    isValidFrom2 = charAt == H || charAt == J || charAt == $ || charAt == S;
                }

                if (isValidFrom1 && isValidFrom2) {
                    pipes.putIfAbsent(coordinate, Set.of(new Coordinate(j, i + 1), new Coordinate(j + 1, i)));
                } else {
                    junks.add(coordinate);
                }
                break;
            case S:
                start = coordinate;
            case G:
                break;
            default:
                throw new IllegalStateException();
        }
    }

    //TODO determine exact type of start
    private void initStart() {
        Set<Coordinate> neighbours = new HashSet<>();
        for (Coordinate neighbour : start.getOrthogonalAdjacentCoordinates()) {
            Set<Coordinate> coordinates = pipes.get(neighbour);
            if (coordinates != null) {
                for (Coordinate coordinate : pipes.get(neighbour)) {
                    if (coordinate.equals(start)) {
                        neighbours.add(neighbour.copy());
                    }
                }
            }
        }
        pipes.putIfAbsent(start, Collections.unmodifiableSet(neighbours));
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
            for (Coordinate neighbour : pipes.get(currentCoordinate)) {
                if (isCoordinateInBounds(neighbour.getX(), neighbour.getY())) {
                    if (!visitedCoords.contains(neighbour)) {
                        paths.offer(new Path(neighbour, currentPath.steps + 1));
                        visitedCoords.add(neighbour);
                    }
                }
            }
        }
        return maxSteps;
    }

    private boolean isCoordinateInBounds(int x, int y) {
        return x < width && x >= 0 && y < height && y >= 0;
    }

    @Override
    public Integer solvePartTwo() {
        Set<Coordinate> insidePoints = new HashSet<>();
        for (int i = 0; i < puzzle.size(); i++) {
            boolean isInside = false;
            char prev = ' ';
            for (int j = 0; j < puzzle.get(i).length(); j++) {
                Coordinate coordinate = new Coordinate(j, i);
                char curr = puzzle.get(i).charAt(j);
                if (pipes.containsKey(coordinate)) {
                    if (prev == ' ') {
                        prev = curr;
                    }
                    if (curr == V || (prev == F && curr == J) || (prev == L && curr == $)) {
                        isInside = !isInside;
                        prev = ' ';
                    } else if (prev == L && curr == J || (prev == F && curr == $)) {
                        prev = ' ';
                    }
                } else {
                    if (isInside) {
                        insidePoints.add(coordinate);
                        prev = ' ';
                    }
                }
            }
        }
        return insidePoints.size();
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
