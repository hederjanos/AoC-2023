package day._8;

import util.common.Solver;

import java.util.*;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Day8Solver extends Solver<Long> {
    private static final String START = "AAA";
    private static final String TARGET = "ZZZ";
    private static final Character L = 'L';
    private static final Character R = 'R';
    private static final char A = 'A';
    private static final char Z = 'Z';
    private final Pattern adjacencyPattern = Pattern.compile("^(\\w+)\\s=\\s\\((\\w+),\\s(\\w+)\\)$");
    private final String instructions = puzzle.get(0);
    private final Map<String, Node> nodes;

    public Day8Solver(String fileName) {
        super(fileName);
        nodes = parseNetwork();
    }

    private Map<String, Node> parseNetwork() {
        Map<String, Node> nodes = new HashMap<>();
        for (int i = 2; i < puzzle.size(); i++) {
            Matcher matcher = adjacencyPattern.matcher(puzzle.get(i));
            while (matcher.find()) {
                String parent = matcher.group(1);
                String left = matcher.group(2);
                String right = matcher.group(3);
                nodes.put(parent, new Node(parent, left, right));
            }
        }
        return nodes;
    }

    @Override
    public Long solvePartOne() {
        Predicate<String> targetPredicate = s -> s.equals(TARGET);
        Map<String, Route> routes = getRoutesByPattern(s -> s.equals(TARGET));
        return getSteps(nodes.get(START), targetPredicate, routes);
    }

    private Map<String, Route> getRoutesByPattern(Predicate<String> targetPredicate) {
        return nodes.values().stream()
                .map((Node node) -> getRouteFromByPattern(node, targetPredicate))
                .collect(Collectors.toMap(route -> route.start, route -> route));
    }

    private Route getRouteFromByPattern(Node node, Predicate<String> targetPredicate) {
        boolean target = false;
        if (targetPredicate.test(node.label) || (node.label.equals(node.right) && node.right.equals(node.left))) {
            return new Route(node.label, node.label, 0, target);
        }
        Node next = node;
        int steps = 0;
        for (int i = 0; i < instructions.length(); i++) {
            steps++;
            char charAt = instructions.charAt(i);
            next = charAt == L ? nodes.get(next.left) : nodes.get(next.right);
            if (targetPredicate.test(next.label)) {
                target = true;
                break;
            }
            if (node.label.equals(node.right) && node.right.equals(node.left)) {
                break;
            }
        }
        return new Route(node.label, next.label, steps, target);
    }

    private long getSteps(Node start, Predicate<String> targetPredicate, Map<String, Route> routes) {
        long steps = 0;
        while (!targetPredicate.test(start.label)) {
            Route route = routes.get(start.label);
            steps += route.steps;
            if (route.target) {
                break;
            }
            start = nodes.get(route.end);
        }
        return steps;
    }

    @Override
    public Long solvePartTwo() {
        Predicate<String> startPredicate = s -> s.charAt(s.length() - 1) == A;
        Predicate<String> targetPredicate = s -> s.charAt(s.length() - 1) == Z;

        Map<String, Route> routes = getRoutesByPattern(targetPredicate);

        return nodes.entrySet().stream()
                .filter(stringNodeEntry -> startPredicate.test(stringNodeEntry.getKey()))
                .mapToLong(entry -> getSteps(entry.getValue(), targetPredicate, routes))
                .reduce(this::calculateLCM)
                .orElseThrow(IllegalStateException::new);
    }

    public long calculateLCM(long a, long b) {
        return Math.abs(a * b) / calculateGCD(a, b);
    }

    public long calculateGCD(long a, long b) {
        return b == 0 ? a : calculateGCD(b, a % b);
    }

    private static class Node {
        String label;
        String left;
        String right;

        Node(String label, String left, String right) {
            this.label = label;
            this.left = left;
            this.right = right;
        }
    }

    private static class Route {
        String start;
        String end;
        int steps;
        boolean target;

        Route(String start, String end, int steps, boolean target) {
            this.start = start;
            this.end = end;
            this.steps = steps;
            this.target = target;
        }
    }
}
