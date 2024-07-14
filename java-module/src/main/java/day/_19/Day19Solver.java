package day._19;

import util.common.Solver;

import java.util.*;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Day19Solver extends Solver<Long> {
    private static final Pattern WORKFLOW_PATTERN = Pattern.compile("^(\\w+)\\{(.+)}$");
    private static final Pattern RULE_PATTERN = Pattern.compile("(\\w)(>|<)(\\d+):(\\w+)");
    private static final Pattern MACHINE_PART_PATTERN = Pattern.compile("^\\{(.+)}$");
    private static final Pattern CATEGORY_PATTERN = Pattern.compile("(x|m|a|s)=(\\d+)");
    private static final String IN = "in";
    private static final String ACCEPTED = "A";
    private static final String REJECTED = "R";
    private static final String GT = ">";
    private static final String LT = "<";

    private final Map<String, WorkFlow> workflowMap;
    private final List<MachinePart> machinePartList;

    public Day19Solver(String fileName) {
        super(fileName);
        workflowMap = getWorkFlowMap();
        machinePartList = getMachineParts();
    }

    private Map<String, WorkFlow> getWorkFlowMap() {
        return puzzle.stream()
                .filter(line -> WORKFLOW_PATTERN.matcher(line).matches())
                .map(this::createWorkFlow)
                .collect(Collectors.toMap(w -> w.name, w -> w));
    }

    private WorkFlow createWorkFlow(String line) {
        Matcher workFlowMatcher = WORKFLOW_PATTERN.matcher(line);
        if (workFlowMatcher.find()) {
            String workflow = workFlowMatcher.group(1);
            String ruleGroup = workFlowMatcher.group(2);
            List<SimpleRule> rules = Arrays.stream(ruleGroup.split(",")).map(this::createRule).collect(Collectors.toList());
            return new WorkFlow(workflow, rules);
        } else {
            throw new IllegalArgumentException();
        }
    }

    private SimpleRule createRule(String rule) {
        Matcher ruleMatcher = RULE_PATTERN.matcher(rule);
        if (ruleMatcher.find()) {
            String workFlow = ruleMatcher.group(4);
            char ch = ruleMatcher.group(1).charAt(0);
            String op = rule.contains(GT) ? GT : LT;
            int number = Integer.parseInt(ruleMatcher.group(3));
            return new TestRule(workFlow, ch, op, number);
        } else {
            return new SimpleRule(rule);
        }
    }

    private List<MachinePart> getMachineParts() {
        return puzzle.stream()
                .filter(line -> MACHINE_PART_PATTERN.matcher(line).matches())
                .map(this::createMachinePart)
                .collect(Collectors.toList());
    }

    private MachinePart createMachinePart(String line) {
        Map<Character, Integer> values = new HashMap<>();
        Matcher matcher = CATEGORY_PATTERN.matcher(line);
        while (matcher.find()) {
            char category = matcher.group(1).charAt(0);
            int value = Integer.parseInt(matcher.group(2));
            values.put(category, value);
        }
        return new MachinePart(Collections.unmodifiableMap(values));
    }

    @Override
    public Long solvePartOne() {
        long sum = 0;
        for (MachinePart machinePart : machinePartList) {
            WorkFlow workFlow = workflowMap.get(IN);
            String answer = "";
            while (!Objects.equals(answer, ACCEPTED) && !Objects.equals(answer, REJECTED)) {
                answer = workFlow.test(machinePart);
                workFlow = workflowMap.get(answer);
            }
            if (Objects.equals(answer, ACCEPTED)) {
                sum += machinePart.getValue();
            }
        }
        return sum;
    }

    @Override
    public Long solvePartTwo() {
        List<Conditions> conditionsList = new ArrayList<>();
        getAllPossibleCombinations(IN, new Conditions(), conditionsList);
        return conditionsList.stream().mapToLong(Conditions::calculateCombinations).sum();
    }

    private void getAllPossibleCombinations(String workFlowName, Conditions conditions, List<Conditions> conditionsList) {
        if (workFlowName.equals(ACCEPTED)) {
            conditionsList.add(conditions);
        } else if (workFlowName.equals(REJECTED)) {
            return;
        }
        WorkFlow workFlow = workflowMap.get(workFlowName);
        if (workFlow != null && workFlow.rules != null) {
            for (SimpleRule rule : workFlow.rules) {
                Conditions complementaryCondition = new Conditions(conditions);
                if (rule instanceof TestRule) {
                    TestRule testRule = (TestRule) rule;
                    conditions.modifyBoundariesFor(testRule.ch, testRule.op, testRule.number);
                    String op = testRule.op.equals(GT) ? LT : GT;
                    int number = testRule.op.equals(GT) ? testRule.number + 1 : testRule.number - 1;
                    complementaryCondition.modifyBoundariesFor(testRule.ch, op, number);
                }
                getAllPossibleCombinations(rule.workFlow, conditions, conditionsList);
                conditions = complementaryCondition;
            }
        }
    }

    private static class Conditions {
        Map<Character, List<Integer>> characterMap = new HashMap<>();

        Conditions() {
            characterMap.put('a', Arrays.asList(1, 4000));
            characterMap.put('s', Arrays.asList(1, 4000));
            characterMap.put('m', Arrays.asList(1, 4000));
            characterMap.put('x', Arrays.asList(1, 4000));
        }

        Conditions(Conditions conditions) {
            this.characterMap = new HashMap<>();
            characterMap.put('a', conditions.getBoundariesFor('a'));
            characterMap.put('s', conditions.getBoundariesFor('s'));
            characterMap.put('m', conditions.getBoundariesFor('m'));
            characterMap.put('x', conditions.getBoundariesFor('x'));
        }

        List<Integer> getBoundariesFor(char ch) {
            return new ArrayList<>(characterMap.get(ch));
        }

        void modifyBoundariesFor(char ch, String op, int number) {
            List<Integer> boundaries = characterMap.get(ch);
            if (op.equals(LT)) {
                boundaries.set(1, Math.min(boundaries.get(1), number - 1));
                if (boundaries.get(1) < boundaries.get(0)) {
                    boundaries.set(0, 1);
                }
            } else {
                boundaries.set(0, Math.max(boundaries.get(0), number + 1));
                if (boundaries.get(1) < boundaries.get(0)) {
                    boundaries.set(1, boundaries.get(0));
                    boundaries.set(0, 1);
                }
            }
        }

        long calculateCombinations() {
            return characterMap.values().stream().mapToLong(boundaries -> boundaries.get(1) - boundaries.get(0) + 1).reduce(1L, (a, b) -> a * b);
        }

        String display() {
            StringBuilder sb = new StringBuilder();
            characterMap.forEach((key, value) -> {
                sb.append(key);
                sb.append(": ");
                StringBuilder boundariesSb = new StringBuilder();
                value.forEach(i -> {
                    boundariesSb.append(i);
                    boundariesSb.append(", ");
                });
                sb.append(boundariesSb.substring(0, boundariesSb.length() - 2));
                sb.append("\n");
            });
            return sb.toString();
        }
    }

    private static class WorkFlow {
        String name;
        List<SimpleRule> rules;

        WorkFlow(String name, List<SimpleRule> rules) {
            this.name = name;
            this.rules = rules;
        }

        String test(MachinePart machinePart) {
            int i = 0;
            SimpleRule rule;
            String answer = "";
            while (Objects.equals(answer, "")) {
                rule = rules.get(i);
                if (rule instanceof TestRule) {
                    char ch = ((TestRule) rule).ch;
                    Integer number = machinePart.values.get(ch);
                    if (((TestRule) rule).test(number)) {
                        answer = rule.workFlow;
                    } else {
                        i++;
                    }
                } else {
                    answer = rule.workFlow;
                }
            }
            return answer;
        }
    }

    private static class SimpleRule {
        String workFlow;

        SimpleRule(String workFlow) {
            this.workFlow = workFlow;
        }
    }

    private static class TestRule extends SimpleRule {
        char ch;
        String op;
        int number;
        Predicate<Integer> predicate;

        TestRule(String workFlow, char ch, String op, int number) {
            super(workFlow);
            this.ch = ch;
            this.op = op;
            this.number = number;
            this.predicate = this.op.equals(LT) ? (i -> i < number) : (i -> i > number);
        }

        boolean test(int number) {
            return predicate.test(number);
        }
    }

    private static class MachinePart {
        final Map<Character, Integer> values;

        MachinePart(Map<Character, Integer> values) {
            this.values = values;
        }

        int getValue() {
            return values.values().stream().mapToInt(i -> i).sum();
        }
    }
}
