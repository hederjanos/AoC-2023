package day._16;

import util.common.Solver;
import util.coordinate.Coordinate;
import util.coordinate.Direction;

import java.util.*;
import java.util.stream.Collectors;

public class Day16Solver extends Solver<Integer> {
    private final int width;
    private final int height;

    private final Map<Coordinate, MirrorType> mirrors;

    public Day16Solver(String fileName) {
        super(fileName);
        width = puzzle.get(0).length();
        height = puzzle.size();
        mirrors = parseMirrors();
    }

    private Map<Coordinate, MirrorType> parseMirrors() {
        Map<Coordinate, MirrorType> mirrorMap = new HashMap<>();
        for (int i = 0; i < puzzle.size(); i++) {
            String line = puzzle.get(i);
            for (int j = 0; j < line.length(); j++) {
                char ch = line.charAt(j);
                if (ch != '.') {
                    mirrorMap.put(new Coordinate(j, i), MirrorType.getType(ch));
                }
            }
        }
        return mirrorMap;
    }

    @Override
    public Integer solvePartOne() {
        return scanFrom(new Coordinate(0, 0), Direction.RIGHT);
    }

    private int scanFrom(Coordinate initCoordinate, Direction heading) {
        Set<Beam> visited = new HashSet<>();
        visited.add(new Beam(initCoordinate, heading));
        Set<Beam> initBeams;
        MirrorType mirror = mirrors.get(initCoordinate);
        if (mirror != null) {
            initBeams = getPossibleBeams(mirror, initCoordinate, heading);
            visited.addAll(initBeams);
        } else {
            initBeams = Set.of(new Beam(initCoordinate, heading));
        }
        scanBeamPath(initBeams, visited);
        return visited.stream().map(beam -> beam.coordinate).collect(Collectors.toSet()).size();
    }

    private void scanBeamPath(Set<Beam> initBeams, Set<Beam> visited) {
        Deque<Beam> beamDeque = new ArrayDeque<>();
        initBeams.forEach(beamDeque::offer);
        while (!beamDeque.isEmpty()) {
            Beam poll = beamDeque.poll();
            Coordinate nextLocation = poll.coordinate.moveByDirection(poll.heading);
            if (isCoordinateInBounds(nextLocation)) {
                Set<Beam> possibleBeams;
                MirrorType mirrorType = mirrors.get(nextLocation);
                if (mirrorType != null) {
                    possibleBeams = getPossibleBeams(mirrorType, nextLocation, poll.heading);
                } else {
                    possibleBeams = Set.of(new Beam(nextLocation, poll.heading));
                }
                possibleBeams.stream()
                        .filter(beam -> !visited.contains(beam))
                        .forEach(beam -> {
                            beamDeque.offer(beam);
                            visited.add(beam);
                        });
            }
        }
    }

    private Set<Beam> getPossibleBeams(MirrorType mirrorType, Coordinate nextLocation, Direction heading) {
        Set<Beam> beams = new HashSet<>();
        switch (mirrorType) {
            case VERTICAL:
                handleVerticalMirror(nextLocation, heading, beams);
                break;
            case HORIZONTAL:
                handleHorizontalMirror(nextLocation, heading, beams);
                break;
            case FORWARD:
                handleForwardMirror(nextLocation, heading, beams);
                break;
            case BACKWARD:
                handleBackwardMirror(nextLocation, heading, beams);
                break;
        }
        return beams;
    }

    private void handleVerticalMirror(Coordinate nextLocation, Direction heading, Set<Beam> beams) {
        switch (heading) {
            case UP:
            case DOWN:
                beams.add(new Beam(nextLocation, heading));
                break;
            case RIGHT:
            case LEFT:
                beams.add(new Beam(nextLocation, Direction.UP));
                beams.add(new Beam(nextLocation, Direction.DOWN));
                break;
        }
    }

    private void handleHorizontalMirror(Coordinate nextLocation, Direction heading, Set<Beam> beams) {
        switch (heading) {
            case UP:
            case DOWN:
                beams.add(new Beam(nextLocation, Direction.RIGHT));
                beams.add(new Beam(nextLocation, Direction.LEFT));
                break;
            case RIGHT:
            case LEFT:
                beams.add(new Beam(nextLocation, heading));
                break;
        }
    }

    private void handleForwardMirror(Coordinate nextLocation, Direction heading, Set<Beam> beams) {
        switch (heading) {
            case UP:
                beams.add(new Beam(nextLocation, Direction.RIGHT));
                break;
            case DOWN:
                beams.add(new Beam(nextLocation, Direction.LEFT));
                break;
            case RIGHT:
                beams.add(new Beam(nextLocation, Direction.UP));
                break;
            case LEFT:
                beams.add(new Beam(nextLocation, Direction.DOWN));
                break;
        }
    }

    private void handleBackwardMirror(Coordinate nextLocation, Direction heading, Set<Beam> beams) {
        switch (heading) {
            case UP:
                beams.add(new Beam(nextLocation, Direction.LEFT));
                break;
            case DOWN:
                beams.add(new Beam(nextLocation, Direction.RIGHT));
                break;
            case RIGHT:
                beams.add(new Beam(nextLocation, Direction.DOWN));
                break;
            case LEFT:
                beams.add(new Beam(nextLocation, Direction.UP));
                break;
        }
    }

    private boolean isCoordinateInBounds(Coordinate coordinate) {
        return coordinate.getX() < width && coordinate.getX() >= 0 && coordinate.getY() < height && coordinate.getY() >= 0;
    }

    @Override
    public Integer solvePartTwo() {
        int max = Integer.MIN_VALUE;
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                if (i == 0 || j == 0 || i == width - 1 || j == height - 1) {
                    Coordinate initCoordinate = new Coordinate(i, j);
                    Direction heading;
                    if (i == 0) {
                        heading = Direction.RIGHT;
                    } else if (i == width - 1) {
                        heading = Direction.LEFT;
                    } else if (j == 0) {
                        heading = Direction.DOWN;
                    } else if (j == height - 1) {
                        heading = Direction.UP;
                    } else {
                        continue;
                    }
                    max = Math.max(max, scanFrom(initCoordinate, heading));
                }
            }
        }
        return max;
    }

    private static class Beam {
        Coordinate coordinate;
        Direction heading;

        Beam(Coordinate coordinate, Direction heading) {
            this.coordinate = coordinate;
            this.heading = heading;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Beam)) return false;
            Beam beam = (Beam) o;
            return Objects.equals(coordinate, beam.coordinate) && heading == beam.heading;
        }

        @Override
        public int hashCode() {
            return Objects.hash(coordinate, heading);
        }
    }

    private enum MirrorType {
        VERTICAL, HORIZONTAL, FORWARD, BACKWARD;

        private static MirrorType getType(char ch) {
            switch (ch) {
                case '|':
                    return VERTICAL;
                case '-':
                    return HORIZONTAL;
                case '/':
                    return FORWARD;
                case '\\':
                    return BACKWARD;
                default:
                    throw new IllegalStateException();
            }
        }
    }
}