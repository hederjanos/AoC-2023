package day._1;

import util.common.Solver;

public class Day1Solver extends Solver<Integer> {
    public Day1Solver(String filename) {
        super(filename);
    }

    @Override
    public Integer solvePartOne() {
        return puzzle.stream().mapToInt(line -> CalibrationValue.from(line, false).value).sum();
    }

    @Override
    public Integer solvePartTwo() {
        return puzzle.stream().mapToInt(line -> CalibrationValue.from(line, true).value).sum();
    }

    private record CalibrationValue(int value) {
        static CalibrationValue from(String line, boolean withSpelled) {
            int first = -1;
            int last = -1;

            for (int i = 0; i < line.length(); i++) {
                char c = line.charAt(i);
                int currentDigit = -1;

                if (Character.isDigit(c)) {
                    currentDigit = c - '0';
                } else if (withSpelled) {
                    currentDigit = Digit.matchAt(line, i);
                }

                if (currentDigit != -1) {
                    if (first == -1) {
                        first = currentDigit;
                    }
                    last = currentDigit;
                }
            }

            return new CalibrationValue(first * 10 + last);
        }
    }

    private enum Digit {
        ONE(1, "one"),
        TWO(2, "two"),
        THREE(3, "three"),
        FOUR(4, "four"),
        FIVE(5, "five"),
        SIX(6, "six"),
        SEVEN(7, "seven"),
        EIGHT(8, "eight"),
        NINE(9, "nine");

        final int value;
        final String text;

        Digit(int value, String text) {
            this.value = value;
            this.text = text;
        }

        static int matchAt(String line, int index) {
            for (Digit digit : values()) {
                if (line.startsWith(digit.text, index)) {
                    return digit.value;
                }
            }
            return -1;
        }
    }
}
