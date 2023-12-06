package me.gritter.aoc2023;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.LongStream;
import java.util.stream.Stream;

import static java.lang.Long.parseLong;
import static org.apache.commons.lang3.StringUtils.*;

/**
 * Code execution time for star 2.
 *
 * Virtualized Intel Xeon E3-1230 v6 (8 cores):
 *   Single threaded (Day5#solution):               359 seconds
 *   Multi threaded (Day5#solution_multithreading): 157 seconds
 *
 * Virtualized Intel Xeon Gold 6140 (8 cores):
 *   Single threaded (Day5#solution):               403 seconds
 *   Multi threaded (Day5#solution_multithreading): 127 seconds
 */
public class Day5 {

    public static void main(String[] args) {
//        new Day5().solution("day5-puzzle.txt");
        new Day5().solution_multithreading("day5-puzzle.txt");
    }

    public void solution(String file) {
        var almanac = parseGardenAlmanac(file);

        long minLocation = Utils.readLines(file)
                .filter(line -> line.startsWith("seeds: "))
                .map(line -> substringAfter(line, "seeds: "))
//                .flatMapToLong(this::parseSeeds_star1) // Star 1
                .flatMapToLong(this::parseSeeds_star2) // Star 2
                .map(almanac::mapThroughAllCategories)
                .min()
                .orElseThrow();

        System.out.println(minLocation);
    }

    // Star 2 (with added multithreading)
    public void solution_multithreading(String file) {
        var almanac = parseGardenAlmanac(file);

        Collection<String> seedPairs = Utils.readLines(file)
                .filter(line -> line.startsWith("seeds: "))
                .map(line -> substringAfter(line, "seeds: "))
                .flatMap(line -> patternMatchStream(line, Pattern.compile("\\d+ \\d+")))
                .collect(Collectors.toList());

        final AtomicLong min = new AtomicLong(Long.MAX_VALUE);
        final AtomicLong runningThreads = new AtomicLong(seedPairs.size());

        for (String seedPair : seedPairs) {
            String[] parts = seedPair.split(" ");
            if (parts.length != 2) {
                throw new IllegalStateException();
            }

            long start = parseLong(parts[0]);
            long length = parseLong(parts[1]);
            long end = start + length - 1;

            new Thread(() -> {
                long localMinimum = LongStream.range(start, end)
                        .map(almanac::mapThroughAllCategories)
                        .min()
                        .orElseThrow();

                synchronized (min) {
                    if (localMinimum < min.get()) {
                        min.set(localMinimum);
                    }
                }

                synchronized (runningThreads) {
                    runningThreads.decrementAndGet();
                }
            }).start();
        }

        while (runningThreads.get() > 0) {
            System.out.println("runningThreads = " + runningThreads.get() + ", minimum = " + min.get());

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        System.out.println("All threads exited.");
        System.out.println("minimum = " + min.get());
    }

    private GardenAlmanac parseGardenAlmanac(String file) {
        GardenAlmanac almanac = new GardenAlmanac();
        GardenMapping currentMapping = null;

        for (String line : Utils.readLines(file).collect(Collectors.toList())) {
            if (line.endsWith(" map:")) {
                if (currentMapping != null) {
                    almanac.addMapping(currentMapping);
                }

                Category src = Category.valueOf(substringBefore(line, "-to-").toUpperCase());
                Category dst = Category.valueOf(substringBetween(line, "-to-", " map:").toUpperCase());

                currentMapping = new GardenMapping(src, dst);
            } else if (currentMapping != null) {
                String[] parts = line.split(" ");
                if (parts.length == 3) {
                    long dstStart = parseLong(parts[0]);
                    long srcStart = parseLong(parts[1]);
                    long range = parseLong(parts[2]);

                    currentMapping.addRangeMapping(dstStart, srcStart, range);
                }
            }
        }

        if (currentMapping != null) {
            almanac.addMapping(currentMapping);
        }

        return almanac;
    }

    private LongStream parseSeeds_star1(String line) {
        return Stream.of(line.split(" "))
                .mapToLong(Long::parseLong);
    }

    private LongStream parseSeeds_star2(String line) {
        return patternMatchStream(line, Pattern.compile("\\d+ \\d+"))
                .flatMapToLong(this::parseSeedsFromPair_star2);
    }

    private LongStream parseSeedsFromPair_star2(String pair) {
        String[] parts = pair.split(" ");
        if (parts.length != 2) {
            throw new IllegalStateException();
        }

        long start = parseLong(parts[0]);
        long length = parseLong(parts[1]);
        long end = start + length - 1;

        return LongStream.range(start, end);
    }

    private Stream<String> patternMatchStream(String input, Pattern pattern) {
        Stream.Builder<String> builder = Stream.builder();

        Matcher matcher = pattern.matcher(input);
        while (matcher.find()) {
            builder.add(matcher.group());
        }

        return builder.build();
    }

    public enum Category {
        SEED(0), SOIL(1), FERTILIZER(2), WATER(3), LIGHT(4), TEMPERATURE(5), HUMIDITY(6), LOCATION(7);

        private final int ordering;

        Category(int ordering) {
            this.ordering = ordering;
        }

        public int ordering() {
            return ordering;
        }
    }

    public static class GardenAlmanac {

        private final List<GardenMapping> mappings = new ArrayList<>();

        public GardenAlmanac() {
        }

        public void addMapping(GardenMapping mapping) {
            mappings.add(mapping);
            mappings.sort(Comparator.comparing(m -> m.getSourceCategory().ordering()));
        }

        public long mapThroughAllCategories(long number) {
            for (GardenMapping mapping : mappings) {
                number = mapping.map(number);
            }

            return number;
        }
    }

    public static class GardenMapping {

        private final Category sourceCategory;
        private final Category destinationCategory;

        private final TreeMap<Long, Long> sourceDeltaMap = new TreeMap<>(Long::compare); // <source, delta>

        public GardenMapping(Category sourceCategory, Category destinationCategory) {
            if (destinationCategory.ordering() - sourceCategory.ordering() != 1) {
                throw new IllegalArgumentException();
            }

            this.sourceCategory = sourceCategory;
            this.destinationCategory = destinationCategory;
        }

        public void addRangeMapping(long destinationStart, long sourceStart, long range) {
            sourceDeltaMap.put(sourceStart, destinationStart - sourceStart);

            if (!sourceDeltaMap.containsKey(sourceStart + range)) {
                sourceDeltaMap.put(sourceStart + range, null);
            }
        }

        public long map(long source) {
            long delta = Optional.ofNullable(sourceDeltaMap.floorEntry(source))
                    .map(Map.Entry::getValue)
                    .orElse(0L);

            return source + delta;
        }

        public Category getSourceCategory() {
            return sourceCategory;
        }

        public Category getDestinationCategory() {
            return destinationCategory;
        }
    }
}
