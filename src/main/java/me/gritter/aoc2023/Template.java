package me.gritter.aoc2023;

public class Template implements Solution {

    public static void main(String[] args) {
        Solution solution = new Template();
        System.out.println(solution.solution_star1("dayx-sample.txt"));
    }

    @Override
    public long solution_star1(String file) {
        Utils.readLines(file)
                .forEach(System.out::println);

        throw new UnsupportedOperationException();
    }

    @Override
    public long solution_star2(String file) {
        Utils.readLines(file)
                .forEach(System.out::println);

        throw new UnsupportedOperationException();
    }
}
