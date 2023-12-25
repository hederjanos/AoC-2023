package day._13;

import util.common.Solver;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class Day13Solver extends Solver<Integer> {
    public Day13Solver(String fileName) {
        super(fileName);
    }

    @Override
    public Integer solvePartOne() {
        puzzle.add("");
        List<StringBuilder> rows = new ArrayList<>();
        List<StringBuilder> cols = new ArrayList<>();
        int maxSize = puzzle.stream().mapToInt(String::length).max().getAsInt();
        for (int i = 0; i < maxSize; i++) {
            cols.add(new StringBuilder());
        }

        int sum = 0;
        for (int j = 0; j < puzzle.size(); j++) {
            String s = puzzle.get(j);
            if (s.isEmpty()) {
                List<BigInteger> cs = cols.stream().filter(stringBuilder -> !stringBuilder.toString().isEmpty()).map(stringBuilder -> new BigInteger(stringBuilder.toString())).collect(Collectors.toList());
                int reflection = isReflection(cs);
                if (reflection != -1) {
                    sum += reflection + 1;
                } else {
                    List<BigInteger> rs = rows.stream().map(stringBuilder -> new BigInteger(stringBuilder.toString())).collect(Collectors.toList());
                    reflection = isReflection(rs);
                    if (reflection != -1) {
                        sum += (reflection + 1) * 100;
                    }
                }

                rows = new ArrayList<>();
                cols = new ArrayList<>();
                for (int i = 0; i < maxSize; i++) {
                    cols.add(new StringBuilder());
                }
                continue;
            }

            StringBuilder rBuilder = new StringBuilder();
            for (int i = 0; i < s.length(); i++) {
                if (s.charAt(i) == '#') {
                    rBuilder.append(i + 1);
                    StringBuilder cBuilder = cols.get(i);
                    cBuilder.append(j + 1);
                }
            }
            rows.add(rBuilder);
        }
        return sum;
    }

    private int isReflection(List<BigInteger> rs) {
        int reflection = -1;
        for (int i = 0; i < rs.size() - 1; i++) {
            if (Objects.equals(rs.get(i), rs.get(i + 1))) {

                List<BigInteger> sub1 = rs.subList(0, i + 1);
                List<BigInteger> sub2 = rs.subList(i + 1, rs.size());

                int min = Math.min(sub1.size(), sub2.size());

                BigInteger sum1;
                BigInteger sum2;
                if (min == sub1.size()) {
                    sum1 = sub1.stream().reduce(BigInteger::add).get();
                    sum2 = rs.subList(i + 1, i + 1 + min).stream().reduce(BigInteger::add).get();
                } else {
                    sum1 = sub2.stream().reduce(BigInteger::add).get();
                    sum2 = rs.subList(i + 1 - min, i + 1).stream().reduce(BigInteger::add).get();
                }

                if (Objects.equals(sum1, sum2)) {
                    reflection = i;
                    break;
                }
            }
        }
        return reflection;
    }

    @Override
    public Integer solvePartTwo() {
        return null;
    }
}
