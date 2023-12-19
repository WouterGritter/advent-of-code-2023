package me.gritter.aoc2023;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Day17 implements Solution {

    public static void main(String[] args) {
        Solution solution = new Day17();
        System.out.println(solution.solution_star2("day17-puzzle.txt"));
    }

    @Override
    public long solution_star1(String file) {
        var heatMap = loadHeatMap(file, -1, 3);
        return solution(heatMap);
    }

    @Override
    public long solution_star2(String file) {
        var heatMap = loadHeatMap(file, 4, 10);
        return solution(heatMap);
    }

    private long solution(HeatMap heatMap) {
        var start = new Point(0, 0);
        var end = new Point(heatMap.getWidth() - 1, heatMap.getHeight() - 1);

        return dijkstra(heatMap, start, end);
    }

    // TODO: Optimize (solution is really slow - should probably use a heap / priority queue)
    private int dijkstra(HeatMap heatMap, Point start, Point end) {
        Node startNode = new Node(heatMap, start, new Point(0, 0), 1);

        Map<Node, Integer> nodeCostMap = new HashMap<>();
        nodeCostMap.put(startNode, 0);

        Collection<Node> visited = new HashSet<>();

        Node currentNode = startNode;

        int step = 0;
        while (true) {
            int currentNodeCost = nodeCostMap.get(currentNode);
            for (Node neighbor : currentNode.calculateNeighbors().collect(Collectors.toSet())) {
                if (!heatMap.isInBounds(neighbor.getLocation()) || visited.contains(neighbor)) {
                    continue;
                }

                int neighborCost = currentNodeCost + heatMap.getValue(neighbor.getLocation());
                if (neighborCost < nodeCostMap.getOrDefault(neighbor, Integer.MAX_VALUE)) {
                    nodeCostMap.put(neighbor, neighborCost);
                }

                if (neighbor.canStop() && neighbor.getLocation().equals(end)) {
                    return neighborCost;
                }
            }

            nodeCostMap.remove(currentNode);
            visited.add(currentNode);

            currentNode = null;
            int minCost = Integer.MAX_VALUE;
            for (Map.Entry<Node, Integer> entry : nodeCostMap.entrySet()) {
                if (entry.getValue() < minCost) {
                    currentNode = entry.getKey();
                    minCost = entry.getValue();
                }
            }

            if (step++ % 5000 == 0) {
                System.out.println("step = " + step);
                System.out.println("visited.size() = " + visited.size());
                System.out.println("nodeCostMap.size() = " + nodeCostMap.size());
            }
        }
    }

    private HeatMap loadHeatMap(String file, int minStraightDistance, int maxStraightDistance) {
        List<String> lines = Utils.readLines(file).collect(Collectors.toList());

        int width = lines.get(0).length();
        int height = lines.size();
        int[] values = new int[width * height];

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                char ch = lines.get(y).charAt(x);
                int value = Integer.parseInt(String.valueOf(ch));
                values[x + y * width] = value;
            }
        }

        return new HeatMap(values, width, height, minStraightDistance, maxStraightDistance);
    }

    private static class Node implements Comparable<Node> {

        private final HeatMap heatMap;
        private final Point location;
        private final Point direction; // dir from previous to this node
        private final int straightDistance;

        private int cost = Integer.MAX_VALUE;

        public Node(HeatMap heatMap, Point location, Point direction, int straightDistance) {
            this.heatMap = heatMap;
            this.location = location;
            this.direction = direction;
            this.straightDistance = straightDistance;
        }

        public boolean canStop() {
            return !heatMap.hasMinStraightDistance() || straightDistance >= heatMap.getMinStraightDistance();
        }

        public Stream<Point> calculateNeighborDirections() {
            if (direction.x == 0 && direction.y == 0) {
                return Stream.of(
                        new Point(-1, 0),
                        new Point(0, -1),
                        new Point(1, 0),
                        new Point(0, 1)
                );
            } else if (heatMap.hasMinStraightDistance() && straightDistance < heatMap.getMinStraightDistance()) {
                return Stream.of(
                        direction
                );
            } else if (heatMap.hasMaxStraightDistance() && straightDistance < heatMap.getMaxStraightDistance()) {
                return Stream.of(
                        direction,
                        direction.cw(),
                        direction.ccw()
                );
            } else {
                return Stream.of(
                        direction.cw(),
                        direction.ccw()
                );
            }
        }

        public Stream<Node> calculateNeighbors() {
            return calculateNeighborDirections()
                    .map(direction -> {
                        int neighborStraightDistance = 1;
                        if (direction.equals(this.direction)) {
                            // Going straight!
                            neighborStraightDistance = straightDistance + 1;
                        }

                        return new Node(heatMap, location.add(direction), direction, neighborStraightDistance);
                    });
        }

        public Point getLocation() {
            return location;
        }

        public int getStraightDistance() {
            return straightDistance;
        }

        public int getCost() {
            return cost;
        }

        public void setCost(int cost) {
            this.cost = cost;
        }

        @Override
        public int compareTo(Node node) {
            return this.cost - node.cost;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Node node = (Node) o;
            return straightDistance == node.straightDistance && Objects.equals(location, node.location) && Objects.equals(direction, node.direction);
        }

        @Override
        public int hashCode() {
            return Objects.hash(location, direction, straightDistance);
        }
    }

    private static class HeatMap {

        private final int[] values;
        private final int width;
        private final int height;
        private final int maxStraightDistance;
        private final int minStraightDistance;

        public HeatMap(int[] values, int width, int height, int minStraightDistance, int maxStraightDistance) {
            if (values.length != width * height) {
                throw new IllegalArgumentException();
            }

            this.values = values;
            this.width = width;
            this.height = height;
            this.minStraightDistance = minStraightDistance;
            this.maxStraightDistance = maxStraightDistance;
        }

        public HeatMap(int width, int height, int minStraightDistance, int maxStraightDistance) {
            this(new int[width * height], width, height, minStraightDistance, maxStraightDistance);
        }

        public boolean isInBounds(Point point) {
            return point.x >= 0 && point.x < width && point.y >= 0 && point.y < height;
        }

        public int getValue(Point point) {
            if (!isInBounds(point)) {
                throw new IllegalArgumentException();
            }

            return values[point.x + point.y * width];
        }

        public int getWidth() {
            return width;
        }

        public int getHeight() {
            return height;
        }

        public boolean hasMinStraightDistance() {
            return minStraightDistance > 0;
        }

        public int getMinStraightDistance() {
            return minStraightDistance;
        }

        public boolean hasMaxStraightDistance() {
            return maxStraightDistance > 0;
        }

        public int getMaxStraightDistance() {
            return maxStraightDistance;
        }
    }

    private static class Point {

        public final int x;
        public final int y;

        public Point(int x, int y) {
            this.x = x;
            this.y = y;
        }

        public Point add(Point delta) {
            return new Point(x + delta.x, y + delta.y);
        }

        public Point cw() {
            return new Point(-y, x);
        }

        public Point ccw() {
            return new Point(y, -x);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Point point = (Point) o;
            return x == point.x && y == point.y;
        }

        @Override
        public int hashCode() {
            return Objects.hash(x, y);
        }
    }
}
