package me.gritter.aoc2023;

import me.gritter.aoc2023.Day9.Sequence;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class Day9Test {

    private static final String SAMPLE = "day9-sample.txt";
    private static final String PUZZLE = "day9-puzzle.txt";

    private static final Solution SOLUTION = new Day9();

    @Test
    public void shouldCalculateSample() {
        assertEquals(
                114,
                SOLUTION.solution_star1(SAMPLE)
        );

        assertEquals(
                2,
                SOLUTION.solution_star2(SAMPLE)
        );
    }

    @Test
    public void shouldCalculateStar1() {
        assertEquals(
                1834108701,
                SOLUTION.solution_star1(PUZZLE)
        );
    }

    @Test
    public void shouldCalculateStar2() {
        assertEquals(
                993,
                SOLUTION.solution_star2(PUZZLE)
        );
    }

    @Test
    public void shouldGenerateCorrectToString1() {
        Sequence sequence = new Sequence(0, 3, 6, 9, 12, 15);

        assertEquals(
                "0   3   6   9  12  15\n" +
                        "  3   3   3   3   3\n" +
                        "    0   0   0   0",
                sequence.toString()
        );
    }

    @Test
    public void shouldGenerateCorrectToString2() {
        Sequence sequence = new Sequence(1, 3, 6, 10, 15, 21);

        assertEquals(
                "1   3   6  10  15  21\n" +
                        "  2   3   4   5   6\n" +
                        "    1   1   1   1\n" +
                        "      0   0   0",
                sequence.toString()
        );
    }

    @Test
    public void shouldGenerateCorrectToString3() {
        Sequence sequence = new Sequence(10, 13, 16, 21, 30, 45, 68);

        assertEquals(
                "10  13  16  21  30  45  68\n" +
                        "   3   3   5   9  15  23\n" +
                        "     0   2   4   6   8\n" +
                        "       2   2   2   2\n" +
                        "         0   0   0",
                sequence.toString()
        );
    }

    @Test
    public void shouldGenerateCorrectToString4() {
        Sequence sequence = new Sequence(5, 10, 13, 16, 21, 30, 45);

        assertEquals(
                "5  10  13  16  21  30  45\n" +
                        "  5   3   3   5   9  15\n" +
                        "   -2   0   2   4   6\n" +
                        "      2   2   2   2\n" +
                        "        0   0   0",
                sequence.toString()
        );
    }
}
