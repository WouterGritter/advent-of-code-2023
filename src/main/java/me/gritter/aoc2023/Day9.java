package me.gritter.aoc2023;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Day9 {

    public static void main(String[] args) {
        new Day9().solution("day9-puzzle.txt");
    }

    public void solution(String file) {
        int sum = Utils.readLines(file)
                .map(this::parseSequence)
//                .mapToInt(Sequence::predictNext) // Star 1
                .mapToInt(Sequence::predictPrevious) // Star 2
                .sum();

        System.out.println(sum);
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
            if (numbers.isEmpty()) {
                throw new IllegalStateException();
            }

            this.numbers = numbers;
        }

        public List<Integer> getNumbers() {
            return Collections.unmodifiableList(numbers);
        }

        public int getLast() {
            return numbers.get(numbers.size() - 1);
        }

        public int getFirst() {
            return numbers.get(0);
        }

        public int predictNext() {
            if (isZeros()) {
                return 0;
            }

            Sequence delta = calculateDeltas();
            return getLast() + delta.predictNext();
        }

        public int predictPrevious() {
            if (isZeros()) {
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
            return numbers.toString();
        }
    }
}
