package me.gritter.aoc2023;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Day13 implements Solution {

    public static void main(String[] args) {
        Solution solution = new Day13();
        System.out.println(solution.solution_star2("day13-puzzle.txt"));
    }

    @Override
    public long solution_star1(String file) {
        return loadReflectionPatterns(file)
                .map(ReflectionPattern::findReflection)
                .mapToInt(Reflection::calculateValue)
                .sum();
    }

    @Override
    public long solution_star2(String file) {
        return loadReflectionPatterns(file)
                .map(ReflectionPattern::findSmudgedReflection)
                .mapToInt(Reflection::calculateValue)
                .sum();
    }

    private Stream<ReflectionPattern> loadReflectionPatterns(String file) {
        return Stream.of(Utils.readFile(file).split("\n\n"))
                .map(this::parseReflectionPattern);
    }

    private ReflectionPattern parseReflectionPattern(String string) {
        String[] lines = string.split("\n");
        int width = lines[0].length();
        int height = lines.length;

        char[] symbols = new char[width * height];
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                symbols[x + y * width] = lines[y].charAt(x);
            }
        }

        return new ReflectionPattern(symbols, width, height);
    }

    private static class ReflectionPattern {

        private final char[] symbols;
        private final int width;
        private final int height;

        public ReflectionPattern(char[] symbols, int width, int height) {
            if (symbols.length != width * height) {
                throw new IllegalArgumentException();
            }

            for (char symbol : symbols) {
                if (symbol != '.' && symbol != '#') {
                    throw new IllegalArgumentException();
                }
            }

            this.symbols = symbols;
            this.width = width;
            this.height = height;
        }

        public Reflection findSmudgedReflection() {
            Reflection normalReflection = findReflection();

            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    smudgeSymbol(x, y);

                    var reflection = findReflections()
                            .filter(r -> !r.equals(normalReflection))
                            .findAny();

                    smudgeSymbol(x, y); // Un-smudge

                    if (reflection.isPresent()) {
                        return reflection.get();
                    }
                }
            }

            throw new IllegalStateException("No smudged reflection found.");
        }

        public Reflection findReflection() {
            List<Reflection> reflections = findReflections().collect(Collectors.toList());
            if (reflections.isEmpty()) {
                throw new IllegalStateException("Expected 1 reflection, but found none.");
            } else if (reflections.size() > 1) {
                throw new IllegalStateException("Expected 1 reflection, but found multiple.");
            }

            return reflections.get(0);
        }

        private Stream<Reflection> findReflections() {
            return Stream.concat(
                    findVerticalReflection().map(Reflection::verticalReflection),
                    findHorizontalReflection().map(Reflection::horizontalReflection)
            );
        }

        private Stream<Integer> findVerticalReflection() {
            Stream.Builder<Integer> result = Stream.builder();

            for (int x = 1; x < width; x++) {
                boolean matches = true;
                for (int w = 0; w < width; w++) {
                    if (x - w - 1 < 0 || x + w >= width) {
                        // Out of bounds
                        break;
                    }

                    if (!matches(x - w - 1, 0, x + w, 0, 1, height)) {
                        matches = false;
                        break;
                    }
                }

                if (matches) {
                    result.add(x);
                }
            }

            return result.build();
        }

        private Stream<Integer> findHorizontalReflection() {
            Stream.Builder<Integer> result = Stream.builder();

            for (int y = 1; y < height; y++) {
                boolean matches = true;
                for (int h = 0; h < height; h++) {
                    if (y - h - 1 < 0 || y + h >= height) {
                        // Out of bounds
                        break;
                    }

                    if (!matches(0, y - h - 1, 0, y + h, width, 1)) {
                        matches = false;
                        break;
                    }
                }

                if (matches) {
                    result.add(y);
                }
            }

            return result.build();
        }

        private boolean matches(int x1, int y1, int x2, int y2, int width, int height) {
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    if (getSymbol(x1 + x, y1 + y) != getSymbol(x2 + x, y2 + y)) {
                        return false;
                    }
                }
            }

            return true;
        }

        public char getSymbol(int x, int y) {
            return symbols[x + y * width];
        }

        public void setSymbol(int x, int y, char symbol) {
            symbols[x + y * width] = symbol;
        }

        public void smudgeSymbol(int x, int y) {
            setSymbol(x, y, getSymbol(x, y) == '#' ? '.' : '#');
        }

        public int getWidth() {
            return width;
        }

        public int getHeight() {
            return height;
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder(width * height + height - 1);

            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    char symbol = getSymbol(x, y);
                    sb.append(symbol);
                }
                if (y != height - 1) {
                    sb.append('\n');
                }
            }

            return sb.toString();
        }
    }

    private enum ReflectionDirection {

        VERTICAL, HORIZONTAL;
    }

    private static class Reflection {

        private final ReflectionDirection direction;
        private final int coordinate;

        public Reflection(ReflectionDirection direction, int coordinate) {
            this.direction = direction;
            this.coordinate = coordinate;
        }

        public int calculateValue() {
            switch (direction) {
                case VERTICAL:
                    return coordinate;
                case HORIZONTAL:
                    return coordinate * 100;
                default:
                    throw new IllegalStateException();
            }
        }

        public ReflectionDirection getDirection() {
            return direction;
        }

        public int getCoordinate() {
            return coordinate;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Reflection that = (Reflection) o;
            return coordinate == that.coordinate && direction == that.direction;
        }

        @Override
        public int hashCode() {
            return Objects.hash(direction, coordinate);
        }

        @Override
        public String toString() {
            return "Reflection{" +
                    "direction=" + direction +
                    ", coordinate=" + coordinate +
                    '}';
        }

        public static Reflection verticalReflection(int coordinate) {
            return new Reflection(ReflectionDirection.VERTICAL, coordinate);
        }

        public static Reflection horizontalReflection(int coordinate) {
            return new Reflection(ReflectionDirection.HORIZONTAL, coordinate);
        }
    }
}
