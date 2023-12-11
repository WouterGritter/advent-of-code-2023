package me.gritter.aoc2023;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class Day1Test {

    private static final String SAMPLE = "day1-sample.txt";
    private static final String SAMPLE_2 = "day1-sample2.txt";
    private static final String PUZZLE = "day1-puzzle.txt";

    private static final Solution SOLUTION = new Day1();

    @Test
    public void shouldCalculateSample1() {
        assertEquals(
                142,
                SOLUTION.solution_star1(SAMPLE)
        );
    }

    @Test
    public void shouldCalculateSample2() {
        assertEquals(
                281,
                SOLUTION.solution_star2(SAMPLE_2)
        );
    }

    @Test
    public void shouldCalculateStar1() {
        assertEquals(
                55621,
                SOLUTION.solution_star1(PUZZLE)
        );
    }

    @Test
    public void shouldCalculateStar2() {
        assertEquals(
                53592,
                SOLUTION.solution_star2(PUZZLE)
        );
    }
}
