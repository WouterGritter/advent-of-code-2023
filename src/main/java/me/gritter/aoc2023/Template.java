package me.gritter.aoc2023;

public class Template {

    public static void main(String[] args) {
        new Template().solution("dayx-sample.txt");
    }

    public void solution(String file) {
        Utils.readLines(file)
                .forEach(System.out::println);
    }
}
