package me.gritter.aoc2023;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class Day3Test {

    private static final String SAMPLE = "day3-sample.txt";
    private static final String PUZZLE = "day3-puzzle.txt";

    private static final Solution SOLUTION = new Day3();

    @Test
    public void shouldCalculateSample() {
        assertEquals(
                4361,
                SOLUTION.solution_star1(SAMPLE)
        );

        assertEquals(
                467835,
                SOLUTION.solution_star2(SAMPLE)
        );
    }

    @Test
    public void shouldCalculateStar1() {
        assertEquals(
                543867,
                SOLUTION.solution_star1(PUZZLE)
        );
    }

    @Test
    public void shouldCalculateStar2() {
        assertEquals(
                79613331,
                SOLUTION.solution_star2(PUZZLE)
        );
    }
}
