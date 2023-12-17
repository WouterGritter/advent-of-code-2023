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

    private String parseGears(String line) {
        return substringBefore(line, " ");
    }

    private long calculateRearrangementsFromLine(String line) {
        String gears = parseGears(line);
        List<Integer> contiguouslyDamaged = parseContiguouslyDamaged(line);
        return calculateRearrangements(new StringBuilder(gears), contiguouslyDamaged, 0, 0, 0, new HashMap<>());
    }

    private long calculateRearrangements(StringBuilder gears, List<Integer> brokenGroups, int index, int brokenGroupIndex, int currentBroken, Map<CacheKey, Long> cache) {
        CacheKey key = null;
        if (index < gears.length() - 1 && brokenGroupIndex < brokenGroups.size()) {
            key = new CacheKey(gears, brokenGroups, index, brokenGroupIndex, currentBroken);
            Long result = cache.get(key);
            if (result != null) {
                return result;
            }
        }

        long result = calculateRearrangements_worker(gears, brokenGroups, index, brokenGroupIndex, currentBroken, cache);
        if (key != null) {
            cache.put(key, result);
        }

        return result;
    }

    private long calculateRearrangements_worker(StringBuilder gears, List<Integer> brokenGroups, int index, int brokenGroupIndex, int currentBroken, Map<CacheKey, Long> cache) {
        if (index >= gears.length()) {
            if (brokenGroupIndex < brokenGroups.size() - 1) {
                // There are more broken groups left.
                return 0;
            } else if (brokenGroupIndex == brokenGroups.size() - 1) {
                int brokenExpected = brokenGroups.get(brokenGroupIndex);
                if (brokenExpected != currentBroken) {
                    // The last broken group size doesn't match the amount of current contiguously broken gears.
                    return 0;
                }
            }

            // All previous recursive checks succeeded. Assume this branch was valid.
            return 1;
        }

        char c = gears.charAt(index);

        if (brokenGroupIndex >= brokenGroups.size()) {
            if (c == '#') {
                // We're not expecting broken gears anymore, but found one.
                return 0;
            } else {
                // Continue as normal.
                return calculateRearrangements(gears, brokenGroups, index + 1, brokenGroupIndex, currentBroken, cache);
            }
        }

        if (c == '?') {
            long total = 0;

            for (char replacement : List.of('.', '#')) {
                gears.setCharAt(index, replacement);
                total += calculateRearrangements(gears, brokenGroups, index, brokenGroupIndex, currentBroken, cache);
                gears.setCharAt(index, '?');
            }

            // Return sum of two branches.
            return total;
        }

        int brokenExpected = brokenGroups.get(brokenGroupIndex);

        if (c == '#') {
            currentBroken++;
            if (currentBroken > brokenExpected) {
                // Found more broken than expected.
                return 0;
            }
        } else if (c == '.') {
            if (currentBroken > 0) {
                // Group of contiguously broken gears has ended
                if (currentBroken != brokenExpected) {
                    // Size doesn't match what we expected.
                    return 0;
                }

                // Reset running variables for next recursive call.
                brokenGroupIndex++;
                currentBroken = 0;
            }
        }

        // Continue as normal.
        return calculateRearrangements(gears, brokenGroups, index + 1, brokenGroupIndex, currentBroken, cache);
    }

    private static class CacheKey {

        private final String gearsLeft;
        private final List<Integer> brokenGroupsLeft;
        private final int currentBroken;

        public CacheKey(StringBuilder gears, List<Integer> brokenGroups, int index, int brokenGroupIndex, int currentBroken) {
            this.gearsLeft = gears.substring(index, gears.length());
            this.brokenGroupsLeft = new ArrayList<>(brokenGroups.subList(brokenGroupIndex, brokenGroups.size()));
            this.currentBroken = currentBroken;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            CacheKey cacheKey = (CacheKey) o;
            return currentBroken == cacheKey.currentBroken && Objects.equals(gearsLeft, cacheKey.gearsLeft) && Objects.equals(brokenGroupsLeft, cacheKey.brokenGroupsLeft);
        }

        @Override
        public int hashCode() {
            return Objects.hash(gearsLeft, brokenGroupsLeft, currentBroken);
        }
    }
}
