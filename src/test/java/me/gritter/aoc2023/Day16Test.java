package me.gritter.aoc2023;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class Day16Test {

    private static final String SAMPLE = "day16-sample.txt";
    private static final String PUZZLE = "day16-puzzle.txt";

    private static final Solution SOLUTION = new Day16();

    @Test
    public void shouldCalculateSample() {
        assertEquals(
                46,
                SOLUTION.solution_star1(SAMPLE)
        );

        assertEquals(
                51,
                SOLUTION.solution_star2(SAMPLE)
        );
    }

    @Test
    public void shouldCalculateStar1() {
        assertEquals(
                8116,
                SOLUTION.solution_star1(PUZZLE)
        );
    }

    @Test
    public void shouldCalculateStar2() {
        assertEquals(
                8383,
                SOLUTION.solution_star2(PUZZLE)
        );
    }
}
