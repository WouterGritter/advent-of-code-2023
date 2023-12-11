package me.gritter.aoc2023;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class Day2Test {

    private static final String SAMPLE = "day2-sample.txt";
    private static final String PUZZLE = "day2-puzzle.txt";

    private static final Solution SOLUTION = new Day2();

    @Test
    public void shouldCalculateSample() {
        assertEquals(
                8,
                SOLUTION.solution_star1(SAMPLE)
        );

        assertEquals(
                2286,
                SOLUTION.solution_star2(SAMPLE)
        );
    }

    @Test
    public void shouldCalculateStar1() {
        assertEquals(
                2551,
                SOLUTION.solution_star1(PUZZLE)
        );
    }

    @Test
    public void shouldCalculateStar2() {
        assertEquals(
                62811,
                SOLUTION.solution_star2(PUZZLE)
        );
    }
}
