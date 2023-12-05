package day._1;

import util.common.Solver;

import java.util.ArrayList;
import java.util.List;

public class Day1Solver extends Solver<Integer> {

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

    public Day1Solver(String filename) {
        super(filename);
    }

    @Override
    public Integer solvePartOne() {
        return puzzle.stream().map(line -> collectDigitsInOneLine(line, false)).mapToInt(this::parseCalibrationValue).sum();
    }

    private List<Integer> collectDigitsInOneLine(String line, boolean withSpelled) {
        List<Integer> digits = new ArrayList<>();
        for (int i = 0; i < line.length(); i++) {
            char charAt = line.charAt(i);
            if (Character.isDigit(charAt)) {
                digits.add(Character.getNumericValue(charAt));
            } else if (withSpelled) {
                String substring = line.substring(i);
                for (Digit digit : Digit.values()) {
                    if (substring.startsWith(digit.name())) {
                        digits.add(digit.getValue());
                        break;
                    }
                }
            }
        }
        return digits;
    }

    private int parseCalibrationValue(List<Integer> digits) {
        return Integer.parseInt(digits.get(0) + String.valueOf(digits.get(digits.size() - 1)));
    }

    @Override
    public Integer solvePartTwo() {
        return puzzle.stream().map(line -> collectDigitsInOneLine(line, true)).mapToInt(this::parseCalibrationValue).sum();
    }

}
