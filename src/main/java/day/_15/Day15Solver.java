package day._15;

import util.common.Solver;

import java.util.*;
import java.util.stream.IntStream;

public class Day15Solver extends Solver<Integer> {
    private List<Box> boxes;

    public Day15Solver(String fileName) {
        super(fileName);
    }

    @Override
    public Integer solvePartOne() {
        return Arrays.stream(puzzle.get(0).split(",")).mapToInt(this::hash).sum();
    }

    private int hash(String operation) {
        int hash = 0;
        for (int i = 0; i < operation.length(); i++) {
            char ch = operation.charAt(i);
            hash += ch;
            hash *= 17;
            hash %= 256;
        }
        return hash;
    }

    @Override
    public Integer solvePartTwo() {
        boxes = loadBoxes();
        return calculateFocusingPower(boxes);
    }

    private List<Box> loadBoxes() {
        List<Box> boxes = new LinkedList<>();
        IntStream.range(0, 256).forEach(i -> boxes.add(new Box()));
        for (String s : puzzle.get(0).split(",")) {
            Box lens;
            if (s.contains("=")) {
                String[] split = s.split("=");
                String label = split[0];
                lens = boxes.get(hash(label));
                int focalLength = Integer.parseInt(split[1]);
                Lens newLens = new Lens(label, focalLength);
                int i = lens.indexOf(newLens);
                if (i != -1) {
                    lens.replace(i, newLens);
                } else {
                    lens.add(newLens);
                }
            } else {
                String[] split = s.split("-");
                String label = split[0];
                lens = boxes.get(hash(label));
                lens.remove(new Lens(label, 0));
            }
        }
        return boxes;
    }

    private int calculateFocusingPower(List<Box> boxes) {
        return IntStream.range(0, boxes.size())
                .boxed()
                .mapToInt(i -> {
                    Box lens = boxes.get(i);
                    return IntStream.range(0, lens.size())
                            .boxed()
                            .mapToInt(j -> {
                                Lens l = lens.get(j);
                                return (i + 1) * (j + 1) * l.focalLength;
                            })
                            .sum();
                })
                .sum();
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        IntStream.range(0, boxes.size())
                .boxed()
                .filter(i -> !boxes.get(i).isEmpty())
                .forEach(i -> stringBuilder.append("Box ").append(i).append(": ").append(boxes.get(i)));
        return stringBuilder.toString();
    }

    private static class Box extends LinkedList<Lens> {
        void replace(int i, Lens lens) {
            remove(i);
            add(i, lens);
        }

        @Override
        public String toString() {
            StringBuilder stringBuilder = new StringBuilder();
            IntStream.range(0, size())
                    .boxed()
                    .forEach(j -> stringBuilder.append(get(j).toString()).append(" "));
            return stringBuilder.toString();
        }
    }

    private static class Lens {
        String label;
        int focalLength;

        Lens(String label, int focalLength) {
            this.label = label;
            this.focalLength = focalLength;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Lens)) return false;
            Lens lens = (Lens) o;
            return Objects.equals(label, lens.label);
        }

        @Override
        public int hashCode() {
            return Objects.hash(label);
        }

        @Override
        public String toString() {
            return "[" + label + " " + focalLength + "]";
        }
    }
}
