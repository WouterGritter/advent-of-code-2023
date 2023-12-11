package me.gritter.aoc2023;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class Day11Test {

    private static final String SAMPLE = "day11-sample.txt";
    private static final String PUZZLE = "day11-puzzle.txt";

    private static final Day11 SOLUTION = new Day11();

    @Test
    public void shouldCalculateSample() {
        assertEquals(
                374,
                SOLUTION.solution_star1(SAMPLE)
        );

        assertEquals(
                1030,
                SOLUTION.solution(SAMPLE, 10)
        );

        assertEquals(
                8410,
                SOLUTION.solution(SAMPLE, 100)
        );
    }

    @Test
    public void shouldCalculateStar1() {
        assertEquals(
                10231178,
                SOLUTION.solution_star1(PUZZLE)
        );
    }

    @Test
    public void shouldCalculateStar2() {
        assertEquals(
                622120986954L,
                SOLUTION.solution_star2(PUZZLE)
        );
    }
}
