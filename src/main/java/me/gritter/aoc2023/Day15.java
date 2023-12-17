package me.gritter.aoc2023;

import static java.lang.Integer.parseInt;
import static org.apache.commons.lang3.StringUtils.substringAfter;
import static org.apache.commons.lang3.StringUtils.substringBefore;

public class Day15 implements Solution {

    public static void main(String[] args) {
        Solution solution = new Day15();
        System.out.println(solution.solution_star2("day15-puzzle.txt"));
    }

    @Override
    public long solution_star1(String file) {
        int sum = 0;
        String[] parts = Utils.readFile(file).split(",");
        for (String part : parts) {
            sum += hash(part);
        }

        return sum;
    }

    @Override
    public long solution_star2(String file) {
        AocHashMap map = new AocHashMap();

        String[] parts = Utils.readFile(file).split(",");
        for (String part : parts) {
            if (part.contains("=")) {
                String label = substringBefore(part, "=");
                int focalLength = parseInt(substringAfter(part, "="));

                map.set(label, focalLength);
            } else if(part.endsWith("-")) {
                String label = substringBefore(part, "-");

                map.remove(label);
            }
        }

        return map.calculateFocusingPower();
    }

    private static class AocHashMap {

        private final AocLinkedList[] lists;

        public AocHashMap() {
            lists = new AocLinkedList[256];
            for (int i = 0; i < lists.length; i++) {
                lists[i] = new AocLinkedList();
            }
        }

        public void set(String key, int value) {
            int hash = hash(key);
            AocLinkedList list = lists[hash];
            list.set(key, value);
        }

        public void remove(String key) {
            int hash = hash(key);
            AocLinkedList list = lists[hash];
            list.remove(key);
        }

        public int get(String key) {
            int hash = hash(key);
            AocLinkedList list = lists[hash];
            return list.get(key);
        }

        public int calculateFocusingPower() {
            int power = 0;

            for (int i = 0; i < lists.length; i++) {
                power += (i + 1) * lists[i].calculateFocusingPower();
            }

            return power;
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();

            boolean first = true;
            for (int i = 0; i < lists.length; i++) {
                if (!lists[i].isEmpty()) {
                    if (!first) {
                        sb.append('\n');
                    }

                    first = false;

                    sb.append("Box ");
                    sb.append(String.valueOf(i));
                    sb.append(": ");
                    sb.append(lists[i].toString());
                }
            }

            return sb.toString();
        }
    }

    private static class AocLinkedList {

        private AocLinkedListNode first = null;

        public void set(String key, int value) {
            AocLinkedListNode current = first;
            while (current != null) {
                if (current.key.equals(key)) {
                    current.value = value;
                    return;
                }

                current = current.next;
            }

            if (first == null) {
                first = new AocLinkedListNode(key, value);
            } else {
                AocLinkedListNode last = first;
                while (last.next != null) {
                    last = last.next;
                }

                last.next = new AocLinkedListNode(key, value);
            }
        }

        public void remove(String key) {
            if (first == null) {
                return;
            }

            if (first.key.equals(key)) {
                first = first.next;
            } else {
                AocLinkedListNode previous = first;
                AocLinkedListNode current = first.next;
                while (current != null && !current.key.equals(key)) {
                    previous = current;
                    current = current.next;
                }

                if (current != null) {
                    previous.next = current.next;
                }
            }
        }

        public int get(String key) {
            AocLinkedListNode current = first;
            while (current != null) {
                if (current.key.equals(key)) {
                    return current.value;
                }

                current = current.next;
            }

            return -1;
        }

        public int calculateFocusingPower() {
            int power = 0;

            int i = 0;
            AocLinkedListNode current = first;
            while (current != null) {
                power += current.value * (i + 1);
                current = current.next;
                i++;
            }

            return power;
        }

        public boolean isEmpty() {
            return first == null;
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();

            AocLinkedListNode current = first;
            while (current != null) {
                sb.append('[');
                sb.append(current.toString());
                sb.append(']');

                current = current.next;
                if (current != null) {
                    sb.append(' ');
                }
            }

            return sb.toString();
        }
    }

    private static class AocLinkedListNode {

        public final String key;
        public int value;
        public AocLinkedListNode next;

        public AocLinkedListNode(String key, int value) {
            this.key = key;
            this.value = value;
        }

        @Override
        public String toString() {
            return key + " " + value;
        }
    }

    private static int hash(String string) {
        int hash = 0;

        for (char c : string.toCharArray()) {
            if (c == '\n') {
                continue;
            }

            hash += c;
            hash *= 17;
            hash %= 256;
        }

        return hash;
    }
}
