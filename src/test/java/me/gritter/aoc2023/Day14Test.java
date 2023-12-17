package me.gritter.aoc2023;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class Day14Test {

    private static final String SAMPLE = "day14-sample.txt";
    private static final String PUZZLE = "day14-puzzle.txt";

    private static final Solution SOLUTION = new Day14();

    @Test
    public void shouldCalculateSample() {
        assertEquals(
                136,
                SOLUTION.solution_star1(SAMPLE)
        );

        assertEquals(
                64,
                SOLUTION.solution_star2(SAMPLE)
        );
    }

    @Test
    public void shouldCalculateStar1() {
        assertEquals(
                105461,
                SOLUTION.solution_star1(PUZZLE)
        );
    }

    @Test
    public void shouldCalculateStar2() {
        assertEquals(
                102829,
                SOLUTION.solution_star2(PUZZLE)
        );
    }
}
