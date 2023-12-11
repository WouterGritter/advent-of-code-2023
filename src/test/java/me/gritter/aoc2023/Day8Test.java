package me.gritter.aoc2023;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class Day8Test {

    private static final String SAMPLE_1 = "day8-sample.txt";
    private static final String SAMPLE_2 = "day8-sample2.txt";
    private static final String SAMPLE_3 = "day8-sample3.txt";
    private static final String PUZZLE = "day8-puzzle.txt";

    private static final Solution SOLUTION = new Day8();

    @Test
    public void shouldCalculateSample1() {
        assertEquals(
                2,
                SOLUTION.solution_star1(SAMPLE_1)
        );
    }

    @Test
    public void shouldCalculateSample2() {
        assertEquals(
                6,
                SOLUTION.solution_star1(SAMPLE_2)
        );
    }

    @Test
    public void shouldCalculateSample3() {
        assertEquals(
                6,
                SOLUTION.solution_star2(SAMPLE_3)
        );
    }

    @Test
    public void shouldCalculateStar1() {
        assertEquals(
                12643,
                SOLUTION.solution_star1(PUZZLE)
        );
    }

    @Test
    public void shouldCalculateStar2() {
        assertEquals(
                13133452426987L,
                SOLUTION.solution_star2(PUZZLE)
        );
    }
}
