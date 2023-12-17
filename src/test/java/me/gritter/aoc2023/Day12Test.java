package me.gritter.aoc2023;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class Day12Test {

    private static final String SAMPLE = "day12-sample.txt";
    private static final String PUZZLE = "day12-puzzle.txt";

    private static final Solution SOLUTION = new Day12();

    @Test
    public void shouldCalculateSample() {
        assertEquals(
                21,
                SOLUTION.solution_star1(SAMPLE)
        );

        assertEquals(
                525152,
                SOLUTION.solution_star2(SAMPLE)
        );
    }

    @Test
    public void shouldCalculateStar1() {
        assertEquals(
                7939,
                SOLUTION.solution_star1(PUZZLE)
        );
    }

    @Test
    public void shouldCalculateStar2() {
        assertEquals(
                850504257483930L,
                SOLUTION.solution_star2(PUZZLE)
        );
    }
}
