package day._1;

import util.common.Solver;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class TrebuchetSolver extends Solver<Integer> {

    private enum Digit {
        one(1), two(2), three(3), four(4), five(5), six(6), seven(7), eight(8), nine(9);

        private final int value;

        Digit(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }

    public TrebuchetSolver(String filename) {
        super(filename);
    }

    @Override
    public Integer solvePartOne() {
        return puzzle.stream().map(this::collectDigitsInOneLine).mapToInt(this::parseCalibrationValue).sum();
    }

    private List<DigitPosition> collectDigitsInOneLine(String line) {
        List<DigitPosition> digits = new ArrayList<>();
        for (int i = 0; i < line.length(); i++) {
            char charAt = line.charAt(i);
            if (Character.isDigit(charAt)) {
                digits.add(new DigitPosition(Character.getNumericValue(charAt), i));
            }
        }
        return digits;
    }

    private int parseCalibrationValue(List<DigitPosition> positions) {
        return Integer.parseInt(positions.get(0).getDigit() + String.valueOf(positions.get(positions.size() - 1).getDigit()));
    }

    @Override
    protected Integer solvePartTwo() {
        Map<Integer, List<DigitPosition>> digitMap = IntStream.range(0, puzzle.size())
                .boxed()
                .collect(Collectors.toMap(
                        i -> i,
                        i -> collectDigitsInOneLine(puzzle.get(i)),
                        (existing, replacement) -> existing,
                        HashMap::new
                ));

        Map<Integer, List<DigitPosition>> spelledDigitMap = IntStream.range(0, puzzle.size())
                .boxed()
                .collect(Collectors.toMap(
                        i -> i,
                        i -> collectSpelledDigitsInOneLine(puzzle.get(i)),
                        (existing, replacement) -> existing,
                        HashMap::new
                ));

        return IntStream.range(0, puzzle.size())
                .mapToObj(i -> Stream.concat(digitMap.get(i).stream(), spelledDigitMap.get(i).stream())
                        .sorted(Comparator.comparingInt(DigitPosition::getPosition))
                        .collect(Collectors.toList()))
                .mapToInt(this::parseCalibrationValue)
                .sum();
    }

    private List<DigitPosition> collectSpelledDigitsInOneLine(String line) {
        List<DigitPosition> digits = new ArrayList<>();
        Arrays.stream(Digit.values())
                .forEach(digit -> {
                    Pattern pattern = Pattern.compile(digit.name());
                    Matcher matcher = pattern.matcher(line);
                    while (matcher.find()) {
                        digits.add(new DigitPosition(digit.getValue(), matcher.start()));
                    }
                });
        return digits;
    }

    private static class DigitPosition {
        private final int digit;
        private final int position;

        public DigitPosition(int digit, int position) {
            this.digit = digit;
            this.position = position;
        }

        public int getDigit() {
            return digit;
        }

        public int getPosition() {
            return position;
        }
    }

}
