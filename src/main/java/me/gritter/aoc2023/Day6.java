package me.gritter.aoc2023;

import org.apache.commons.lang3.StringUtils;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.LongStream;
import java.util.stream.Stream;

import static org.apache.commons.lang3.StringUtils.startsWith;
import static org.apache.commons.lang3.StringUtils.substringAfter;

public class Day6 implements Solution {

    public static void main(String[] args) {
        Solution solution = new Day6();
        System.out.println(solution.solution_star2("day6-puzzle.txt"));
    }

    @Override
    public long solution_star1(String file) {
        return parseRaces_star1(file)
                .map(Race::calculateWinPossibilities)
                .reduce(1L, (a, b) -> a * b);
    }

    @Override
    public long solution_star2(String file) {
        var race = parseRace_star2(file);
        return race.calculateWinPossibilities();
    }

    private Race parseRace_star2(String file) {
        Collection<String> lines = Utils.readLines(file)
                .map(StringUtils::normalizeSpace)
                .collect(Collectors.toList());

        Optional<Long> time = lines.stream()
                .filter(line -> startsWith(line, "Time: "))
                .findFirst()
                .map(line -> substringAfter(line, "Time: "))
                .map(StringUtils::deleteWhitespace)
                .map(Long::parseLong);

        Optional<Long> distance = lines.stream()
                .filter(line -> startsWith(line, "Distance: "))
                .findFirst()
                .map(line -> substringAfter(line, "Distance: "))
                .map(StringUtils::deleteWhitespace)
                .map(Long::parseLong);

        if (time.isEmpty() || distance.isEmpty()) {
            throw new IllegalStateException();
        }

        return new Race(time.get(), distance.get());
    }

    private Stream<Race> parseRaces_star1(String file) {
        Collection<String> lines = Utils.readLines(file)
                .map(StringUtils::normalizeSpace)
                .collect(Collectors.toList());

        List<Long> times = lines.stream()
                .filter(line -> startsWith(line, "Time: "))
                .map(line -> substringAfter(line, "Time: "))
                .flatMap(line -> Stream.of(line.split(" ")))
                .map(Long::parseLong)
                .collect(Collectors.toList());

        List<Long> distances = lines.stream()
                .filter(line -> startsWith(line, "Distance: "))
                .map(line -> substringAfter(line, "Distance: "))
                .flatMap(line -> Stream.of(line.split(" ")))
                .map(Long::parseLong)
                .collect(Collectors.toList());

        if (times.size() != distances.size()) {
            throw new IllegalStateException();
        }

        Stream.Builder<Race> races = Stream.builder();
        for (int i = 0; i < times.size(); i++) {
            long time = times.get(i);
            long distance = distances.get(i);

            races.add(new Race(time, distance));
        }

        return races.build();
    }

    public static class Race {

        public final long time;
        public final long distance;

        public Race(long time, long distance) {
            this.time = time;
            this.distance = distance;
        }

        public long calculateWinPossibilities() {
            return LongStream.range(0, time)
                    .map(timeButtonHeld -> distanceTraveled(time, timeButtonHeld))
                    .filter(d -> d > distance)
                    .count();
        }

        private long distanceTraveled(long totalTime, long timeButtonHeld) {
            return (totalTime - timeButtonHeld) * timeButtonHeld;
        }

        @Override
        public String toString() {
            return "Race{" +
                    "time=" + time +
                    ", distance=" + distance +
                    '}';
        }
    }
}
