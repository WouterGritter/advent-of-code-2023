package me.gritter.aoc2023;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class Day7Test {

    private static final String SAMPLE = "day7-sample.txt";
    private static final String PUZZLE = "day7-puzzle.txt";

    private static final Solution SOLUTION = new Day7();

    @Test
    public void shouldCalculateSample() {
        assertEquals(
                6440,
                SOLUTION.solution_star1(SAMPLE)
        );

        assertEquals(
                5905,
                SOLUTION.solution_star2(SAMPLE)
        );
    }

    @Test
    public void shouldCalculateStar1() {
        assertEquals(
                251121738,
                SOLUTION.solution_star1(PUZZLE)
        );
    }

    @Disabled("Disabled: test takes too long")
    @Test
    public void shouldCalculateStar2() {
        assertEquals(
                251421071,
                SOLUTION.solution_star2(PUZZLE)
        );
    }
}
