package me.gritter.aoc2023;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.apache.commons.lang3.StringUtils.*;

public class Day12 implements Solution {

    public static void main(String[] args) {
        Solution solution = new Day12();
        System.out.println(solution.solution_star2("day12-puzzle.txt"));
    }

    @Override
    public long solution_star1(String file) {
        return Utils.readLines(file)
                .mapToLong(this::calculateRearrangementsFromLine)
                .sum();
    }

    @Override
    public long solution_star2(String file) {
        return Utils.readLines(file)
                .map(this::unfoldLine)
                .mapToLong(this::calculateRearrangementsFromLine)
                .sum();
    }

    private String unfoldLine(String line) {
        String gears = substringBefore(line, " ");
        String contiguouslyDamaged = substringAfter(line, " ");

        return repeat(gears, "?", 5) + " " + repeat(contiguouslyDamaged, ",", 5);
    }

    private List<Integer> parseContiguouslyDamaged(String line) {
        return Stream.of(
                        substringAfter(line, " ").split(",")
                )
                .map(Integer::parseInt)
                .collect(Collectors.toUnmodifiableList());
    }

    private List<Gear> parseGears(String line) {
        return substringBefore(line, " ")
                .chars()
                .mapToObj(c -> (char) c)
                .map(Gear::bySymbol)
                .collect(Collectors.toUnmodifiableList());
    }

    private long calculateRearrangementsFromLine(String line) {
        List<Gear> gears = parseGears(line);
        List<Integer> contiguouslyDamaged = parseContiguouslyDamaged(line);
        return calculateRearrangements(gears, contiguouslyDamaged, 0, new HashMap<>());
    }

    private long calculateRearrangements(List<Gear> gears, List<Integer> brokenGroups, int currentBroken, Map<CacheKey, Long> cache) {
        CacheKey key = new CacheKey(gears, brokenGroups, currentBroken);
        Long result = cache.get(key);
        if (result == null) {
            result = calculateRearrangements_worker(gears, brokenGroups, currentBroken, cache);
            cache.put(key, result);
        }

        return result;
    }

    private long calculateRearrangements_worker(List<Gear> gears, List<Integer> brokenGroups, int currentBroken, Map<CacheKey, Long> cache) {
        if (gears.isEmpty()) {
            if (brokenGroups.size() > 1) {
                // There are more broken groups left.
                return 0;
            } else if (brokenGroups.size() == 1 && currentBroken != brokenGroups.get(0)) {
                // The last broken group size doesn't match the amount of current contiguously broken gears.
                return 0;
            } else {
                // All previous recursive checks succeeded. Assume this branch was valid.
                return 1;
            }
        }

        Gear gear = gears.get(0);

        if (brokenGroups.isEmpty()) {
            if (gear == Gear.BROKEN) {
                // We're not expecting broken gears anymore, but found one.
                return 0;
            } else {
                // Continue as normal.
                return calculateRearrangements(gears.subList(1, gears.size()), brokenGroups, currentBroken, cache);
            }
        }

        if (gear == Gear.UNKNOWN) {
            long total = 0;

            for (Gear replacement : List.of(Gear.WORKING, Gear.BROKEN)) {
                List<Gear> branchGears = new ArrayList<>(gears);
                branchGears.set(0, replacement);
                total += calculateRearrangements(branchGears, brokenGroups, currentBroken, cache);
            }

            // Return sum of two branches.
            return total;
        }

        int brokenExpected = brokenGroups.get(0);

        if (gear == Gear.BROKEN) {
            currentBroken++;
            if (currentBroken > brokenExpected) {
                // Found more broken than expected.
                return 0;
            }
        } else if (gear == Gear.WORKING) {
            if (currentBroken > 0) {
                // Group of contiguously broken gears has ended
                if (currentBroken != brokenExpected) {
                    // Size doesn't match what we expected.
                    return 0;
                }

                // Reset running variables for next recursive call.
                brokenGroups = brokenGroups.subList(1, brokenGroups.size());
                currentBroken = 0;
            }
        }

        // Continue as normal.
        return calculateRearrangements(gears.subList(1, gears.size()), brokenGroups, currentBroken, cache);
    }

    private enum Gear {
        BROKEN('#'),
        WORKING('.'),
        UNKNOWN('?');

        private final char symbol;

        Gear(char symbol) {
            this.symbol = symbol;
        }

        public char symbol() {
            return symbol;
        }

        public static Gear bySymbol(char symbol) {
            for (Gear gear : values()) {
                if (gear.symbol() == symbol) {
                    return gear;
                }
            }

            throw new IllegalArgumentException();
        }
    }

    private static class CacheKey {

        private final List<Gear> gears;
        private final List<Integer> brokenGroups;
        private final int currentBroken;

        public CacheKey(List<Gear> gears, List<Integer> brokenGroups, int currentBroken) {
            this.gears = gears;
            this.brokenGroups = brokenGroups;
            this.currentBroken = currentBroken;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            CacheKey cacheKey = (CacheKey) o;
            return currentBroken == cacheKey.currentBroken && Objects.equals(gears, cacheKey.gears) && Objects.equals(brokenGroups, cacheKey.brokenGroups);
        }

        @Override
        public int hashCode() {
            return Objects.hash(gears, brokenGroups, currentBroken);
        }
    }
}
