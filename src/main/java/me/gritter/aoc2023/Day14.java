package me.gritter.aoc2023;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class Day14 implements Solution {

    public static void main(String[] args) {
        Solution solution = new Day14();
        System.out.println(solution.solution_star2("day14-puzzle.txt"));
    }

    @Override
    public long solution_star1(String file) {
        var dish = loadMirrorDish(file);
        dish.tiltNorth();

        return dish.calculateTotalLoad();
    }

    @Override
    public long solution_star2(String file) {
        var dish = loadMirrorDish(file);

        List<MirrorDish> states = new ArrayList<>();
        int repeatStartIndex = -1;

        while (repeatStartIndex == -1) {
            dish.tiltNorth();
            dish.tiltWest();
            dish.tiltSouth();
            dish.tiltEast();

            var dishCopy = dish.copy();
            if (states.contains(dishCopy)) {
                repeatStartIndex = states.indexOf(dishCopy);
            } else {
                states.add(dishCopy);
            }
        }

        int repeatingLength = states.size() - repeatStartIndex;

        int n = 1000000000 - 1; // Convert from cycles to index (-1)
        int index = repeatStartIndex + (n - repeatStartIndex) % repeatingLength;
        MirrorDish state = states.get(index);

        return state.calculateTotalLoad();
    }

    private MirrorDish loadMirrorDish(String file) {
        List<String> lines = Utils.readLines(file).collect(Collectors.toList());

        int width = lines.get(0).length();
        int height = lines.size();
        char[] symbols = new char[width * height];

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                symbols[x + y * width] = lines.get(y).charAt(x);
            }
        }

        return new MirrorDish(symbols, width, height);
    }

    private static class MirrorDish {

        private final char[] symbols;
        private final int width;
        private final int height;

        public MirrorDish(char[] symbols, int width, int height) {
            if (symbols.length != width * height) {
                throw new IllegalArgumentException();
            }

            this.symbols = symbols;
            this.width = width;
            this.height = height;
        }

        public void tiltNorth() {
            tiltVertically(-1);
        }

        public void tiltEast() {
            tiltHorizontally(1);
        }

        public void tiltSouth() {
            tiltVertically(1);
        }

        public void tiltWest() {
            tiltHorizontally(-1);
        }

        public void tiltVertically(int dy) {
            for (int y = dy == 1 ? height - 1 : 0; y >= 0 && y < height; y -= dy) {
                for (int x = 0; x < width; x++) {
                    char symbol = getSymbol(x, y);
                    if (symbol == 'O') {
                        // Slide this symbol as far up as it can go
                        for (int ny = y + dy; ny >= 0 && ny < height; ny += dy) {
                            if (getSymbol(x, ny) == '.') {
                                setSymbol(x, ny - dy, '.');
                                setSymbol(x, ny, 'O');
                            } else {
                                break;
                            }
                        }
                    }
                }
            }
        }

        public void tiltHorizontally(int dx) {
            for (int y = 0; y < height; y++) {
                for (int x = dx == 1 ? width - 1 : 0; x >= 0 && x < width; x -= dx) {
                    char symbol = getSymbol(x, y);
                    if (symbol == 'O') {
                        // Slide this symbol as far up as it can go
                        for (int nx = x + dx; nx >= 0 && nx < width; nx += dx) {
                            if (getSymbol(nx, y) == '.') {
                                setSymbol(nx - dx, y, '.');
                                setSymbol(nx, y, 'O');
                            } else {
                                break;
                            }
                        }
                    }
                }
            }
        }

        public int calculateTotalLoad() {
            int totalLoad = 0;

            for (int y = 0; y < height; y++) {
                int loadPerStone = height - y;
                for (int x = 0; x < width; x++) {
                    if (getSymbol(x, y) == 'O') {
                        totalLoad += loadPerStone;
                    }
                }
            }

            return totalLoad;
        }

        public char getSymbol(int x, int y) {
            return symbols[x + y * width];
        }

        public void setSymbol(int x, int y, char symbol) {
            symbols[x + y * width] = symbol;
        }

        public MirrorDish copy() {
            return new MirrorDish(Arrays.copyOf(symbols, symbols.length), width, height);
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder(width * height + height - 1);

            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    char symbol = getSymbol(x, y);
                    sb.append(symbol);
                }

                if (y != height - 1) {
                    sb.append('\n');
                }
            }

            return sb.toString();
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            MirrorDish that = (MirrorDish) o;
            return width == that.width && height == that.height && Arrays.equals(symbols, that.symbols);
        }

        @Override
        public int hashCode() {
            int result = Objects.hash(width, height);
            result = 31 * result + Arrays.hashCode(symbols);
            return result;
        }
    }
}
