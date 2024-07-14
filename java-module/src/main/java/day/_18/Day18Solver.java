package day._18;

import util.common.Solver;
import util.coordinate.Coordinate;
import util.coordinate.Direction;

import java.util.ArrayList;
import java.util.List;

public class Day18Solver extends Solver<Long> {
    private Loop loop;

    public Day18Solver(String fileName) {
        super(fileName);
    }

    @Override
    public Long solvePartOne() {
        loop = parseSimple();
        return loop.getNumberOfPoints();
    }

    private Loop parseSimple() {
        List<Coordinate> points = new ArrayList<>();
        int sumOfBoundaryPoints = 0;
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
                sumOfBoundaryPoints++;
            }
            points.add(current);
        }
        return new Loop(points, sumOfBoundaryPoints);
    }


    @Override
    public Long solvePartTwo() {
        loop = parseComplex();
        return loop.getNumberOfPoints();
    }

    private Loop parseComplex() {
        List<Coordinate> points = new ArrayList<>();
        int sumOfBoundaryPoints = 0;
        Coordinate current = new Coordinate(0, 0);
        for (String line : puzzle) {
            String[] split = line.split(" ");
            String hex = split[2].replace("(#", "").replace(")", "");
            char dir = hex.charAt(hex.length() - 1);
            int quantity = Integer.parseInt(hex.substring(0, 5), 16);
            for (int i = 0; i < quantity; i++) {
                switch (dir) {
                    case '3':
                        current = current.moveByDirection(Direction.UP);
                        break;
                    case '0':
                        current = current.moveByDirection(Direction.RIGHT);
                        break;
                    case '1':
                        current = current.moveByDirection(Direction.DOWN);
                        break;
                    case '2':
                        current = current.moveByDirection(Direction.LEFT);
                        break;
                    default:
                        throw new IllegalArgumentException();
                }
                sumOfBoundaryPoints++;
            }
            points.add(current);
        }
        return new Loop(points, sumOfBoundaryPoints);
    }

    private static class Loop {
        List<Coordinate> cornerPoints;
        long numberOfBoundaryPoints;

        Loop(List<Coordinate> cornerPoints, long numberOfBoundaryPoints) {
            this.cornerPoints = cornerPoints;
            this.numberOfBoundaryPoints = numberOfBoundaryPoints;
        }

        //Pick's theorem, based on Day 10 solutions on reddit sub
        long getNumberOfPoints() {
            return calculateArea() - numberOfBoundaryPoints / 2 + 1 + numberOfBoundaryPoints;
        }

        //Shoelace formula, based on Day 10 solutions on reddit sub
        private long calculateArea() {
            long sum1 = 0;
            long sum2 = 0;

            for (int i = 0; i < cornerPoints.size() - 1; i++) {
                sum1 += (long) cornerPoints.get(i).getX() * cornerPoints.get(i + 1).getY();
                sum2 += (long) cornerPoints.get(i).getY() * cornerPoints.get(i + 1).getX();
            }
            sum1 += (long) cornerPoints.get(cornerPoints.size() - 1).getX() * cornerPoints.get(0).getY();
            sum2 += (long) cornerPoints.get(0).getX() * cornerPoints.get(cornerPoints.size() - 1).getY();

            return Math.abs(sum1 - sum2) / 2;
        }
    }
}
