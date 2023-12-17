package me.gritter.aoc2023;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class Day13Test {

    private static final String SAMPLE = "day13-sample.txt";
    private static final String PUZZLE = "day13-puzzle.txt";

    private static final Solution SOLUTION = new Day13();

    @Test
    public void shouldCalculateSample() {
        assertEquals(
                405,
                SOLUTION.solution_star1(SAMPLE)
        );

        assertEquals(
                400,
                SOLUTION.solution_star2(SAMPLE)
        );
    }

    @Test
    public void shouldCalculateStar1() {
        assertEquals(
                26957,
                SOLUTION.solution_star1(PUZZLE)
        );
    }

    @Test
    public void shouldCalculateStar2() {
        assertEquals(
                42695,
                SOLUTION.solution_star2(PUZZLE)
        );
    }
}
