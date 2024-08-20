package day._1;

import util.common.Solver;

import java.util.ArrayList;
import java.util.List;

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
            List<Integer> digits = new ArrayList<>();
            int i = 0;
            while (i < line.length()) {
                boolean matchedWithSpelled = false;
                char charAt = line.charAt(i);
                if (Character.isDigit(charAt)) {
                    digits.add(Character.getNumericValue(charAt));
                } else if (withSpelled) {
                    String substring = line.substring(i);
                    for (Digit digit : Digit.values()) {
                        if (substring.startsWith(digit.name().toLowerCase())) {
                            digits.add(digit.getValue());
                            i += digit.name().length() - 1;
                            matchedWithSpelled = true;
                            break;
                        }
                    }
                }
                if (!matchedWithSpelled) {
                    i++;
                }
            }
            return new CalibrationValue(Integer.parseInt(digits.get(0) + "" + digits.get(digits.size() - 1)));
        }
    }

    private enum Digit {
        ONE(1),
        TWO(2),
        THREE(3),
        FOUR(4),
        FIVE(5),
        SIX(6),
        SEVEN(7),
        EIGHT(8),
        NINE(9);

        final int value;

        Digit(int value) {
            this.value = value;
        }

        int getValue() {
            return value;
        }
    }
}
