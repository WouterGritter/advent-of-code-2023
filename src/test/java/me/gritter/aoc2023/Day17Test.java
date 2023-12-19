package me.gritter.aoc2023;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class Day17Test {

    private static final String SAMPLE_1 = "day17-sample.txt";
    private static final String SAMPLE_2 = "day17-sample2.txt";
    private static final String PUZZLE = "day17-puzzle.txt";

    private static final Solution SOLUTION = new Day17();

    @Test
    public void shouldCalculateSample1() {
        assertEquals(
                102,
                SOLUTION.solution_star1(SAMPLE_1)
        );

        assertEquals(
                94,
                SOLUTION.solution_star2(SAMPLE_1)
        );
    }

    @Test
    public void shouldCalculateSample2() {
        assertEquals(
                71,
                SOLUTION.solution_star2(SAMPLE_2)
        );
    }

    @Disabled("Disabled: test takes too long")
    @Test
    public void shouldCalculateStar1() {
        assertEquals(
                916,
                SOLUTION.solution_star1(PUZZLE)
        );
    }

    @Disabled("Disabled: test takes too long")
    @Test
    public void shouldCalculateStar2() {
        assertEquals(
                1067,
                SOLUTION.solution_star2(PUZZLE)
        );
    }
}
