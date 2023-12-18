package me.gritter.aoc2023;

import java.util.*;
import java.util.stream.Collectors;

public class Day16 implements Solution {

    public static void main(String[] args) {
        Solution solution = new Day16();
        System.out.println(solution.solution_star2("day16-puzzle.txt"));
    }

    @Override
    public long solution_star1(String file) {
        var mirrors = loadMirrors(file);
        var contraption = new Contraption(mirrors);

        contraption.addBeam(new LightBeam(0, 0, 1, 0));
        contraption.updateUntilSaturation();

        return contraption.countEnergizedTiles();
    }

    @Override
    public long solution_star2(String file) {
        var mirrors = loadMirrors(file);

        return generatePossibleStartingBeams(mirrors.getWidth(), mirrors.getHeight())
                .stream()
                .map(beam -> updateContraptionUntilSaturation(mirrors, beam))
                .mapToLong(Contraption::countEnergizedTiles)
                .max()
                .orElseThrow();
    }

    private Contraption updateContraptionUntilSaturation(Mirrors mirrors, LightBeam startingBeam) {
        var contraption = new Contraption(mirrors);
        contraption.addBeam(startingBeam);
        contraption.updateUntilSaturation();

        return contraption;
    }

    private Collection<LightBeam> generatePossibleStartingBeams(int width, int height) {
        Collection<LightBeam> startingBeams = new HashSet<>();

        for (int x = 0; x < width; x++) {
            // Top row
            startingBeams.add(new LightBeam(x, 0, 0, 1));

            // Bottom row
            startingBeams.add(new LightBeam(x, height - 1, 0, -1));
        }

        for (int y = 0; y < height; y++) {
            // Left column
            startingBeams.add(new LightBeam(0, y, 1, 0));

            // Right column
            startingBeams.add(new LightBeam(width - 1, y, -1, 0));
        }

        return startingBeams;
    }

    private Mirrors loadMirrors(String file) {
        List<String> lines = Utils.readLines(file).collect(Collectors.toList());

        int width = lines.get(0).length();
        int height = lines.size();
        char[] symbols = new char[width * height];

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                char symbol = lines.get(y).charAt(x);
                symbols[x + y * width] = symbol;
            }
        }

        return new Mirrors(symbols, width, height);
    }

    private static class Mirrors {

        private final char[] symbols;
        private final int width;
        private final int height;

        public Mirrors(char[] symbols, int width, int height) {
            if (symbols.length != width * height) {
                throw new IllegalArgumentException();
            }

            this.symbols = symbols;
            this.width = width;
            this.height = height;
        }

        public boolean isOutOfBounds(Point point) {
            return point.getX() < 0 || point.getX() >= width || point.getY() < 0 || point.getY() >= height;
        }

        public char getSymbol(int x, int y) {
            return symbols[x + y * width];
        }

        public int getWidth() {
            return width;
        }

        public int getHeight() {
            return height;
        }
    }

    private static class Contraption {

        private final Mirrors mirrors;
        private final Collection<LightBeam> beams = new HashSet<>();
        private final Collection<LightBeam> uniqueBeamStates = new HashSet<>();

        public Contraption(Mirrors mirrors) {
            this.mirrors = mirrors;
        }

        public void addBeam(LightBeam beam) {
            if (!uniqueBeamStates.contains(beam)) {
                beams.add(beam);
                uniqueBeamStates.add(beam.copy());
            }
        }

        public void updateUntilSaturation() {
            boolean didUpdate;
            do {
                didUpdate = update();
            } while (didUpdate);
        }

        public boolean update() {
            if (beams.isEmpty()) {
                return false;
            }

            Collection<LightBeam> splitBeams = new HashSet<>();

            // Updating beams can add beams to the collection. Make a copy before updating.
            new ArrayList<>(beams).forEach(beam -> splitBeams.addAll(beam.update(mirrors)));

            beams.removeIf(beam -> mirrors.isOutOfBounds(beam.toPoint()));
            beams.removeIf(uniqueBeamStates::contains);
            beams.stream()
                    .map(LightBeam::copy)
                    .forEach(uniqueBeamStates::add);

            splitBeams.stream()
                    .filter(beam -> !mirrors.isOutOfBounds(beam.toPoint()))
                    .filter(beam -> !uniqueBeamStates.contains(beam))
                    .forEach(beam -> {
                        beams.add(beam);
                        uniqueBeamStates.add(beam.copy());
                    });

            return true;
        }

        public long countEnergizedTiles() {
            return uniqueBeamStates.stream()
                    .map(LightBeam::toPoint)
                    .distinct()
                    .count();
        }

        public Mirrors getMirrors() {
            return mirrors;
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder(mirrors.getWidth() * mirrors.getHeight() + mirrors.getHeight() - 1);

            for (int y = 0; y < mirrors.getHeight(); y++) {
                for (int x = 0; x < mirrors.getWidth(); x++) {
                    sb.append(mirrors.getSymbol(x, y));
                }

                if (y != mirrors.getHeight() - 1) {
                    sb.append('\n');
                }
            }

            for (LightBeam beam : uniqueBeamStates) {
                sb.setCharAt(beam.getX() + beam.getY() * (mirrors.getWidth() + 1), '#'); // width + 1: account for newline
            }

            return sb.toString();
        }
    }

    private static class LightBeam {

        private int x;
        private int y;
        private int dx;
        private int dy;

        public LightBeam(int x, int y, int dx, int dy) {
            if (Math.abs(dx) + Math.abs(dy) != 1) {
                throw new IllegalArgumentException();
            }

            this.x = x;
            this.y = y;
            this.dx = dx;
            this.dy = dy;
        }

        public LightBeam copy() {
            return new LightBeam(x, y, dx, dy);
        }

        public Collection<LightBeam> update(Mirrors mirrors) {
            Collection<LightBeam> splitBeams = new HashSet<>();

            char current = mirrors.getSymbol(x, y);
            if (current == '\\') {
                // Rotate dx/dy accordingly
                int temp = dx;
                dx = dy;
                dy = temp;
            } else if (current == '/') {
                // Rotate dx/dy accordingly
                int temp = dx;
                dx = -dy;
                dy = -temp;
            } else if ((current == '|' && Math.abs(dx) == 1) || (current == '-' && Math.abs(dy) == 1)) {
                // Split
                splitBeams.add(new LightBeam(x + dy, y + dx, dy, dx));

                // Rotate dx/dy in the opposite way of new beam
                int temp = dx;
                dx = -dy;
                dy = -temp;
            }

            x += dx;
            y += dy;

            return splitBeams;
        }

        public Point toPoint() {
            return new Point(x, y);
        }

        public int getX() {
            return x;
        }

        public int getY() {
            return y;
        }

        public int getDx() {
            return dx;
        }

        public int getDy() {
            return dy;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            LightBeam lightBeam = (LightBeam) o;
            return x == lightBeam.x && y == lightBeam.y && dx == lightBeam.dx && dy == lightBeam.dy;
        }

        @Override
        public int hashCode() {
            return Objects.hash(x, y, dx, dy);
        }
    }

    private static class Point {

        private final int x;
        private final int y;

        public Point(int x, int y) {
            this.x = x;
            this.y = y;
        }

        public int getX() {
            return x;
        }

        public int getY() {
            return y;
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
    }
}
