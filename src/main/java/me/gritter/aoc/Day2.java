package me.gritter.aoc;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.lang.Integer.parseInt;
import static org.apache.commons.lang3.StringUtils.*;

public class Day2 {

    private static final Map<String, Integer> MAX_COLORS = Map.of(
            "red", 12,
            "green", 13,
            "blue", 14
    );

    public static void main(String[] args) {
//        new Day2().solution("day2-sample.txt");
        new Day2().solution("day2-puzzle.txt");
    }

    public void solution(String file) {
        // Star 1
//        int sum = Utils.readLines(file)
//                .map(this::parseGame)
//                .filter(Game::isPossible)
//                .mapToInt(Game::getId)
//                .sum();

        // Star 2
        int sum = Utils.readLines(file)
                .map(this::parseGame)
                .map(Game::getMinimumRequiredSet)
                .mapToInt(GameSet::getPower)
                .sum();

        System.out.println(sum);
    }

    private Game parseGame(String gameStr) {
        int id = parseInt(substringBetween(gameStr, "Game ", ": "));

        String setsStr = substringAfter(gameStr, ": ");
        Collection<GameSet> sets = Stream.of(setsStr.split("; "))
                .map(this::parseSet)
                .collect(Collectors.toList());

        return new Game(id, sets);
    }

    private GameSet parseSet(String setStr) {
        Map<String, Integer> set = new HashMap<>();
        for (String cube : setStr.split(", ")) {
            int amount = parseInt(substringBefore(cube, " "));
            String color = substringAfter(cube, " ");

            if (!MAX_COLORS.containsKey(color)) {
                throw new IllegalStateException();
            }

            set.put(color, amount);
        }

        return new GameSet(set);
    }

    private static class Game {
        private final int id;
        private final Collection<GameSet> sets;

        public Game(int id, Collection<GameSet> sets) {
            this.id = id;
            this.sets = sets;
        }

        public int getId() {
            return id;
        }

        public Collection<GameSet> getSets() {
            return sets;
        }

        public boolean isPossible() {
            return sets.stream().allMatch(GameSet::isPossible);
        }

        public GameSet getMinimumRequiredSet() {
            Map<String, Integer> minReqCubes = new HashMap<>();
            for (GameSet set : this.sets) {
                for (String color : set.getColors().keySet()) {
                    int amount = set.getColors().get(color);
                    if (amount > minReqCubes.getOrDefault(color, 0)) {
                        minReqCubes.put(color, amount);
                    }
                }
            }

            return new GameSet(minReqCubes);
        }

        @Override
        public String toString() {
            return "Game{" +
                    "id=" + id +
                    ", sets=" + sets +
                    '}';
        }
    }

    private static class GameSet {
        private final Map<String, Integer> colors;

        public GameSet(Map<String, Integer> colors) {
            this.colors = colors;
        }

        public Map<String, Integer> getColors() {
            return colors;
        }

        public boolean isPossible() {
            for (String color : colors.keySet()) {
                int amount = colors.get(color);
                int maxAmount = MAX_COLORS.get(color);

                if (amount > maxAmount) {
                    return false;
                }
            }

            return true;
        }

        public int getPower() {
            int power = 1;
            for (int amount : colors.values()) {
                power *= amount;
            }

            return power;
        }

        @Override
        public String toString() {
            return "GameSet{" +
                    "colors=" + colors +
                    '}';
        }
    }
}
