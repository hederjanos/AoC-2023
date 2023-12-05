package day._3;

import util.common.Solver;
import util.coordinate.Coordinate;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Day3Solver extends Solver<Integer> {
    private final Pattern numbersPattern = Pattern.compile("\\d+");
    private final char PERIOD = '.';
    private final char ASTERISK = '*';
    private final EngineSchematic engineSchematic;

    public Day3Solver(String fileName) {
        super(fileName);
        engineSchematic = parsePuzzle();
    }

    private EngineSchematic parsePuzzle() {
        Set<PartNumber> partNumbers = new HashSet<>();
        Set<Symbol> symbols = new HashSet<>();
        for (int i = 0; i < puzzle.size(); i++) {
            String line = puzzle.get(i);
            Matcher matcher = numbersPattern.matcher(line);
            while (matcher.find()) {
                String group = matcher.group();
                int start = matcher.start();
                int end = matcher.end();
                int number = Integer.parseInt(group);
                Set<Symbol> symbolSet = new HashSet<>();
                for (int j = start; j < end; j++) {
                    symbolSet.add(new Symbol(line.charAt(j), new Coordinate(j, i)));
                }
                partNumbers.add(new PartNumber(symbolSet, number));
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

    @Override
    public Integer solvePartOne() {
        return engineSchematic.getSumOfValidPartNumbers();
    }

    @Override
    public Integer solvePartTwo() {
        return engineSchematic.getSumOfGearRatios();
    }

    private class EngineSchematic {
        int width;
        int height;
        Set<PartNumber> partNumbers;
        Set<Symbol> symbols;

        public EngineSchematic(int height, int width, Set<PartNumber> partNumbers, Set<Symbol> symbols) {
            this.width = width;
            this.height = height;
            this.partNumbers = partNumbers;
            this.symbols = symbols;
        }

        int getSumOfValidPartNumbers() {
            return partNumbers.stream().filter(this::partNumberIsAdjacentToSymbol).mapToInt(partNumber -> partNumber.value).sum();
        }

        boolean partNumberIsAdjacentToSymbol(PartNumber partNumber) {
            return getNeighbours(partNumber).stream()
                    .anyMatch(neighbour -> symbols.stream()
                            .map(symbol -> symbol.coordinate)
                            .anyMatch(symbol -> symbol.equals(neighbour)));
        }

        Set<Coordinate> getNeighbours(PartNumber partNumber) {
            return partNumber.getNeighbours(partNumber).stream()
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

        private Set<PartNumber> getAdjacentPartNumbersToSymbol(Symbol symbol) {
            Set<Coordinate> adjacentCoordinates = symbol.coordinate.getAdjacentCoordinates();
            Set<PartNumber> gearParts = new HashSet<>();
            for (Coordinate adjacentCoordinate : adjacentCoordinates) {
                for (PartNumber partNumber : partNumbers) {
                    partNumber.coordinateIsPartOfMe(adjacentCoordinate).ifPresent(gearParts::add);
                }
            }
            return gearParts;
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
            Optional<Symbol> containsPartNumber = containsPartNumber(coordinate);
            if (containsPartNumber.isPresent()) {
                print = containsPartNumber.get().symbol;
            } else {
                Optional<Symbol> containsSymbol = containsSymbol(coordinate);
                print = containsSymbol.map(symbol -> symbol.symbol).orElse(PERIOD);
            }
            return print;
        }

        Optional<Symbol> containsPartNumber(Coordinate coordinate) {
            Optional<Symbol> optionalSymbol = Optional.empty();
            for (PartNumber partNumber : partNumbers) {
                Optional<Symbol> contains = partNumber.contains(coordinate);
                if (contains.isPresent()) {
                    optionalSymbol = contains;
                    break;
                }
            }
            return optionalSymbol;
        }

        Optional<Symbol> containsSymbol(Coordinate coordinate) {
            Optional<Symbol> optionalSymbol = Optional.empty();
            for (Symbol symbol : symbols) {
                if (symbol.coordinate.equals(coordinate)) {
                    optionalSymbol = Optional.of(symbol);
                    break;
                }
            }
            return optionalSymbol;
        }
    }

    private static class PartNumber {
        Set<Symbol> symbols;
        int value;

        public PartNumber(Set<Symbol> symbols, int value) {
            this.symbols = symbols;
            this.value = value;
        }

        Optional<PartNumber> coordinateIsPartOfMe(Coordinate coordinate) {
            boolean anyMatch = symbols.stream().anyMatch(symbol -> symbol.coordinate.equals(coordinate));
            return anyMatch ? Optional.of(this) : Optional.empty();
        }

        Set<Coordinate> getNeighbours(PartNumber partNumber) {
            return partNumber.symbols.stream()
                    .flatMap(symbol -> symbol.coordinate.getAdjacentCoordinates().stream())
                    .collect(Collectors.toSet());
        }

        Optional<Symbol> contains(Coordinate coordinate) {
            return symbols.stream().filter(symbol -> symbol.coordinate.equals(coordinate)).findFirst();
        }
    }

    private static class Symbol {
        char symbol;
        Coordinate coordinate;

        public Symbol(char symbol, Coordinate coordinate) {
            this.symbol = symbol;
            this.coordinate = coordinate;
        }
    }
}
