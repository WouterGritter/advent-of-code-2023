package me.gritter.aoc2023;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class Day4Test {

    private static final String SAMPLE = "day4-sample.txt";
    private static final String PUZZLE = "day4-puzzle.txt";

    private static final Solution SOLUTION = new Day4();

    @Test
    public void shouldCalculateSample() {
        assertEquals(
                13,
                SOLUTION.solution_star1(SAMPLE)
        );

        assertEquals(
                30,
                SOLUTION.solution_star2(SAMPLE)
        );
    }

    @Test
    public void shouldCalculateStar1() {
        assertEquals(
                21959,
                SOLUTION.solution_star1(PUZZLE)
        );
    }

    @Test
    public void shouldCalculateStar2() {
        assertEquals(
                5132675,
                SOLUTION.solution_star2(PUZZLE)
        );
    }
}
