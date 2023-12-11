package me.gritter.aoc2023;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class Day10 implements Solution {

    public static void main(String[] args) {
        Solution solution = new Day10();
        System.out.println(solution.solution_star2("day10-puzzle.txt"));
    }

    @Override
    public long solution_star1(String file) {
        Board board = loadBoard(file);

        Position startPosition = board.positionStream()
                .filter(pos -> board.get(pos) == Tile.START)
                .findAny()
                .orElseThrow();

        Collection<Position> visited = new HashSet<>();
        visited.add(startPosition);

        Collection<Position> currentPositions = Set.of(startPosition);

        int distance = 0;
        while (!currentPositions.isEmpty()) {
            currentPositions = currentPositions.stream()
                    .flatMap(board::getConnectingTiles)
                    .filter(pos -> !visited.contains(pos))
                    .collect(Collectors.toSet());

            if (!currentPositions.isEmpty()) {
                distance++;
                visited.addAll(currentPositions);
            }
        }

        return distance;
    }

    @Override
    public long solution_star2(String file) {
        Board board = loadBoard(file);

        Position startPosition = board.positionStream()
                .filter(pos -> board.get(pos) == Tile.START)
                .findAny()
                .orElseThrow();

        Set<Position> loopPositions = new HashSet<>();
        loopPositions.add(startPosition);

        List<Position> orderedLoopPositions = new ArrayList<>();
        orderedLoopPositions.add(startPosition);

        while (true) {
            var current = orderedLoopPositions.get(orderedLoopPositions.size() - 1);
            var next = board.getConnectingTiles(current)
                    .filter(pos -> !loopPositions.contains(pos))
                    .findAny();

            if (next.isPresent()) {
                loopPositions.add(next.get());
                orderedLoopPositions.add(next.get());
            } else {
                break;
            }
        }

        orderedLoopPositions.add(startPosition);

        Set<Position> rightHandPositions = new HashSet<>();
        Set<Position> leftHandPositions = new HashSet<>();

        for (int i = 1; i < orderedLoopPositions.size(); i++) {
            Position previous = orderedLoopPositions.get(i - 1);
            Position current = orderedLoopPositions.get(i);

            Position delta = current.subtract(previous);

            Stream.of(
                            delta.rotateClockwise90().add(previous),
                            delta.rotateClockwise90().add(current)
                    )
                    .filter(pos -> !loopPositions.contains(pos))
                    .forEach(rightHandPositions::add);

            Stream.of(
                            delta.rotateCounterClockwise90().add(previous),
                            delta.rotateCounterClockwise90().add(current)
                    )
                    .filter(pos -> !loopPositions.contains(pos))
                    .forEach(leftHandPositions::add);
        }

        flush(board, rightHandPositions, loopPositions);
        flush(board, leftHandPositions, loopPositions);

        Set<Position> insidePositions;
        Set<Position> outsidePositions;

        if (rightHandPositions.stream().noneMatch(board::isOnBorder)) {
            insidePositions = rightHandPositions;
            outsidePositions = leftHandPositions;
        } else if (leftHandPositions.stream().noneMatch(board::isOnBorder)) {
            insidePositions = leftHandPositions;
            outsidePositions = rightHandPositions;
        } else {
            throw new IllegalStateException();
        }

        for (int y = 0; y < board.getHeight(); y++) {
            for (int x = 0; x < board.getWidth(); x++) {
                Position pos = new Position(x, y);
                if (loopPositions.contains(pos)) {
                    Tile tile = board.get(pos);
                    System.out.print(tile.symbol());
                } else if (rightHandPositions.contains(pos)) {
                    System.out.print('A');
                } else if (leftHandPositions.contains(pos)) {
                    System.out.print('B');
                } else {
                    System.out.print(' ');
                }
            }

            System.out.println();
        }

        System.out.println();

        for (int y = 0; y < board.getHeight(); y++) {
            for (int x = 0; x < board.getWidth(); x++) {
                Position pos = new Position(x, y);
                if (loopPositions.contains(pos)) {
                    Tile tile = board.get(pos);
                    System.out.print(tile.symbol());
                } else if (insidePositions.contains(pos)) {
                    System.out.print('I');
                } else if (outsidePositions.contains(pos)) {
                    System.out.print('O');
                } else {
                    System.out.print(' ');
                }
            }

            System.out.println();
        }

        System.out.println();
        System.out.println("insidePositions.size() = " + insidePositions.size());

        return insidePositions.size();
    }

    private void flush(Board board, Set<Position> flushPositions, Set<Position> loopPositions) {
        Set<Position> currentPositions = new HashSet<>(flushPositions);
        while (!currentPositions.isEmpty()) {
            currentPositions = currentPositions
                    .stream()
                    .flatMap(Position::neighborStream)
                    .filter(board::isInBounds)
                    .filter(pos -> !flushPositions.contains(pos))
                    .filter(pos -> !loopPositions.contains(pos))
                    .collect(Collectors.toSet());

            flushPositions.addAll(currentPositions);
        }
    }

    private Board loadBoard(String file) {
        List<String> lines = Utils.readLines(file).collect(Collectors.toList());

        int height = lines.size();
        int width = lines.stream()
                .findFirst()
                .map(String::length)
                .orElseThrow();

        Board board = new Board(width, height);

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                char symbol = lines.get(y).charAt(x);
                Tile tile = Tile.bySymbol(symbol);
                board.set(new Position(x, y), tile);
            }
        }

        return board;
    }

    public enum Tile {

        NORTH_TO_SOUTH('|', Position.NORTH, Position.SOUTH),
        EAST_TO_WEST('-', Position.EAST, Position.WEST),
        NORTH_TO_EAST('L', Position.NORTH, Position.EAST),
        NORTH_TO_WEST('J', Position.NORTH, Position.WEST),
        SOUTH_TO_WEST('7', Position.SOUTH, Position.WEST),
        SOUTH_TO_EAST('F', Position.SOUTH, Position.EAST),
        GROUND('.'),
        START('S', Position.NORTH, Position.EAST, Position.SOUTH, Position.WEST);

        private final char symbol;
        private final Collection<Position> deltas;

        Tile(char symbol, Position... deltas) {
            this.symbol = symbol;
            this.deltas = Set.of(deltas);
        }

        public char symbol() {
            return symbol;
        }

        public Stream<Position> deltas() {
            return deltas.stream();
        }

        public static Tile bySymbol(char symbol) {
            for (Tile tile : values()) {
                if (tile.symbol == symbol) {
                    return tile;
                }
            }

            throw new IllegalArgumentException();
        }
    }

    public static class Board {

        private final Tile[] tiles;
        private final int width;
        private final int height;

        public Board(int width, int height) {
            this.tiles = new Tile[width * height];
            this.width = width;
            this.height = height;

            Arrays.fill(tiles, Tile.GROUND);
        }

        public boolean isInBounds(Position position) {
            return position.x >= 0 && position.x < width && position.y >= 0 && position.y < height;
        }

        public boolean isOnBorder(Position position) {
            return position.x == 0 || position.x == width - 1 || position.y == 0 || position.y == height - 1;
        }

        public Tile get(Position position) {
            if (!isInBounds(position)) {
                throw new IllegalArgumentException(String.valueOf(position));
            }

            return tiles[position.y * width + position.x];
        }

        public void set(Position position, Tile tile) {
            if (!isInBounds(position)) {
                throw new IllegalArgumentException();
            }

            tiles[position.y * width + position.x] = tile;
        }

        public Stream<Position> positionStream() {
            return IntStream.range(0, width * height - 1)
                    .mapToObj(i -> new Position(i % width, i / width));
        }

        public Stream<Position> getConnectingTiles(Position position) {
            return get(position)
                    .deltas()
                    .map(position::add)
                    .filter(this::isInBounds)
                    .filter(neighborPosition ->
                            get(neighborPosition)
                                    .deltas()
                                    .map(neighborPosition::add)
                                    .anyMatch(position::equals)
                    );
        }

        public int getWidth() {
            return width;
        }

        public int getHeight() {
            return height;
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();

            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    Tile tile = get(new Position(x, y));
                    sb.append(tile.symbol());
                }

                if (y != height - 1) {
                    sb.append('\n');
                }
            }

            return sb.toString();
        }
    }

    public static class Position {

        public static final Position NORTH = new Position(0, -1);
        public static final Position EAST = new Position(1, 0);
        public static final Position SOUTH = new Position(0, 1);
        public static final Position WEST = new Position(-1, 0);

        public final int x;
        public final int y;

        public Position(int x, int y) {
            this.x = x;
            this.y = y;
        }

        public Stream<Position> neighborStream() {
            return Stream.of(NORTH, EAST, SOUTH, WEST)
                    .map(this::add);
        }

        public Position rotateClockwise90() {
            return new Position(-y, x);
        }

        public Position rotateCounterClockwise90() {
            return new Position(y, -x);
        }

        public Position add(Position delta) {
            return new Position(x + delta.x, y + delta.y);
        }

        public Position subtract(Position delta) {
            return new Position(x - delta.x, y - delta.y);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Position position = (Position) o;
            return x == position.x && y == position.y;
        }

        @Override
        public int hashCode() {
            return Objects.hash(x, y);
        }

        @Override
        public String toString() {
            return "Position{" +
                    "x=" + x +
                    ", y=" + y +
                    '}';
        }
    }
}

