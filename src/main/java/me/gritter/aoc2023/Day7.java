package me.gritter.aoc2023;

import org.apache.commons.collections4.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.lang.Integer.parseInt;
import static java.util.Map.entry;
import static org.apache.commons.lang3.StringUtils.substringAfter;
import static org.apache.commons.lang3.StringUtils.substringBefore;

public class Day7 implements Solution {

    private static final Map<Character, Card> STAR_1_CARD_MAP = Map.ofEntries(
            entry('A', Card.ACE),
            entry('K', Card.KING),
            entry('Q', Card.QUEEN),
            entry('J', Card.JACK),
            entry('T', Card.TEN),
            entry('9', Card.NINE),
            entry('8', Card.EIGHT),
            entry('7', Card.SEVEN),
            entry('6', Card.SIX),
            entry('5', Card.FIVE),
            entry('4', Card.FOUR),
            entry('3', Card.THREE),
            entry('2', Card.TWO)
    );

    private static final Map<Character, Card> STAR_2_CARD_MAP = Map.ofEntries(
            entry('A', Card.ACE),
            entry('K', Card.KING),
            entry('Q', Card.QUEEN),
            entry('J', Card.JOKER),
            entry('T', Card.TEN),
            entry('9', Card.NINE),
            entry('8', Card.EIGHT),
            entry('7', Card.SEVEN),
            entry('6', Card.SIX),
            entry('5', Card.FIVE),
            entry('4', Card.FOUR),
            entry('3', Card.THREE),
            entry('2', Card.TWO)
    );

    public static void main(String[] args) {
        Solution solution = new Day7();
        System.out.println(solution.solution_star2("day7-puzzle.txt"));
    }

    @Override
    public long solution_star1(String file) {
        Collection<Hand> hands = Utils.readLines(file)
                .map(line -> parseHand(line, STAR_1_CARD_MAP))
                .collect(Collectors.toSet());

        return countWinnings(hands);
    }

    @Override
    public long solution_star2(String file) {
        Collection<Hand> hands = Utils.readLines(file)
                .map(line -> parseHand(line, STAR_2_CARD_MAP))
                .collect(Collectors.toSet());

        return countWinnings(hands);
    }

    private int countWinnings(Collection<Hand> hands) {
        List<Hand> sortedHands = new ArrayList<>(hands);
        Collections.sort(sortedHands);

        int winnings = 0;
        for (int i = 0; i < sortedHands.size(); i++) {
            Hand hand = sortedHands.get(i);
            int rank = i + 1;

            winnings += rank * hand.getBid();
        }

        return winnings;
    }

    private Hand parseHand(String line, Map<Character, Card> cardMap) {
        List<Card> cards = Stream.of(substringBefore(line, " "))
                .flatMapToInt(String::chars)
                .mapToObj(c -> (char) c)
                .map(cardMap::get)
                .map(Objects::requireNonNull)
                .collect(Collectors.toList());

        int bid = parseInt(substringAfter(line, " "));

        return new Hand(cards, bid);
    }

    public enum Card {
        JOKER(-1),

        ACE(14),
        KING(13),
        QUEEN(12),
        JACK(11),
        TEN(10),
        NINE(9),
        EIGHT(8),
        SEVEN(7),
        SIX(6),
        FIVE(5),
        FOUR(4),
        THREE(3),
        TWO(2);

        private final int value;

        Card(int value) {
            this.value = value;
        }

        public int value() {
            return value;
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
            for (int i = 0; i < cards.size(); i++) {
                Card card = cards.get(i);
                if (card == Card.JOKER) {
                    HandType highestHandType = null;

                    for (Card replacementCard : Card.values()) {
                        if (replacementCard == Card.JOKER) continue;

                        List<Card> replacementCards = new ArrayList<>(cards);
                        replacementCards.set(i, replacementCard);
                        Hand replacementHand = new Hand(replacementCards, bid);
                        HandType replacementHandType = replacementHand.calculateHandType();

                        if (highestHandType == null || replacementHandType.strength() > highestHandType.strength()) {
                            highestHandType = replacementHandType;
                        }
                    }

                    return highestHandType;
                }
            }

            return calculatePureHandType();
        }

        private HandType calculatePureHandType() {
            if (cards.stream().anyMatch(Card.JOKER::equals)) {
                throw new IllegalStateException();
            }

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
            } else {
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
