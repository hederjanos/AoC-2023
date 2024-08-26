package day._10;

import util.common.Solver;
import util.coordinate.Coordinate;

import java.util.*;
import java.util.stream.Stream;

public class Day10Solver extends Solver<Integer> {
    private final Maze maze = Maze.from(puzzle);

    public Day10Solver(String fileName) {
        super(fileName);
    }

    @Override
    public Integer solvePartOne() {
        return maze.lengthOfLoop() / 2;
    }

    @Override
    public Integer solvePartTwo() {
        return maze.areaOfLoop();
    }

    private static final class Maze {
        static final char V = '|';
        static final char H = '-';
        static final char L = 'L';
        static final char J = 'J';
        static final char $ = '7';
        static final char F = 'F';
        static final char G = '.';
        static final char S = 'S';
        private final int width;
        private final int height;
        private final Pipe startPipe;
        private final Map<Coordinate, Character> pipeMap;
        private final Set<Coordinate> loop;

        Maze(int width, int height, Coordinate start, Map<Coordinate, Character> pipeMap) {
            this.width = width;
            this.height = height;
            this.pipeMap = pipeMap;
            this.startPipe = initStartPipe(start);
            this.loop = findLoop();
        }

        static Maze from(List<String> puzzle) {
            int width = puzzle.get(0).length();
            int height = puzzle.size();
            Coordinate start = null;
            Map<Coordinate, Character> characterMap = new HashMap<>();
            for (int i = 0; i < puzzle.size(); i++) {
                for (int j = 0; j < puzzle.get(i).length(); j++) {
                    char ch = puzzle.get(i).charAt(j);
                    Coordinate coordinate = new Coordinate(j, i);
                    characterMap.put(coordinate, ch);
                    if (ch == S) {
                        start = coordinate;
                    }
                }
            }
            return new Maze(width, height, start, characterMap);
        }

        Pipe initStartPipe(Coordinate start) {
            return Stream.of(V, H, L, J, $, F, G, S)
                    .map(ch -> new Pipe(start, ch))
                    .filter(this::isValidPipe)
                    .findFirst()
                    .orElse(null);
        }

        boolean isValidPipe(Pipe pipe) {
            boolean isValid = false;
            Set<Coordinate> connectors = getNeighbourCoordinates(pipe);
            if (!connectors.isEmpty() && connectors.stream().allMatch(this::isCoordinateInBounds)) {
                switch (pipe.ch) {
                    case V -> isValid = connectors.stream().allMatch(coordinate -> {
                        char ch = pipeMap.get(coordinate);
                        return (ch == V) || (coordinate.getY() < pipe.coordinate.getY() && (ch == $ || ch == F)) || (coordinate.getY() > pipe.coordinate.getY() && (ch == L || ch == J)) || ch == S;
                    });
                    case H -> isValid = connectors.stream().allMatch(coordinate -> {
                        char ch = pipeMap.get(coordinate);
                        return (ch == H) || (coordinate.getX() < pipe.coordinate.getX() && (ch == L || ch == F)) || (coordinate.getX() > pipe.coordinate.getX() && (ch == J || ch == $)) || ch == S;
                    });
                    case L -> isValid = connectors.stream().allMatch(coordinate -> {
                        char ch = pipeMap.get(coordinate);
                        return (coordinate.getY() < pipe.coordinate.getY() && (ch == V || ch == F || ch == $)) || (coordinate.getX() > pipe.coordinate.getX() && (ch == H || ch == J || ch == $)) || ch == S;
                    });
                    case J -> isValid = connectors.stream().allMatch(coordinate -> {
                        char ch = pipeMap.get(coordinate);
                        return (coordinate.getX() < pipe.coordinate.getX() && (ch == H || ch == L || ch == F)) || (coordinate.getY() < pipe.coordinate.getY() && (ch == V || ch == F || ch == $)) || ch == S;
                    });
                    case F -> isValid = connectors.stream().allMatch(coordinate -> {
                        char ch = pipeMap.get(coordinate);
                        return (coordinate.getY() > pipe.coordinate.getY() && (ch == V || ch == L || ch == J)) || (coordinate.getX() > pipe.coordinate.getX() && (ch == H || ch == J || ch == $)) || ch == S;
                    });
                    case $ -> isValid = connectors.stream().allMatch(coordinate -> {
                        char ch = pipeMap.get(coordinate);
                        return (coordinate.getX() < pipe.coordinate.getX() && (ch == H || ch == F || ch == L)) || (coordinate.getY() > pipe.coordinate.getY() && (ch == V || ch == L || ch == J)) || ch == S;
                    });
                    case G -> {
                    }
                    default -> throw new IllegalStateException();
                }
            }
            return isValid;
        }

        Set<Coordinate> getNeighbourCoordinates(Pipe pipe) {
            return switch (pipe.ch) {
                case V ->
                        Set.of(new Coordinate(pipe.coordinate.getX(), pipe.coordinate.getY() - 1), new Coordinate(pipe.coordinate.getX(), pipe.coordinate.getY() + 1));
                case H ->
                        Set.of(new Coordinate(pipe.coordinate.getX() - 1, pipe.coordinate.getY()), new Coordinate(pipe.coordinate.getX() + 1, pipe.coordinate.getY()));
                case L ->
                        Set.of(new Coordinate(pipe.coordinate.getX(), pipe.coordinate.getY() - 1), new Coordinate(pipe.coordinate.getX() + 1, pipe.coordinate.getY()));
                case J ->
                        Set.of(new Coordinate(pipe.coordinate.getX(), pipe.coordinate.getY() - 1), new Coordinate(pipe.coordinate.getX() - 1, pipe.coordinate.getY()));
                case $ ->
                        Set.of(new Coordinate(pipe.coordinate.getX(), pipe.coordinate.getY() + 1), new Coordinate(pipe.coordinate.getX() - 1, pipe.coordinate.getY()));
                case F ->
                        Set.of(new Coordinate(pipe.coordinate.getX(), pipe.coordinate.getY() + 1), new Coordinate(pipe.coordinate.getX() + 1, pipe.coordinate.getY()));
                default -> new HashSet<>();
            };
        }

        boolean isCoordinateInBounds(Coordinate coordinate) {
            return coordinate.getX() < width && coordinate.getX() >= 0 && coordinate.getY() < height && coordinate.getY() >= 0;
        }

        Set<Coordinate> findLoop() {
            Set<Coordinate> visitedLocations = new HashSet<>();
            Deque<Pipe> paths = new ArrayDeque<>();
            paths.offer(startPipe);
            visitedLocations.add(startPipe.coordinate);
            while (!paths.isEmpty()) {
                Pipe polled = paths.poll();
                for (Coordinate coordinate : getNeighbourCoordinates(polled)) {
                    if (isCoordinateInBounds(coordinate)) {
                        Pipe newPipe = new Pipe(coordinate, pipeMap.get(coordinate));
                        if (!visitedLocations.contains(coordinate) && isValidPipe(newPipe)) {
                            visitedLocations.add(coordinate);
                            paths.offer(newPipe);
                        }
                    }
                }
            }
            return visitedLocations;
        }

        int lengthOfLoop() {
            return loop.size();
        }

        int areaOfLoop() {
            Set<Coordinate> insidePoints = new HashSet<>();
            for (int i = 0; i < height; i++) {
                boolean isInside = false;
                char prev = G;
                for (int j = 0; j < width; j++) {
                    Coordinate coordinate = new Coordinate(j, i);
                    char curr = pipeMap.get(coordinate);
                    if (loop.contains(coordinate)) {
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

        private record Pipe(Coordinate coordinate, char ch) {
        }
    }
}
