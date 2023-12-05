package me.gritter.aoc2023;

import java.util.Map;
import java.util.Optional;
import java.util.function.BiFunction;

public class Day1 {

    private static final Map<String, Integer> NUMBERS = Map.of(
            "one", 1,
            "two", 2,
            "three", 3,
            "four", 4,
            "five", 5,
            "six", 6,
            "seven", 7,
            "eight", 8,
            "nine", 9
    );

    public static void main(String[] args) {
//        new Day1().solution("day1-sample.txt");
        new Day1().solution("day1-puzzle.txt");
    }

    public void solution(String file) {
        int sum = Utils.readLines(file)
//                .mapToInt(line -> extractNumber(line, this::extractNumberByChar)) // Star 1
                .mapToInt(line -> extractNumber(line, this::extractNumberByCharOrName)) // Star 2
                .sum();

        System.out.println(sum);
    }

    private int extractNumber(String input, BiFunction<String, Integer, Optional<Integer>> charMapFunction) {
        int first = -1;
        int last = -1;

        for (int i = 0; i < input.length(); i++) {
            Optional<Integer> digit = charMapFunction.apply(input, i);
            if (digit.isPresent()) {
                if (first == -1) {
                    first = digit.get();
                }

                last = digit.get();
            }
        }

        if (first == -1 || last == -1) {
            throw new IllegalStateException();
        }

        return first * 10 + last;
    }

    private Optional<Integer> extractNumberByName(String input, int offset) {
        return NUMBERS.entrySet()
                .stream()
                .filter(entry -> input.startsWith(entry.getKey(), offset))
                .map(Map.Entry::getValue)
                .findFirst();
    }

    private Optional<Integer> extractNumberByChar(String input, int offset) {
        char ch = input.charAt(offset);
        if (!Character.isDigit(ch)) {
            return Optional.empty();
        }

        return Optional.of(ch - '0');
    }

    private Optional<Integer> extractNumberByCharOrName(String input, int offset) {
        return extractNumberByChar(input, offset)
                .or(() -> extractNumberByName(input, offset));
    }
}
