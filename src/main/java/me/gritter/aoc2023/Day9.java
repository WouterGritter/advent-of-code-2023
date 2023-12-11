package me.gritter.aoc2023;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * As part of an extra exercise, I have attempted to implement the Sequence#toString method to
 * match the String representations found on <a href="https://adventofcode.com/2023/day/9">Advent of Code</a>.
 * <p>
 * The toString method uses a StringBuilder and does not re-generate or delete characters; it only adds to
 * the buffer (which made it a bit harder). The tests found in the Day9Test class confirm the
 * Sequence#toString method generates strings equivalent to those found on the Advent of Code page.
 */
public class Day9 implements Solution {

    public static void main(String[] args) {
        Solution solution = new Day9();
        System.out.println(solution.solution_star2("day9-puzzle.txt"));
    }

    @Override
    public long solution_star1(String file) {
        return Utils.readLines(file)
                .map(this::parseSequence)
                .mapToInt(Sequence::predictNext)
                .sum();
    }

    @Override
    public long solution_star2(String file) {
        return Utils.readLines(file)
                .map(this::parseSequence)
                .mapToInt(Sequence::predictPrevious)
                .sum();
    }

    private Sequence parseSequence(String line) {
        List<Integer> numbers = Stream.of(line.split(" "))
                .map(Integer::parseInt)
                .collect(Collectors.toList());

        return new Sequence(numbers);
    }

    public static class Sequence {

        private final List<Integer> numbers;

        public Sequence(List<Integer> numbers) {
            if (numbers.stream().anyMatch(Objects::isNull)) {
                throw new NullPointerException();
            }

            this.numbers = numbers;
        }

        public Sequence(Integer... numbers) {
            this(List.of(numbers));
        }

        public List<Integer> getNumbers() {
            return Collections.unmodifiableList(numbers);
        }

        public int size() {
            return numbers.size();
        }

        public int getLast() {
            if (numbers.isEmpty()) {
                throw new IllegalStateException();
            }

            return numbers.get(numbers.size() - 1);
        }

        public int getFirst() {
            if (numbers.isEmpty()) {
                throw new IllegalStateException();
            }

            return numbers.get(0);
        }

        public Sequence grow() {
            return grow(1);
        }

        public Sequence grow(int steps) {
            if (steps == 0) {
                return this;
            }

            int previous = predictPrevious();
            int next = predictNext();

            List<Integer> before = new ArrayList<>();
            before.add(previous);
            before.addAll(numbers);
            before.add(next);

            return new Sequence(before).grow(steps - 1);
        }

        public int predictNext() {
            if (numbers.isEmpty() || isZeros()) {
                return 0;
            }

            Sequence delta = calculateDeltas();
            return getLast() + delta.predictNext();
        }

        public int predictPrevious() {
            if (numbers.isEmpty() || isZeros()) {
                return 0;
            }

            Sequence delta = calculateDeltas();
            return getFirst() - delta.predictPrevious();
        }

        public Sequence calculateDeltas() {
            List<Integer> delta = new ArrayList<>();

            for (int i = 0; i < numbers.size() - 1; i++) {
                int a = numbers.get(i);
                int b = numbers.get(i + 1);

                delta.add(b - a);
            }

            return new Sequence(delta);
        }

        public boolean isZeros() {
            return numbers.stream().allMatch(n -> n == 0);
        }

        @Override
        public String toString() {
            if (numbers.isEmpty()) {
                return "";
            }

            int longestNumber = findLongestNumberRecursively();
            int charsPerNumber = longestNumber % 2 == 1 ? longestNumber : longestNumber + 1;
            int omitLeftPaddingNumber = padCalculateLeftPadding(String.valueOf(numbers.get(0)), charsPerNumber);

            StringBuilder builder = new StringBuilder();
            buildToStringRecursively(builder, charsPerNumber, omitLeftPaddingNumber, 0);
            return builder.toString();
        }

        private int findLongestNumberRecursively() {
            if (numbers.isEmpty()) {
                return 0;
            }

            int longest = 0;
            for (int n : numbers) {
                String s = String.valueOf(n);
                longest = Math.max(longest, s.length());
            }

            return Math.max(longest, calculateDeltas().findLongestNumberRecursively());
        }

        private void buildToStringRecursively(StringBuilder builder, int charsPerNumber, int omitLeftPaddingNumber, int paddingLeft) {
            int padding = paddingLeft * (charsPerNumber / 2 + 1) - omitLeftPaddingNumber;
            for (int i = 0; i < padding; i++) {
                builder.append(' ');
            }

            for (int i = 0; i < numbers.size(); i++) {
                boolean isFirst = i == 0;
                boolean isLast = i == numbers.size() - 1;

                String s = String.valueOf(numbers.get(i));
                appendPadded(builder, s, charsPerNumber, isFirst ? -padding : 0, isLast ? Integer.MAX_VALUE : 0);

                if (!isLast) {
                    builder.append(' ');
                }
            }

            if (!isZeros()) {
                Sequence deltas = calculateDeltas();
                if (deltas.size() > 0) {
                    builder.append('\n');
                    deltas.buildToStringRecursively(builder, charsPerNumber, omitLeftPaddingNumber, paddingLeft + 1);
                }
            }
        }

        private static int padCalculateLeftPadding(String s, int maxLength) {
            boolean left = false;
            int length = s.length();

            int leftPad = 0;
            while (length < maxLength) {
                if (left) {
                    leftPad++;
                }

                left = !left;
                length++;
            }

            return leftPad;
        }

        private static void appendPadded(StringBuilder builder, String s, int maxLength, int omitLeft, int omitRight) {
            boolean left = false;
            int length = s.length();

            int leftPad = 0;
            int rightPad = 0;

            while (length < maxLength) {
                if (left && omitLeft-- <= 0) {
                    leftPad++;
                }

                if (!left && omitRight-- <= 0) {
                    rightPad++;
                }

                left = !left;
                length++;
            }

            for (int i = 0; i < leftPad; i++) {
                builder.append(' ');
            }

            builder.append(s);

            for (int i = 0; i < rightPad; i++) {
                builder.append(' ');
            }
        }
    }
}
