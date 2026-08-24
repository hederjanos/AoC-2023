package day._6;

import util.common.Solver;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.IntStream;

public class Day6Solver extends Solver<Long> {

    public Day6Solver(String fileName) {
        super(fileName);
    }

    @Override
    public Long solvePartOne() {
        return RaceInfo.parseMultiple(puzzle).stream()
                .mapToLong(RaceInfo::calculateWaysToBeatRecord)
                .reduce(1L, (a, b) -> a * b);
    }

    @Override
    public Long solvePartTwo() {
        return RaceInfo.parseCombined(puzzle).calculateWaysToBeatRecord();
    }

    private static class RaceInfo {
        private static final Pattern NUMBER_PATTERN = Pattern.compile("\\d+");

        private final long time;
        private final long bestDistance;

        RaceInfo(long time, long bestDistance) {
            this.time = time;
            this.bestDistance = bestDistance;
        }

        static List<RaceInfo> parseMultiple(List<String> puzzle) {
            List<Long> times = extractNumbers(puzzle.get(0));
            List<Long> bestDistances = extractNumbers(puzzle.get(1));

            return IntStream.range(0, times.size())
                    .mapToObj(i -> new RaceInfo(times.get(i), bestDistances.get(i)))
                    .toList();
        }

        static RaceInfo parseCombined(List<String> puzzle) {
            long time = extractCombinedNumber(puzzle.get(0));
            long bestDistance = extractCombinedNumber(puzzle.get(1));
            return new RaceInfo(time, bestDistance);
        }

        static List<Long> extractNumbers(String input) {
            List<Long> numbers = new ArrayList<>();
            Matcher matcher = NUMBER_PATTERN.matcher(input);
            while (matcher.find()) {
                numbers.add(Long.parseLong(matcher.group()));
            }
            return numbers;
        }

        static long extractCombinedNumber(String input) {
            return Long.parseLong(input.replaceAll("\\D", ""));
        }

        long calculateWaysToBeatRecord() {
            double a = 1.0;
            double b = -this.time;
            double c = this.bestDistance;

            double discriminant = b * b - 4 * a * c;

            if (discriminant < 0) {
                throw new IllegalStateException("Discriminant is negative, no real roots exist.");
            }

            double sqrtDiscriminant = Math.sqrt(discriminant);

            double root1 = (-b - sqrtDiscriminant) / (2 * a);
            double root2 = (-b + sqrtDiscriminant) / (2 * a);

            root1 = root1 == (int) root1 ? root1 + 1 : Math.ceil(root1);
            root2 = root2 == (int) root2 ? root2 - 1 : Math.floor(root2);

            return (long) (root2 - root1 + 1);
        }
    }
}