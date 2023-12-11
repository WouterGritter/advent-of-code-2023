package me.gritter.aoc2023;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Objects.requireNonNull;

public class Day3 implements Solution {

    public static void main(String[] args) {
        Solution solution = new Day3();
        System.out.println(solution.solution_star2("day3-puzzle.txt"));
    }

    @Override
    public long solution_star1(String file) {
        var schematic = parseSchematic(file);

        return schematic.findNumbers()
                .filter(range -> range.adjacent().map(schematic::get).flatMap(Optional::stream).anyMatch(this::isSymbol))
                .map(schematic::getStringRepresentation)
                .mapToInt(Integer::parseInt)
                .sum();
    }

    @Override
    public long solution_star2(String file) {
        var schematic = parseSchematic(file);

        return schematic.getBounds()
                .toPointStream()
                .filter(point -> schematic.get(point).map(this::isStar).orElse(false))
                .map(schematic::calculateGearRatio)
                .flatMap(Optional::stream)
                .mapToInt(i -> i)
                .sum();
    }

    private Schematic parseSchematic(String file) {
        int width = Utils.readLines(file)
                .findFirst()
                .map(String::length)
                .orElseThrow();

        List<Character> schematic = new ArrayList<>();
        Utils.readLines(file)
                .flatMapToInt(String::chars)
                .forEach(c -> schematic.add((char) c));

        int height = schematic.size() / width;

        return new Schematic(schematic, width, height);
    }

    private boolean isSymbol(char c) {
        return !Character.isDigit(c) && c != '.';
    }

    private boolean isStar(char c) {
        return c == '*';
    }

    public static class Schematic {

        private final List<Character> schematic;
        private final int width;
        private final int height;
        private final Range bounds;

        public Schematic(List<Character> schematic, int width, int height) {
            if (schematic.size() != width * height) {
                throw new IllegalStateException();
            }

            this.schematic = schematic;
            this.width = width;
            this.height = height;
            this.bounds = new Range(new Point(0, 0), new Point(width - 1, height - 1));
        }

        public Optional<Character> get(Point p) {
            if (!bounds.inRange(p)) {
                return Optional.empty();
            }

            return Optional.of(schematic.get(p.y * width + p.x));
        }

        public Stream<Range> findNumbers() {
            Stream.Builder<Range> builder = Stream.builder();

            for (int y = 0; y < height; y++) {
                Point start = null;
                Point end = null;

                for (int x = 0; x <= width; x++) {
                    Point p = new Point(x, y);
                    char ch = get(p).orElse('.');

                    if (Character.isDigit(ch)) {
                        if (start == null) {
                            start = p;
                        }

                        end = p;
                    } else if (start != null) {
                        builder.add(new Range(start, end));

                        start = null;
                        end = null;
                    }
                }
            }

            return builder.build();
        }

        public String getStringRepresentation(Range range) {
            return range.toPointStream()
                    .map(this::get)
                    .flatMap(Optional::stream)
                    .map(Object::toString)
                    .collect(Collectors.joining());
        }

        public Optional<Integer> calculateGearRatio(Point commonNeighbor) {
            var nums = findNumbers()
                    .filter(r -> r.adjacent().anyMatch(commonNeighbor::equals))
                    .map(this::getStringRepresentation)
                    .map(Integer::parseInt)
                    .collect(Collectors.toList());

            if (nums.size() != 2) {
                return Optional.empty();
            }

            return Optional.of(nums.stream().reduce(1, (a, b) -> a * b));
        }

        public int getWidth() {
            return width;
        }

        public int getHeight() {
            return height;
        }

        public Range getBounds() {
            return bounds;
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder(width * height + height - 1);
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    sb.append(this.get(new Point(x, y)).orElseThrow());
                }

                if (y != height - 1) {
                    sb.append('\n');
                }
            }

            return sb.toString();
        }
    }

    public static class Point {

        private static final Collection<Point> ADJACENT_DELTAS = Set.of(
                new Point(-1, -1),
                new Point(0, -1),
                new Point(1, -1),
                new Point(1, 0),
                new Point(1, 1),
                new Point(0, 1),
                new Point(-1, 1),
                new Point(-1, 0)
        );

        public final int x, y;

        public Point(int x, int y) {
            this.x = x;
            this.y = y;
        }

        public Point add(Point other) {
            return new Point(this.x + other.x, this.y + other.y);
        }

        public Stream<Point> adjacent() {
            return ADJACENT_DELTAS.stream()
                    .map(this::add);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Point point = (Point) o;
            return x == point.x && y == point.y;
        }

        @Override
        public int hashCode() {
            return Objects.hash(x, y);
        }

        @Override
        public String toString() {
            return "Point{" +
                    "x=" + x +
                    ", y=" + y +
                    '}';
        }
    }

    public static class Range {

        public final Point min;
        public final Point max;

        public Range(Point min, Point max) {
            this.min = requireNonNull(min);
            this.max = requireNonNull(max);
        }

        public boolean inRange(Point point) {
            return point.x >= min.x &&
                    point.y >= min.y &&
                    point.x <= max.x &&
                    point.y <= max.y;
        }

        public boolean notInRange(Point point) {
            return !inRange(point);
        }

        public Stream<Point> toPointStream() {
            Stream.Builder<Point> builder = Stream.builder();

            for (int y = min.y; y <= max.y; y++) {
                for (int x = min.x; x <= max.x; x++) {
                    builder.add(new Point(x, y));
                }
            }

            return builder.build();
        }

        public Stream<Point> adjacent() { // TODO: Optimize
            return toPointStream()
                    .flatMap(Point::adjacent)
                    .filter(this::notInRange)
                    .distinct();
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Range range = (Range) o;
            return Objects.equals(min, range.min) && Objects.equals(max, range.max);
        }

        @Override
        public int hashCode() {
            return Objects.hash(min, max);
        }

        @Override
        public String toString() {
            return "Range{" +
                    "min=" + min +
                    ", max=" + max +
                    '}';
        }
    }
}
