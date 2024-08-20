package day._3;

import util.common.Solver;
import util.coordinate.Coordinate;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

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

    private record EngineSchematic(int height, int width, Set<PartNumber> partNumbers, Set<Symbol> symbols) {
        final static char PERIOD = '.';
        final static char ASTERISK = '*';

        static EngineSchematic from(List<String> puzzle) {
            Set<Symbol> symbols = new HashSet<>();
            Set<PartNumber> partNumbers = new HashSet<>();
            for (int i = 0; i < puzzle.size(); i++) {
                String line = puzzle.get(i);
                Matcher numMatcher = Pattern.compile("\\d+").matcher(line);
                while (numMatcher.find()) {
                    String group = numMatcher.group();
                    int start = numMatcher.start();
                    int end = numMatcher.end();
                    int value = Integer.parseInt(group);
                    Set<Coordinate> coordinates = new HashSet<>();
                    for (int j = start; j < end; j++) {
                        coordinates.add(new Coordinate(j, i));
                    }
                    partNumbers.add(new PartNumber(value, coordinates));
                }
                for (int j = 0; j < line.length(); j++) {
                    char charAt = line.charAt(j);
                    if (!Character.isDigit(charAt) && charAt != PERIOD) {
                        symbols.add(new Symbol(charAt, new Coordinate(j, i)));
                    }
                }
            }
            return new EngineSchematic(puzzle.get(0).length(), puzzle.size(), partNumbers, symbols);
        }

        int getSumOfValidPartNumbers() {
            return partNumbers.stream()
                    .filter(this::partNumberIsAdjacentToSymbol)
                    .mapToInt(partNumber -> partNumber.value)
                    .sum();
        }

        boolean partNumberIsAdjacentToSymbol(PartNumber partNumber) {
            return getNeighbours(partNumber).stream()
                    .anyMatch(neighbour -> symbols.stream().anyMatch(symbol -> symbol.coordinate.equals(neighbour)));
        }

        Set<Coordinate> getNeighbours(PartNumber partNumber) {
            return partNumber.getNeighbours().stream()
                    .filter(this::isCoordinateInBounds)
                    .collect(Collectors.toSet());
        }

        boolean isCoordinateInBounds(Coordinate coordinate) {
            return coordinate.getX() < width && coordinate.getX() >= 0 && coordinate.getY() < height && coordinate.getY() >= 0;
        }

        int getSumOfGearRatios() {
            return symbols.stream()
                    .filter(symbol -> symbol.symbol == ASTERISK)
                    .map(this::getAdjacentPartNumbersToSymbol)
                    .filter(partNumbers -> partNumbers.size() == 2)
                    .mapToInt(gearParts -> gearParts.stream()
                            .mapToInt(partNumber -> partNumber.value)
                            .reduce(1, (a, b) -> a * b))
                    .sum();
        }

        Set<PartNumber> getAdjacentPartNumbersToSymbol(Symbol symbol) {
            return symbol.coordinate.getAdjacentCoordinates().stream()
                    .map(this::findPartNumber)
                    .flatMap(Optional::stream)
                    .collect(Collectors.toSet());
        }

        Optional<PartNumber> findPartNumber(Coordinate coordinate) {
            return partNumbers.stream().filter(partNumber -> partNumber.contains(coordinate)).findFirst();
        }

        Optional<Symbol> findSymbol(Coordinate coordinate) {
            return symbols.stream().filter(symbol -> symbol.coordinate.equals(coordinate)).findFirst();
        }

        @Override
        public String toString() {
            return IntStream.range(0, height)
                    .mapToObj(i -> IntStream.range(0, width)
                            .mapToObj(j -> getPrintAt(i, j))
                            .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append))
                    .collect(Collectors.joining(System.lineSeparator()));
        }

        char getPrintAt(int i, int j) {
            char print;
            Coordinate coordinate = new Coordinate(j, i);
            Optional<PartNumber> partNumber = findPartNumber(coordinate);
            if (partNumber.isPresent()) {
                print = (char) ('0' + partNumber.get().value);
            } else {
                Optional<Symbol> containsSymbol = findSymbol(coordinate);
                print = containsSymbol.map(symbol -> symbol.symbol).orElse(PERIOD);
            }
            return print;
        }
    }

    private record PartNumber(int value, Set<Coordinate> coordinates) {
        boolean contains(Coordinate coordinate) {
            return coordinates.contains(coordinate);
        }

        Set<Coordinate> getNeighbours() {
            return coordinates.stream()
                    .flatMap(coordinate -> coordinate.getAdjacentCoordinates().stream())
                    .collect(Collectors.toSet());
        }
    }

    private record Symbol(char symbol, Coordinate coordinate) {
    }
}
