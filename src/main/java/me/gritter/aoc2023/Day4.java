package me.gritter.aoc2023;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.apache.commons.collections4.CollectionUtils.intersection;
import static org.apache.commons.lang3.StringUtils.substringAfter;
import static org.apache.commons.lang3.StringUtils.substringBetween;

public class Day4 {

    public static void main(String[] args) {
        new Day4().solution("day4-puzzle.txt");
    }

    public void solution(String file) {
        // Star 1
//        int sum = Utils.readLines(file)
//                .map(this::parseScratchGame)
//                .mapToInt(ScratchGame::getScore)
//                .sum();

        // Star 2
        List<ScratchGame> games = Utils.readLines(file)
                .map(this::parseScratchGame)
                .collect(Collectors.toList());

        int sum = 0;
        for (int i = 0; i < games.size(); i++) {
            ScratchGame game = games.get(i);
            int matches = game.calculateMatches();
            int amount = game.getAmount();
            sum += amount;
            for (int j = 0; j < matches; j++) {
                if (i + j + 1 >= games.size()) break;
                games.get(i + j + 1).increaseAmount(amount);
            }
        }

        System.out.println(sum);
    }

    private ScratchGame parseScratchGame(String game) {
        String cardStr = substringBetween(game, ": ", " | ");
        String numsStr = substringAfter(game, " | ");

        return new ScratchGame(
                parseNumbers(cardStr),
                parseNumbers(numsStr)
        );
    }

    private Collection<Integer> parseNumbers(String string) {
        return Stream.of(string.trim().split("\\s+"))
                .map(Integer::parseInt)
                .collect(Collectors.toSet());
    }

    public static class ScratchGame {

        private int amount = 1;

        private final Collection<Integer> card;
        private final Collection<Integer> numbers;

        public ScratchGame(Collection<Integer> card, Collection<Integer> numbers) {
            this.card = card;
            this.numbers = numbers;
        }

        public int getAmount() {
            return amount;
        }

        public void setAmount(int amount) {
            this.amount = amount;
        }

        public void increaseAmount(int amountDelta) {
            this.amount += amountDelta;
        }

        public int calculateMatches() {
            return intersection(card, numbers).size();
        }

        public int getScore() {
            int intersections = calculateMatches();
            if (intersections == 0) {
                return 0;
            }

            return (int) Math.pow(2, intersections - 1);
        }
    }
}
