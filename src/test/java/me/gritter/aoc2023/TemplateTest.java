package me.gritter.aoc2023;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Disabled("Disabled: Template class")
public class TemplateTest {

    private static final String SAMPLE = "dayx-sample.txt";
    private static final String PUZZLE = "dayx-puzzle.txt";

    private static final Solution SOLUTION = new Template();

    @Test
    public void shouldCalculateSample() {
        assertEquals(
                1337,
                SOLUTION.solution_star1(SAMPLE)
        );

        assertEquals(
                7331,
                SOLUTION.solution_star2(SAMPLE)
        );
    }

    @Test
    public void shouldCalculateStar1() {
        assertEquals(
                13371337,
                SOLUTION.solution_star1(PUZZLE)
        );
    }

    @Test
    public void shouldCalculateStar2() {
        assertEquals(
                73317331,
                SOLUTION.solution_star2(PUZZLE)
        );
    }
}
