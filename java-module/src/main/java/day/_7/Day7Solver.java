package day._7;

import util.common.Solver;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Day7Solver extends Solver<Integer> {
    private final List<Hand> handList;

    public Day7Solver(String fileName) {
        super(fileName);
        handList = parseHands();
    }

    private List<Hand> parseHands() {
        return puzzle.stream().map(Hand::from).collect(Collectors.toList());
    }

    @Override
    public Integer solvePartOne() {
        Collections.sort(handList);
        return calculateScore(handList);
    }

    private int calculateScore(List<Hand> hands) {
        return IntStream.range(0, hands.size()).boxed().mapToInt(i -> (i + 1) * hands.get(i).bid).sum();
    }

    @Override
    public Integer solvePartTwo() {
        List<Hand> reOrderedHands = handList.stream().sorted(Hand.specialComparator).toList();
        return calculateScore(reOrderedHands);
    }

    record Hand(List<Card> cards, int bid) implements Comparable<Hand> {
        final static Comparator<Hand> specialComparator = (hand1, hand2) -> hand1.compare(hand2, true);

        static Hand from(String line) {
            String[] hand = line.split(" ");
            List<Card> cardList = hand[0].chars().mapToObj(c -> Card.from((char) c)).toList();
            int bid = Integer.parseInt(hand[1]);
            return new Hand(cardList, bid);
        }

        public int compareTo(Hand hand) {
            return this.compare(hand, false);
        }

        int compare(Hand other, boolean checkJoker) {
            HandType thisType = this.getType(checkJoker);
            HandType otherType = other.getType(checkJoker);

            if (thisType != otherType) {
                return thisType.compareTo(otherType);
            }

            for (int i = 0; i < cards.size(); i++) {
                Card thisCard = cards.get(i);
                Card otherCard = other.cards.get(i);

                if (thisCard != otherCard) {
                    if (checkJoker) {
                        if (thisCard == Card.J) {
                            return -1;
                        }
                        if (otherCard == Card.J) {
                            return 1;
                        }
                    }
                    return thisCard.compareTo(otherCard);
                }
            }
            return 0;
        }

        HandType getType(boolean checkJoker) {
            Set<Card> uniqueCards = new HashSet<>(cards);
            int uniqueCount = uniqueCards.size();

            HandType type = switch (uniqueCount) {
                case 5 -> HandType.HIGH_CARD;
                case 4 -> HandType.ONE_PAIR;
                case 3 -> countPairs() == 2 ? HandType.TWO_PAIR : HandType.THREE_OF_A_KIND;
                case 2 -> countPairs() == 1 ? HandType.FULL_HOUSE : HandType.FOUR_OF_A_KIND;
                default -> HandType.FIVE_OF_A_KIND;
            };

            if (checkJoker && cards.contains(Card.J)) {
                type = modifyTypeForJoker(type);
            }

            return type;
        }

        long countPairs() {
            return groupCards().values().stream().filter(list -> list.size() == 2).count();
        }

        Map<Character, List<Card>> groupCards() {
            return cards.stream().collect(Collectors.groupingBy(card -> card.label));
        }

        HandType modifyTypeForJoker(HandType type) {
            return switch (type) {
                case HIGH_CARD -> HandType.ONE_PAIR;
                case ONE_PAIR -> HandType.THREE_OF_A_KIND;
                case TWO_PAIR -> hasJokerInPairs() ? HandType.FOUR_OF_A_KIND : HandType.FULL_HOUSE;
                case FULL_HOUSE, FOUR_OF_A_KIND -> HandType.FIVE_OF_A_KIND;
                case THREE_OF_A_KIND -> HandType.FOUR_OF_A_KIND;
                default -> type;
            };
        }

        boolean hasJokerInPairs() {
            Map<Character, List<Card>> cardGroups = groupCards();
            return cardGroups.entrySet().stream()
                    .filter(entry -> entry.getValue().size() == 2)
                    .anyMatch(entry -> entry.getKey() == Card.J.label);
        }
    }

    private enum HandType {
        HIGH_CARD, ONE_PAIR, TWO_PAIR, THREE_OF_A_KIND, FULL_HOUSE, FOUR_OF_A_KIND, FIVE_OF_A_KIND
    }

    private enum Card {
        TWO('2'), THREE('3'), FOUR('4'), FIVE('5'), SIX('6'), SEVEN('7'),
        EIGHT('8'), NINE('9'), T('T'), J('J'), Q('Q'), K('K'), A('A');

        final char label;

        Card(char label) {
            this.label = label;
        }

        static Card from(char label) {
            return Arrays.stream(Card.values())
                    .filter(card -> card.label == label)
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("Illegal card label: " + label));
        }
    }
}
