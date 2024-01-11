package day._19;

import util.common.Solver;

import java.util.*;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Day19Solver extends Solver<Integer> {
    private static final Pattern WORKFLOW_PATTERN = Pattern.compile("^(\\w+)\\{(.+)}$");
    private static final Pattern RULE_PATTERN = Pattern.compile("(\\w)(>|<)(\\d+):(\\w+)");
    private static final Pattern MACHINE_PART_PATTERN = Pattern.compile("^\\{(.+)}$");
    private static final Pattern CATEGORY_PATTERN = Pattern.compile("(x|m|a|s)=(\\d+)");

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
            List<SimpleRule> rules = Arrays.stream(ruleGroup.split(","))
                    .map(this::createRule)
                    .collect(Collectors.toList());
            return new WorkFlow(workflow, rules);
        } else {
            throw new IllegalArgumentException();
        }
    }

    private SimpleRule createRule(String rule) {
        Matcher ruleMatcher = RULE_PATTERN.matcher(rule);
        if (ruleMatcher.find()) {
            char ch = ruleMatcher.group(1).charAt(0);
            int number = Integer.parseInt(ruleMatcher.group(3));
            Predicate<Integer> predicate = rule.contains("<") ? (i -> i < number) : (i -> i > number);
            String workFlow = ruleMatcher.group(4);
            return new TestRule(workFlow, ch, predicate);
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
    public Integer solvePartOne() {
        int sum = 0;
        for (MachinePart machinePart : machinePartList) {
            WorkFlow workFlow = workflowMap.get("in");
            String answer = "";
            while (!Objects.equals(answer, "A") && !Objects.equals(answer, "R")) {
                answer = workFlow.test(machinePart);
                workFlow = workflowMap.get(answer);
            }
            if (Objects.equals(answer, "A")) {
                System.out.println(machinePart.getValue());
                sum += machinePart.getValue();
            }
        }
        return sum;
    }

    @Override
    public Integer solvePartTwo() {
        return null;
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
                    Predicate<Integer> predicate = ((TestRule) rule).predicate;
                    Integer number = machinePart.values.get(ch);
                    if (predicate.test(number)) {
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
        Predicate<Integer> predicate;

        TestRule(String workFlow, char ch, Predicate<Integer> predicate) {
            super(workFlow);
            this.ch = ch;
            this.predicate = predicate;
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
