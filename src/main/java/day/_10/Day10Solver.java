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

    private final Set<Pipe> pipes;
    private final Pipe startPipe;

    public Day10Solver(String fileName) {
        super(fileName);
        width = puzzle.get(0).length();
        height = puzzle.size();
        startPipe = findStartPipe();
        if (startPipe != null) {
            pipes = findLoop(startPipe);
        } else {
            throw new IllegalStateException();
        }
    }

    private Pipe findStartPipe() {
        Pipe startPipe = null;
        for (int i = 0; i < puzzle.size(); i++) {
            for (int j = 0; j < puzzle.get(i).length(); j++) {
                char ch = puzzle.get(i).charAt(j);
                Coordinate coordinate = new Coordinate(j, i);
                if (ch == S) {
                    startPipe = initStartPipe(coordinate);
                    break;
                }
            }
            if (startPipe != null) {
                break;
            }
        }
        return startPipe;
    }

    private Pipe initStartPipe(Coordinate start) {
        List<Pipe> pipes = List.of(new Pipe(start, V), new Pipe(start, H), new Pipe(start, L), new Pipe(start, J), new Pipe(start, $), new Pipe(start, F));
        for (Pipe pipe : pipes) {
            if (isValidPipe(pipe, getPossibleConnectorLocationsFor(pipe))) {
                return pipe;
            }
        }
        return null;
    }

    private Set<Pipe> findLoop(Pipe pipe) {
        Set<Pipe> visitedPipes = new HashSet<>();
        Deque<Pipe> paths = new ArrayDeque<>();
        paths.offer(pipe);
        visitedPipes.add(pipe);
        while (!paths.isEmpty()) {
            Pipe polled = paths.poll();
            for (Coordinate coordinate : getPossibleConnectorLocationsFor(polled)) {
                Pipe newPipe = new Pipe(coordinate, puzzle.get(coordinate.getY()).charAt(coordinate.getX()));
                Set<Coordinate> possibleConnectorLocationsFor = getPossibleConnectorLocationsFor(newPipe);
                if (!visitedPipes.contains(newPipe) && isValidPipe(newPipe, possibleConnectorLocationsFor)) {
                    visitedPipes.add(newPipe);
                    paths.offer(newPipe);
                }
            }
        }
        return visitedPipes;
    }

    private Set<Coordinate> getPossibleConnectorLocationsFor(Pipe pipe) {
        Set<Coordinate> coordinates;
        switch (pipe.ch) {
            case V:
                coordinates = Set.of(new Coordinate(pipe.coordinate.getX(), pipe.coordinate.getY() - 1), new Coordinate(pipe.coordinate.getX(), pipe.coordinate.getY() + 1));
                break;
            case H:
                coordinates = Set.of(new Coordinate(pipe.coordinate.getX() - 1, pipe.coordinate.getY()), new Coordinate(pipe.coordinate.getX() + 1, pipe.coordinate.getY()));
                break;
            case L:
                coordinates = Set.of(new Coordinate(pipe.coordinate.getX(), pipe.coordinate.getY() - 1), new Coordinate(pipe.coordinate.getX() + 1, pipe.coordinate.getY()));
                break;
            case J:
                coordinates = Set.of(new Coordinate(pipe.coordinate.getX(), pipe.coordinate.getY() - 1), new Coordinate(pipe.coordinate.getX() - 1, pipe.coordinate.getY()));
                break;
            case $:
                coordinates = Set.of(new Coordinate(pipe.coordinate.getX(), pipe.coordinate.getY() + 1), new Coordinate(pipe.coordinate.getX() - 1, pipe.coordinate.getY()));
                break;
            case F:
                coordinates = Set.of(new Coordinate(pipe.coordinate.getX(), pipe.coordinate.getY() + 1), new Coordinate(pipe.coordinate.getX() + 1, pipe.coordinate.getY()));
                break;
            default:
                coordinates = new HashSet<>();
        }
        return coordinates;
    }

    private boolean isValidPipe(Pipe pipe, Set<Coordinate> connectors) {
        boolean isValid = false;
        if (!connectors.isEmpty() && connectors.stream().allMatch(this::isCoordinateInBounds)) {
            switch (pipe.ch) {
                case V:
                    isValid = connectors.stream().allMatch(coordinate -> {
                        char ch = puzzle.get(coordinate.getY()).charAt(coordinate.getX());
                        return (ch == V) || (coordinate.getY() < pipe.coordinate.getY() && (ch == $ || ch == F)) || (coordinate.getY() > pipe.coordinate.getY() && (ch == L || ch == J)) || ch == S;
                    });
                    break;
                case H:
                    isValid = connectors.stream().allMatch(coordinate -> {
                        char ch = puzzle.get(coordinate.getY()).charAt(coordinate.getX());
                        return (ch == H) || (coordinate.getX() < pipe.coordinate.getX() && (ch == L || ch == F)) || (coordinate.getX() > pipe.coordinate.getX() && (ch == J || ch == $)) || ch == S;
                    });
                    break;
                case L:
                    isValid = connectors.stream().allMatch(coordinate -> {
                        char ch = puzzle.get(coordinate.getY()).charAt(coordinate.getX());
                        return (coordinate.getY() < pipe.coordinate.getY() && (ch == V || ch == F || ch == $)) || (coordinate.getX() > pipe.coordinate.getX() && (ch == H || ch == J || ch == $)) || ch == S;
                    });
                    break;
                case J:
                    isValid = connectors.stream().allMatch(coordinate -> {
                        char ch = puzzle.get(coordinate.getY()).charAt(coordinate.getX());
                        return (coordinate.getX() < pipe.coordinate.getX() && (ch == H || ch == L || ch == F)) || (coordinate.getY() < pipe.coordinate.getY() && (ch == V || ch == F || ch == $)) || ch == S;
                    });
                    break;
                case F:
                    isValid = connectors.stream().allMatch(coordinate -> {
                        char ch = puzzle.get(coordinate.getY()).charAt(coordinate.getX());
                        return (coordinate.getY() > pipe.coordinate.getY() && (ch == V || ch == L || ch == J)) || (coordinate.getX() > pipe.coordinate.getX() && (ch == H || ch == J || ch == $)) || ch == S;
                    });
                    break;
                case $:
                    isValid = connectors.stream().allMatch(coordinate -> {
                        char ch = puzzle.get(coordinate.getY()).charAt(coordinate.getX());
                        return (coordinate.getX() < pipe.coordinate.getX() && (ch == H || ch == F || ch == L)) || (coordinate.getY() > pipe.coordinate.getY() && (ch == V || ch == L || ch == J)) || ch == S;
                    });
                    break;
                case G:
                    break;
                default:
                    throw new IllegalStateException();
            }
        }
        return isValid;
    }

    @Override
    public Integer solvePartOne() {
        return pipes.size() / 2;
    }

    private boolean isCoordinateInBounds(Coordinate coordinate) {
        return coordinate.getX() < width && coordinate.getX() >= 0 && coordinate.getY() < height && coordinate.getY() >= 0;
    }

    @Override
    public Integer solvePartTwo() {
        Set<Coordinate> insidePoints = new HashSet<>();
        for (int i = 0; i < puzzle.size(); i++) {
            boolean isInside = false;
            char prev = G;
            for (int j = 0; j < puzzle.get(i).length(); j++) {
                Coordinate coordinate = new Coordinate(j, i);
                char curr = puzzle.get(i).charAt(j);
                if (pipes.contains(new Pipe(coordinate, curr))) {
                    if (curr == S) {
                        curr = startPipe.ch;
                    }
                    if (curr == V || (prev == F && curr == J) || (prev == L && curr == $)) {
                        isInside = !isInside;
                    }
                    if (curr == L || curr == F) {
                        prev = curr;
                    }
                } else {
                    if (isInside) {
                        insidePoints.add(coordinate);
                    }
                }
            }
        }
        return insidePoints.size();
    }

    public static class Pipe {
        Coordinate coordinate;
        char ch;

        public Pipe(Coordinate coordinate, char ch) {
            this.coordinate = coordinate;
            this.ch = ch;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Pipe pipe)) return false;
            return Objects.equals(coordinate, pipe.coordinate);
        }

        @Override
        public int hashCode() {
            return Objects.hash(coordinate);
        }
    }

}
