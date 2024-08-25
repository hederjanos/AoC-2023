package day._8;

import util.common.Solver;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Day8Solver extends Solver<Long> {
    private final Network network;

    public Day8Solver(String fileName) {
        super(fileName);
        network = Network.from(puzzle);
    }

    @Override
    public Long solvePartOne() {
        return network.countStepsFrom("AAA", s -> s.equals("ZZZ"));
    }

    @Override
    public Long solvePartTwo() {
        Predicate<String> startPredicate = s -> s.charAt(s.length() - 1) == 'A';
        Predicate<String> targetPredicate = s -> s.charAt(s.length() - 1) == 'Z';

        return network.getAllNodeLabels().stream()
                .filter(startPredicate)
                .mapToLong(label -> network.countStepsFrom(label, targetPredicate))
                .reduce(this::calculateLCM)
                .orElseThrow(IllegalStateException::new);
    }

    public long calculateLCM(long a, long b) {
        return Math.abs(a * b) / calculateGCD(a, b);
    }

    public long calculateGCD(long a, long b) {
        return b == 0 ? a : calculateGCD(b, a % b);
    }

    private record Network(String instructions, Map<String, Node> nodeMap) {
        static Network from(List<String> puzzle) {
            Pattern pattern = Pattern.compile("^(\\w+)\\s=\\s\\((\\w+),\\s(\\w+)\\)$");
            Map<String, Node> nodeMap = new HashMap<>();
            for (int i = 2; i < puzzle.size(); i++) {
                Matcher matcher = pattern.matcher(puzzle.get(i));
                while (matcher.find()) {
                    String parent = matcher.group(1);
                    String left = matcher.group(2);
                    String right = matcher.group(3);
                    nodeMap.put(parent, new Node(parent, left, right));
                }
            }
            return new Network(puzzle.get(0), nodeMap);
        }

        Set<String> getAllNodeLabels() {
            return nodeMap.keySet();
        }

        long countStepsFrom(String start, Predicate<String> targetPredicate) {
            Node current = nodeMap.get(start);
            long steps = 0;
            while (!targetPredicate.test(current.label)) {
                char instruction = instructions.charAt((int) (steps++ % instructions.length()));
                current = instruction == 'L' ? nodeMap.get(current.left) : nodeMap.get(current.right);
            }
            return steps;
        }
    }

    private record Node(String label, String left, String right) {
    }
}
