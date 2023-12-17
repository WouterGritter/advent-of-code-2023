package me.gritter.aoc2023;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class Day15Test {

    private static final String SAMPLE = "day15-sample.txt";
    private static final String PUZZLE = "day15-puzzle.txt";

    private static final Solution SOLUTION = new Day15();

    @Test
    public void shouldCalculateSample() {
        assertEquals(
                1320,
                SOLUTION.solution_star1(SAMPLE)
        );

        assertEquals(
                145,
                SOLUTION.solution_star2(SAMPLE)
        );
    }

    @Test
    public void shouldCalculateStar1() {
        assertEquals(
                510273,
                SOLUTION.solution_star1(PUZZLE)
        );
    }

    @Test
    public void shouldCalculateStar2() {
        assertEquals(
                212449,
                SOLUTION.solution_star2(PUZZLE)
        );
    }
}
