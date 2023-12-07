package me.gritter.aoc2023;

import org.apache.commons.collections4.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.lang.Integer.parseInt;
import static org.apache.commons.lang3.StringUtils.*;

public class Day7 {

    public static void main(String[] args) {
        new Day7().solution("day7-puzzle.txt");
    }

    public void solution(String file) {
        List<Hand> sortedHands = Utils.readLines(file)
                .map(this::parseHand)
                .sorted()
                .collect(Collectors.toList());

        int winnings = 0;
        for (int i = 0; i < sortedHands.size(); i++) {
            Hand hand = sortedHands.get(i);
            int rank = i + 1;

            winnings += rank * hand.getBid();
        }

        System.out.println(winnings);
    }

    private Hand parseHand(String line) {
        List<Card> cards = Stream.of(substringBefore(line, " "))
                .flatMapToInt(String::chars)
                .mapToObj(c -> (char) c)
                .map(Card::bySymbol)
                .collect(Collectors.toList());

        int bid = parseInt(substringAfter(line, " "));

        return new Hand(cards, bid);
    }

    public enum Card {
        ACE('A', 13),
        KING('K', 12),
        QUEEN('Q', 11),
//        JACK('J', 10), // Star 1
        JOKER('J', -1), // Star 2
        T('T', 9),
        NINE('9', 8),
        EIGHT('8', 7),
        SEVEN('7', 6),
        SIX('6', 5),
        FIVE('5', 4),
        FOUR('4', 3),
        THREE('3', 2),
        TWO('2', 1);

        private final char symbol;
        private final int value;

        Card(char symbol, int value) {
            this.symbol = symbol;
            this.value = value;
        }

        public char symbol() {
            return symbol;
        }

        public int value() {
            return value;
        }

        public static Card bySymbol(char symbol) {
            for (Card card : values()) {
                if (card.symbol == symbol) {
                    return card;
                }
            }

            return null;
        }
    }

    public enum HandType {
        FIVE_OF_A_KIND(7),
        FOUR_OF_A_KIND(6),
        FULL_HOUSE(5),
        THREE_OF_A_KIND(4),
        TWO_PAIR(3),
        ONE_PAIR(2),
        HIGH_CARD(1);

        private final int strength;

        HandType(int strength) {
            this.strength = strength;
        }

        public int strength() {
            return strength;
        }
    }

    private static class Hand implements Comparable<Hand> {

        private final List<Card> cards;
        private final int bid;

        public Hand(List<Card> cards, int bid) {
            if (cards.size() != 5) {
                throw new IllegalStateException();
            }

            this.cards = Collections.unmodifiableList(cards);
            this.bid = bid;
        }

        public Collection<Card> getCards() {
            return cards;
        }

        public int getBid() {
            return bid;
        }

        public HandType calculateHandType() {
//            return calculateHandType_star1(); // Star 1
            return calculateHandType_star2(); // Star 2
        }

        private HandType calculateHandType_star2() {
            for (int i = 0; i < cards.size(); i++) {
                Card card = cards.get(i);
                if (card == Card.JOKER) {
                    HandType highestHandType = null;

                    for (Card replacementCard : Card.values()) {
                        if (replacementCard == Card.JOKER) continue;

                        List<Card> replacementCards = new ArrayList<>(cards);
                        replacementCards.set(i, replacementCard);
                        Hand replacementHand = new Hand(replacementCards, bid);
                        HandType replacementHandType = replacementHand.calculateHandType_star2();

                        if (highestHandType == null || replacementHandType.strength() > highestHandType.strength()) {
                            highestHandType = replacementHandType;
                        }
                    }

                    return highestHandType;
                }
            }

            return calculateHandType_star1();
        }

        public HandType calculateHandType_star1() {
            List<Integer> frequencies = cards.stream()
                    .distinct()
                    .map(card -> Collections.frequency(cards, card))
                    .collect(Collectors.toList());

            int highestFrequency = frequencies.stream()
                    .mapToInt(i -> i)
                    .max()
                    .orElseThrow();

            if (highestFrequency == 5) {
                return HandType.FIVE_OF_A_KIND;
            } else if (highestFrequency == 4) {
                return HandType.FOUR_OF_A_KIND;
            } else if (CollectionUtils.isEqualCollection(frequencies, List.of(2, 3))) {
                return HandType.FULL_HOUSE;
            } else if (highestFrequency == 3) {
                return HandType.THREE_OF_A_KIND;
            } else if (CollectionUtils.isEqualCollection(frequencies, List.of(2, 2, 1))) {
                return HandType.TWO_PAIR;
            } else if (highestFrequency == 2) {
                return HandType.ONE_PAIR;
            } else {
                return HandType.HIGH_CARD;
            }
        }

        @Override
        public String toString() {
            return "Hand{" +
                    "cards=" + cards +
                    ", bid=" + bid +
                    '}';
        }

        @Override
        public int compareTo(Hand other) {
            int selfStrength = this.calculateHandType().strength();
            int otherStrength = other.calculateHandType().strength();

            if (selfStrength != otherStrength) {
                return selfStrength - otherStrength;
            }else {
                for (int i = 0; i < Math.min(cards.size(), other.cards.size()); i++) {
                    int selfCard = cards.get(i).value();
                    int otherCard = other.cards.get(i).value();

                    if (selfCard != otherCard) {
                        return selfCard - otherCard;
                    }
                }

                throw new IllegalStateException("Can't compare cards: hand type and all cards are equal.");
            }
        }
    }
}
