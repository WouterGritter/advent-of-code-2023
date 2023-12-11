package me.gritter.aoc2023;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class Day6Test {

    private static final String SAMPLE = "day6-sample.txt";
    private static final String PUZZLE = "day6-puzzle.txt";

    private static final Solution SOLUTION = new Day6();

    @Test
    public void shouldCalculateSample() {
        assertEquals(
                288,
                SOLUTION.solution_star1(SAMPLE)
        );

        assertEquals(
                71503,
                SOLUTION.solution_star2(SAMPLE)
        );
    }

    @Test
    public void shouldCalculateStar1() {
        assertEquals(
                512295,
                SOLUTION.solution_star1(PUZZLE)
        );
    }

    @Test
    public void shouldCalculateStar2() {
        assertEquals(
                36530883,
                SOLUTION.solution_star2(PUZZLE)
        );
    }
}
