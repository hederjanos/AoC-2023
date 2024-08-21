package day._6;

import util.common.Solver;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Day6Solver extends Solver<Long> {
    private final List<RaceInfo> raceInfoList;

    public Day6Solver(String fileName) {
        super(fileName);
        raceInfoList = parseRaceData();
    }

    private List<RaceInfo> parseRaceData() {
        List<Integer> times = extractIntegers(puzzle.get(0));
        List<Integer> bestDistances = extractIntegers(puzzle.get(1));

        return IntStream.range(0, times.size())
                .mapToObj(i -> new RaceInfo(times.get(i), bestDistances.get(i)))
                .collect(Collectors.toList());
    }

    private List<Integer> extractIntegers(String input) {
        List<Integer> integers = new ArrayList<>();
        Matcher matcher = Pattern.compile("\\d+").matcher(input);
        while (matcher.find()) {
            integers.add(Integer.parseInt(matcher.group()));
        }
        return integers;
    }

    @Override
    public Long solvePartOne() {
        return raceInfoList.stream().mapToLong(this::getNumberOfWaysToBeatRecord).reduce(1L, (a, b) -> a * b);
    }

    private long getNumberOfWaysToBeatRecord(RaceInfo raceInfo) {
        double a = 1.0, b = raceInfo.time, c = raceInfo.bestDistance;
        double discriminant = b * b - 4 * a * c;

        if (discriminant < 0) {
            throw new IllegalStateException("Discriminant is negative, no real roots exist.");
        }

        double sqrtDiscriminant = Math.sqrt(discriminant);

        double root1 = (-b + sqrtDiscriminant) / (2 * a) * -1;
        double root2 = (-b - sqrtDiscriminant) / (2 * a) * -1;

        root1 = root1 == (int) root1 ? root1 + 1 : Math.ceil(root1);
        root2 = root2 == (int) root2 ? root2 - 1 : Math.floor(root2);

        return (long) (root2 - root1 + 1);
    }

    @Override
    public Long solvePartTwo() {
        return getNumberOfWaysToBeatRecord(parseRace());
    }

    private RaceInfo parseRace() {
        List<Integer> times = extractIntegers(puzzle.get(0));
        List<Integer> bestDistances = extractIntegers(puzzle.get(1));
        return new RaceInfo(joinIntegersAndParse(times), joinIntegersAndParse(bestDistances));
    }

    private long joinIntegersAndParse(List<Integer> integers) {
        return Long.parseLong(integers.stream()
                .map(Object::toString)
                .collect(Collectors.joining("")));
    }

    private record RaceInfo(long time, long bestDistance) {
    }
}
