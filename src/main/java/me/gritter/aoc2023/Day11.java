package me.gritter.aoc2023;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.math.BigInteger;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static java.math.BigInteger.ONE;
import static java.math.BigInteger.ZERO;

public class Day11 implements Solution {

    public static void main(String[] args) {
        Solution solution = new Day11();
        System.out.println(solution.solution_star2("day11-puzzle.txt"));
    }

    @Override
    public long solution_star1(String file) {
        return solution(file, 2);
    }

    @Override
    public long solution_star2(String file) {
        return solution(file, 1000000);
    }

    public long solution(String file, long expansionFactor) {
        Universe universe = loadUniverse(file);

        universe.expand(BigInteger.valueOf(expansionFactor));

        var sum = universe.galaxyPairStream()
                .map(pair -> universe.distanceBetween(pair.getLeft(), pair.getRight()))
                .reduce(ZERO, BigInteger::add);

        return sum.longValueExact();
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

        public boolean isExpanded() {
            return expanded;
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
                                    .map(g -> ImmutablePair.of(galaxyList.get(i), g))
                    );
        }

        public BigInteger distanceBetween(Galaxy a, Galaxy b) {
            var xDelta = a.getX().subtract(b.getX()).abs();
            var yDelta = a.getY().subtract(b.getY()).abs();

            return xDelta.add(yDelta);
        }

        public void expand(BigInteger expansionFactor) {
            expanded = true;

            expandDirection(expansionFactor, Galaxy::getX, Galaxy::addX);
            expandDirection(expansionFactor, Galaxy::getY, Galaxy::addY);
        }

        private void expandDirection(BigInteger expansionFactor, Function<Galaxy, BigInteger> coordinateGetter, BiConsumer<Galaxy, BigInteger> coordinateAdder) {
            Optional<BigInteger> current = galaxies.stream()
                    .map(coordinateGetter)
                    .max(BigInteger::compareTo);

            while (current.isPresent()) {
                BigInteger _current = current.get();
                Optional<BigInteger> next = galaxies.stream()
                        .map(coordinateGetter)
                        .filter(coordinate -> coordinate.compareTo(_current) < 0)
                        .max(BigInteger::compareTo);

                if (next.isPresent()) {
                    BigInteger emptySpace = current.get().subtract(next.get()).subtract(ONE);
                    if (emptySpace.compareTo(ZERO) > 0) {
                        BigInteger addition = emptySpace.multiply(expansionFactor.subtract(ONE));
                        galaxies.stream()
                                .filter(g -> coordinateGetter.apply(g).compareTo(_current) >= 0)
                                .forEach(g -> coordinateAdder.accept(g, addition));
                    }
                }

                current = next;
            }
        }

        @Override
        public String toString() {
            if (expanded) {
                return "[expanded universe]";
            }

            StringBuilder sb = new StringBuilder();

            int minX = 0, maxX = 0, minY = 0, maxY = 0;
            for (Galaxy galaxy : galaxies) {
                minX = Math.min(minX, galaxy.getX().intValue());
                maxX = Math.max(maxX, galaxy.getX().intValue());

                minY = Math.min(minY, galaxy.getY().intValue());
                maxY = Math.max(maxY, galaxy.getY().intValue());
            }

            for (int y = minY; y <= maxY; y++) {
                for (int x = minX; x <= maxX; x++) {
                    sb.append('.');
                }

                if (y != maxY) {
                    sb.append('\n');
                }
            }

            for (Galaxy galaxy : galaxies) {
                int x = galaxy.getX().intValue();
                int y = galaxy.getY().intValue();
                int index = x + y * (maxX - minX + 2); // account for newline
                sb.setCharAt(index, '#');
            }

            return sb.toString();
        }
    }

    public static class Galaxy {

        private BigInteger x;
        private BigInteger y;

        public Galaxy(BigInteger x, BigInteger y) {
            this.x = x;
            this.y = y;
        }

        public Galaxy(long x, long y) {
            this(BigInteger.valueOf(x), BigInteger.valueOf(y));
        }

        public BigInteger getX() {
            return x;
        }

        public BigInteger getY() {
            return y;
        }

        public void setX(BigInteger x) {
            this.x = x;
        }

        public void setY(BigInteger y) {
            this.y = y;
        }

        public void addX(BigInteger delta) {
            x = x.add(delta);
        }

        public void addY(BigInteger delta) {
            y = y.add(delta);
        }
    }
}
