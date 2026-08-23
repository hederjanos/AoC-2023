package day._3;

import util.common.Solver;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Day3Solver extends Solver<Integer> {
    private final EngineSchematic engineSchematic;

    public Day3Solver(String fileName) {
        super(fileName);
        engineSchematic = EngineSchematic.from(puzzle);
    }

    @Override
    public Integer solvePartOne() {
        return engineSchematic.getSumOfValidPartNumbers();
    }

    @Override
    public Integer solvePartTwo() {
        return engineSchematic.getSumOfGearRatios();
    }

    private record EngineSchematic(
            int width,
            int height,
            Set<PartNumber> partNumbers,
            Map<Coordinate, PartNumber> partMap,
            Map<Coordinate, Symbol> symbolMap
    ) {
        private static final Pattern NUMBER_PATTERN = Pattern.compile("\\d+");
        private static final char PERIOD = '.';
        private static final char ASTERISK = '*';

        static EngineSchematic from(List<String> puzzle) {
            Set<PartNumber> partNumbers = new HashSet<>();
            Map<Coordinate, PartNumber> partMap = new HashMap<>();
            Map<Coordinate, Symbol> symbolMap = new HashMap<>();

            for (int i = 0; i < puzzle.size(); i++) {
                String line = puzzle.get(i);
                Matcher numMatcher = NUMBER_PATTERN.matcher(line);

                while (numMatcher.find()) {
                    int value = Integer.parseInt(numMatcher.group());
                    Set<Coordinate> coordinates = new HashSet<>();
                    for (int j = numMatcher.start(); j < numMatcher.end(); j++) {
                        coordinates.add(new Coordinate(j, i));
                    }

                    PartNumber part = new PartNumber(value, coordinates);
                    partNumbers.add(part);
                    for (Coordinate c : coordinates) {
                        partMap.put(c, part);
                    }
                }

                for (int j = 0; j < line.length(); j++) {
                    char charAt = line.charAt(j);
                    if (!Character.isDigit(charAt) && charAt != PERIOD) {
                        Symbol symbol = new Symbol(charAt, new Coordinate(j, i));
                        symbolMap.put(symbol.coordinate(), symbol);
                    }
                }
            }

            return new EngineSchematic(puzzle.getFirst().length(), puzzle.size(), partNumbers, partMap, symbolMap);
        }

        int getSumOfValidPartNumbers() {
            return partNumbers.stream()
                    .filter(this::partNumberIsAdjacentToSymbol)
                    .mapToInt(PartNumber::value)
                    .sum();
        }

        boolean partNumberIsAdjacentToSymbol(PartNumber partNumber) {
            return getNeighbours(partNumber).stream()
                    .anyMatch(symbolMap::containsKey);
        }

        Set<Coordinate> getNeighbours(PartNumber partNumber) {
            return partNumber.getNeighbours().stream()
                    .filter(this::isCoordinateInBounds)
                    .collect(Collectors.toSet());
        }

        boolean isCoordinateInBounds(Coordinate coordinate) {
            return coordinate.x() >= 0 && coordinate.x() < width &&
                    coordinate.y() >= 0 && coordinate.y() < height;
        }

        int getSumOfGearRatios() {
            return symbolMap.values().stream()
                    .filter(symbol -> symbol.symbol() == ASTERISK)
                    .map(this::getAdjacentPartNumbersToSymbol)
                    .filter(partNumbers -> partNumbers.size() == 2)
                    .mapToInt(gearParts -> {
                        Iterator<PartNumber> it = gearParts.iterator();
                        return it.next().value() * it.next().value();
                    })
                    .sum();
        }

        Set<PartNumber> getAdjacentPartNumbersToSymbol(Symbol symbol) {
            return symbol.coordinate().getAdjacentCoordinates().stream()
                    .map(partMap::get)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toSet());
        }
    }

    private record PartNumber(int value, Set<Coordinate> coordinates) {
        Set<Coordinate> getNeighbours() {
            return coordinates.stream()
                    .flatMap(coordinate -> coordinate.getAdjacentCoordinates().stream())
                    .collect(Collectors.toSet());
        }
    }

    private record Symbol(char symbol, Coordinate coordinate) {
    }

    private record Coordinate(int x, int y) {
        Set<Coordinate> getAdjacentCoordinates() {
            Set<Coordinate> adjacent = new HashSet<>();
            for (Direction dir : Direction.values()) {
                adjacent.add(new Coordinate(this.x + dir.getX(), this.y + dir.getY()));
            }
            return adjacent;
        }
    }

    private enum Direction {
        UP(0, -1),
        UPPER_RIGHT(1, -1),
        RIGHT(1, 0),
        DOWN_RIGHT(1, 1),
        DOWN(0, 1),
        DOWN_LEFT(-1, 1),
        LEFT(-1, 0),
        UPPER_LEFT(-1, -1);

        private final int x;
        private final int y;

        Direction(int x, int y) {
            this.x = x;
            this.y = y;
        }

        public int getX() {
            return x;
        }

        public int getY() {
            return y;
        }
    }
}
