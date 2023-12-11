package me.gritter.aoc2023;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class Day10Test {

    private static final String SAMPLE_1 = "day10-sample.txt";
    private static final String SAMPLE_2 = "day10-sample2.txt";
    private static final String SAMPLE_3 = "day10-sample3.txt";
    private static final String SAMPLE_4 = "day10-sample4.txt";
    private static final String SAMPLE_5 = "day10-sample5.txt";
    private static final String PUZZLE = "day10-puzzle.txt";

    private static final Solution SOLUTION = new Day10();

    @Test
    public void shouldCalculateSample1() {
        assertEquals(
                4,
                SOLUTION.solution_star1(SAMPLE_1)
        );
    }

    @Test
    public void shouldCalculateSample2() {
        assertEquals(
                4,
                SOLUTION.solution_star1(SAMPLE_2)
        );
    }

    @Test
    public void shouldCalculateSample3() {
        assertEquals(
                8,
                SOLUTION.solution_star1(SAMPLE_3)
        );
    }

    @Test
    public void shouldCalculateSample4() {
        assertEquals(
                4,
                SOLUTION.solution_star2(SAMPLE_4)
        );
    }

    @Test
    public void shouldCalculateSample5() {
        assertEquals(
                10,
                SOLUTION.solution_star2(SAMPLE_5)
        );
    }

    @Test
    public void shouldCalculateStar1() {
        assertEquals(
                6875,
                SOLUTION.solution_star1(PUZZLE)
        );
    }

    @Test
    public void shouldCalculateStar2() {
        assertEquals(
                471,
                SOLUTION.solution_star2(PUZZLE)
        );
    }
}
