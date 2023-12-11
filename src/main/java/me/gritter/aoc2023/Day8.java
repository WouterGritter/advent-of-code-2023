package me.gritter.aoc2023;

import org.apache.commons.math3.util.ArithmeticUtils;

import java.util.*;
import java.util.stream.Collectors;

import static java.util.Objects.requireNonNull;
import static java.util.function.Function.identity;
import static org.apache.commons.lang3.StringUtils.substringBefore;
import static org.apache.commons.lang3.StringUtils.substringBetween;

public class Day8 implements Solution {

    public static void main(String[] args) {
        Solution solution = new Day8();
        System.out.println(solution.solution_star2("day8-puzzle.txt"));
    }

    @Override
    public long solution_star1(String file) {
        Network network = loadNetwork(file, Star1NetworkNode::new);

        return network.getNodes()
                .stream()
                .filter(NetworkNode::isStartingNode)
                .map(node -> new Walker(network, node))
                .map(Walker::walkUntilEnd)
                .reduce(this::reduceLeastCommonDivider)
                .orElseThrow();
    }

    @Override
    public long solution_star2(String file) {
        Network network = loadNetwork(file, Star2NetworkNode::new);

        return network.getNodes()
                .stream()
                .filter(NetworkNode::isStartingNode)
                .map(node -> new Walker(network, node))
                .map(Walker::walkUntilEnd)
                .reduce(this::reduceLeastCommonDivider)
                .orElseThrow();
    }

    private long reduceLeastCommonDivider(long a, long b) {
        return a * b / ArithmeticUtils.gcd(a, b);
    }

    private Network loadNetwork(String file, NetworkNodeFactory networkNodeFactory) {
        Collection<String> lines = Utils.readLines(file).collect(Collectors.toList());

        Sequence sequence = lines.stream()
                .findFirst()
                .map(this::parseSequence)
                .orElseThrow();

        Collection<NetworkNode> nodes = parseNetworkNodes(lines, networkNodeFactory);

        return new Network(sequence, nodes);
    }

    private Sequence parseSequence(String line) {
        List<Step> steps = line.chars()
                .mapToObj(c -> (char) c)
                .map(Step::bySymbol)
                .collect(Collectors.toList());

        return new Sequence(steps);
    }

    private Collection<NetworkNode> parseNetworkNodes(Collection<String> lines, NetworkNodeFactory networkNodeFactory) {
        // Build empty nodes
        Map<String, NetworkNode> nodes = lines.stream()
                .skip(2)
                .map(line -> substringBefore(line, " = "))
                .map(networkNodeFactory::build)
                .collect(Collectors.toMap(NetworkNode::getName, identity()));

        // Add connections to nodes
        lines.stream()
                .skip(2)
                .forEach(line -> {
                    NetworkNode node = requireNonNull(nodes.get(substringBefore(line, " = ")));
                    NetworkNode left = requireNonNull(nodes.get(substringBetween(line, "(", ",")));
                    NetworkNode right = requireNonNull(nodes.get(substringBetween(line, ", ", ")")));

                    node.addStep(Step.LEFT, left);
                    node.addStep(Step.RIGHT, right);
                });

        return nodes.values();
    }

    public static class Network {

        private final Sequence sequence;
        private final Collection<NetworkNode> nodes;

        public Network(Sequence sequence, Collection<NetworkNode> nodes) {
            this.sequence = sequence;
            this.nodes = Collections.unmodifiableCollection(nodes);
        }

        public Sequence getSequence() {
            return sequence;
        }

        public Collection<NetworkNode> getNodes() {
            return nodes;
        }
    }

    public static class Sequence {

        private final List<Step> steps;

        public Sequence(List<Step> steps) {
            this.steps = steps;
        }

        public Step get(long stepNumber) {
            if (stepNumber < 0) {
                throw new IllegalArgumentException();
            }

            return steps.get((int) (stepNumber % steps.size()));
        }
    }

    public enum Step {
        LEFT('L'), RIGHT('R');

        private final char symbol;

        Step(char symbol) {
            this.symbol = symbol;
        }

        public char symbol() {
            return this.symbol;
        }

        public static Step bySymbol(char symbol) {
            for (Step step : values()) {
                if (step.symbol() == symbol) {
                    return step;
                }
            }

            throw new IllegalArgumentException();
        }
    }

    public abstract static class NetworkNode {

        private final String name;
        private final Map<Step, NetworkNode> steps = new HashMap<>();

        public NetworkNode(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public void addStep(Step step, NetworkNode connectedNode) {
            steps.put(step, connectedNode);
        }

        public NetworkNode getStep(Step step) {
            return steps.get(step);
        }

        public abstract boolean isStartingNode();

        public abstract boolean isEndingNode();
    }

    public static class Star1NetworkNode extends NetworkNode {

        public Star1NetworkNode(String name) {
            super(name);
        }

        @Override
        public boolean isStartingNode() {
            return getName().equals("AAA");
        }

        @Override
        public boolean isEndingNode() {
            return getName().equals("ZZZ");
        }
    }

    public static class Star2NetworkNode extends NetworkNode {

        public Star2NetworkNode(String name) {
            super(name);
        }

        @Override
        public boolean isStartingNode() {
            return getName().endsWith("A");
        }

        @Override
        public boolean isEndingNode() {
            return getName().endsWith("Z");
        }
    }

    @FunctionalInterface
    public interface NetworkNodeFactory {

        NetworkNode build(String name);
    }

    public static class Walker {
        private final Network network;
        private final NetworkNode first;

        private NetworkNode current;
        private long stepNumber;

        public Walker(Network network, NetworkNode first) {
            this.network = network;
            this.first = first;

            reset();
        }

        public void reset() {
            current = first;
            stepNumber = 0;
        }

        public long walkUntilEnd() {
            reset();

            while (!isAtEnd()) {
                walkSingle();
            }

            return stepNumber;
        }

        public void walkSingle() {
            if (isAtEnd()) {
                throw new IllegalStateException();
            }

            Step step = network.getSequence().get(stepNumber++);
            current = current.getStep(step);
        }

        public boolean isAtEnd() {
            return current.isEndingNode();
        }

        public Network getNetwork() {
            return network;
        }

        public NetworkNode getFirst() {
            return first;
        }

        public NetworkNode getCurrent() {
            return current;
        }

        public long getStepNumber() {
            return stepNumber;
        }
    }
}
