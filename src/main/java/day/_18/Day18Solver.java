package day._18;

import util.common.Solver;
import util.coordinate.Coordinate;
import util.coordinate.Direction;

import java.util.ArrayList;
import java.util.List;

public class Day18Solver extends Solver<Long> {
    private List<Coordinate> boundaryPoints;

    public Day18Solver(String fileName) {
        super(fileName);
    }

    @Override
    public Long solvePartOne() {
        boundaryPoints = parseSimple();
        return boundaryPoints.size() + getInteriorPoints();
    }

    private List<Coordinate> parseSimple() {
        List<Coordinate> points = new ArrayList<>();
        Coordinate current = new Coordinate(0, 0);
        for (String line : puzzle) {
            String[] split = line.split(" ");
            char dir = split[0].charAt(0);
            int quantity = Integer.parseInt(split[1]);
            for (int i = 0; i < quantity; i++) {
                switch (dir) {
                    case 'U':
                        current = current.moveByDirection(Direction.UP);
                        break;
                    case 'R':
                        current = current.moveByDirection(Direction.RIGHT);
                        break;
                    case 'D':
                        current = current.moveByDirection(Direction.DOWN);
                        break;
                    case 'L':
                        current = current.moveByDirection(Direction.LEFT);
                        break;
                    default:
                        throw new IllegalArgumentException();
                }
                points.add(current);
            }
        }
        return points;
    }

    //Pick's theorem, based on Day 10 solutions on reddit sub
    private long getInteriorPoints() {
        return calculateArea() - boundaryPoints.size() / 2 + 1;
    }

    //Shoelace formula, based on Day 10 solutions on reddit sub
    private long calculateArea() {
        double sum1 = 0;
        double sum2 = 0;

        for (int i = 0; i < boundaryPoints.size() - 1; i++) {
            sum1 += boundaryPoints.get(i).getX() * boundaryPoints.get(i + 1).getY();
            sum2 += boundaryPoints.get(i).getY() * boundaryPoints.get(i + 1).getX();
        }
        sum1 += boundaryPoints.get(boundaryPoints.size() - 1).getX() * boundaryPoints.get(0).getY();
        sum2 += boundaryPoints.get(0).getX() * boundaryPoints.get(boundaryPoints.size() - 1).getY();

        return (long) Math.abs(sum1 - sum2) / 2;
    }

    @Override
    public Long solvePartTwo() {
        return null;
    }
}
