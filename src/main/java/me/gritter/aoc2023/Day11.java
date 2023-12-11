package me.gritter.aoc2023;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class Day11 {

    public static void main(String[] args) {
        new Day11().solution("day11-puzzle.txt");
    }

    public void solution(String file) {
        Universe universe = loadUniverse(file);

//        universe.expand(2); // Star 1
        universe.expand(1000000); // Star 2

        long totalDistance = universe.galaxyPairStream()
                .mapToLong(pair -> universe.distanceBetween(pair.getLeft(), pair.getRight()))
                .sum();

        System.out.println(totalDistance);
    }

    private Universe loadUniverse(String file) {
        List<String> lines = Utils.readLines(file).collect(Collectors.toList());

        Collection<Galaxy> galaxies = new HashSet<>();
        for (int y = 0; y < lines.size(); y++) {
            String line = lines.get(y);
            for (int x = 0; x < line.length(); x++) {
                char ch = line.charAt(x);
                if (ch == '#') {
                    Galaxy galaxy = new Galaxy(x, y);
                    galaxies.add(galaxy);
                }
            }
        }

        return new Universe(galaxies);
    }

    public static class Universe {

        private final Collection<Galaxy> galaxies;

        private boolean expanded;

        public Universe(Collection<Galaxy> galaxies, boolean expanded) {
            this.galaxies = new HashSet<>(galaxies);
            this.expanded = expanded;
        }

        public Universe(Collection<Galaxy> galaxies) {
            this(galaxies, false);
        }

        public Stream<Galaxy> galaxyStream() {
            return galaxies.stream();
        }

        public Stream<Pair<Galaxy, Galaxy>> galaxyPairStream() {
            List<Galaxy> galaxyList = new ArrayList<>(galaxies);

            return IntStream.range(0, galaxyList.size() - 1)
                    .boxed()
                    .flatMap(i ->
                            galaxyList.stream()
                                    .skip(i + 1)
                                    .map(g2 -> ImmutablePair.of(galaxyList.get(i), g2))
                    );
        }

        public long distanceBetween(Galaxy a, Galaxy b) {
            return Math.abs(a.x() - b.x()) + Math.abs(a.y() - b.y());
        }

        public void expand(long expansionFactor) {
            if (expanded) {
                throw new IllegalStateException("Expansion is only allowed once.");
            }

            expanded = true;

            Bounds bounds = calculateBounds();

            // Expand rows (y)
            for (long y = bounds.maxY(); y >= bounds.minY(); y--) {
                final long _y = y;
                boolean emptyRow = galaxies.stream().noneMatch(g -> g.y() == _y);
                if (emptyRow) {
                    galaxies.stream()
                            .filter(g -> g.y() > _y)
                            .collect(Collectors.toSet())
                            .forEach(g -> g.y(g.y() + expansionFactor - 1));

                    bounds = calculateBounds();
                }
            }

            // Expand columns (x)
            for (long x = bounds.maxX(); x >= bounds.minX(); x--) {
                final long _x = x;
                boolean emptyColumn = galaxies.stream().noneMatch(g -> g.x() == _x);
                if (emptyColumn) {
                    galaxies.stream()
                            .filter(g -> g.x() > _x)
                            .collect(Collectors.toSet())
                            .forEach(g -> g.x(g.x() + expansionFactor - 1));

                    bounds = calculateBounds();
                }
            }
        }

        public Bounds calculateBounds() {
            long minX = 0, maxX = 0, minY = 0, maxY = 0;
            for (Galaxy galaxy : galaxies) {
                minX = Math.min(minX, galaxy.x());
                maxX = Math.max(maxX, galaxy.x());

                minY = Math.min(minY, galaxy.y());
                maxY = Math.max(maxY, galaxy.y());
            }

            return new Bounds(minX, maxX, minY, maxY);
        }

        @Override
        public String toString() {
            if (expanded) {
                return "[expanded universe]";
            }

            StringBuilder sb = new StringBuilder();

            Bounds bounds = calculateBounds();
            for (long y = bounds.minY(); y <= bounds.maxY(); y++) {
                for (long x = bounds.minX(); x <= bounds.maxX(); x++) {
                    sb.append('.');
                }

                if (y != bounds.maxY()) {
                    sb.append('\n');
                }
            }

            for (Galaxy galaxy : galaxies) {
                long index = galaxy.x() + galaxy.y() * (bounds.maxX() - bounds.minX() + 2); // account for newline
                sb.setCharAt((int) index, '#');
            }

            return sb.toString();
        }
    }

    public static class Galaxy {

        private long x;
        private long y;

        public Galaxy(long x, long y) {
            this.x = x;
            this.y = y;
        }

        public long x() {
            return x;
        }

        public long y() {
            return y;
        }

        public void x(long x) {
            this.x = x;
        }

        public void y(long y) {
            this.y = y;
        }
    }

    public static class Bounds {

        public final long minX;
        public final long maxX;
        public final long minY;
        public final long maxY;

        public Bounds(long minX, long maxX, long minY, long maxY) {
            this.minX = minX;
            this.maxX = maxX;
            this.minY = minY;
            this.maxY = maxY;
        }

        public long minX() {
            return minX;
        }

        public long maxX() {
            return maxX;
        }

        public long minY() {
            return minY;
        }

        public long maxY() {
            return maxY;
        }
    }
}
