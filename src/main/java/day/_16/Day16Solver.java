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
        Set<Beam> visited = new HashSet<>();

        Coordinate initCoordinate = new Coordinate(0, 0);
        Direction heading = Direction.RIGHT;
        MirrorType mirror = mirrors.get(initCoordinate);

        Beam initBeam;
        if (mirror != null) {
            initBeam = getPossibleBeams(mirror, initCoordinate, heading).stream().findFirst().get();
            visited.add(initBeam);
        } else {
            initBeam = new Beam(initCoordinate, heading);
        }

        scanBeamPath(initBeam, visited);
        return visited.stream().map(beam -> beam.coordinate).collect(Collectors.toSet()).size();
    }

    private void scanBeamPath(Beam initBeam, Set<Beam> visited) {
        Deque<Beam> beamDeque = new ArrayDeque<>();
        beamDeque.offer(initBeam);
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
        return null;
    }

    private static class Beam {
        Coordinate coordinate;
        Direction heading;

        Beam(Coordinate coordinate, Direction heading) {
            this.coordinate = coordinate;
            this.heading = heading;
        }

        Beam move(Direction direction) {
            return new Beam(coordinate.moveByDirection(direction), direction);
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