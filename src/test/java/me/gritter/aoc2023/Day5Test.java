package me.gritter.aoc2023;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class Day5Test {

    private static final String SAMPLE = "day5-sample.txt";
    private static final String PUZZLE = "day5-puzzle.txt";

    private static final Solution SOLUTION = new Day5();

    @Test
    public void shouldCalculateSample() {
        assertEquals(
                35,
                SOLUTION.solution_star1(SAMPLE)
        );

        assertEquals(
                46,
                SOLUTION.solution_star2(SAMPLE)
        );
    }

    @Test
    public void shouldCalculateStar1() {
        assertEquals(
                265018614,
                SOLUTION.solution_star1(PUZZLE)
        );
    }

    @Disabled("Disabled: test takes too long")
    @Test
    public void shouldCalculateStar2() {
        assertEquals(
                63179500,
                SOLUTION.solution_star2(PUZZLE)
        );
    }
}
