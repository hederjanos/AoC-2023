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
        return puzzle.stream().map(this::parseHand).sorted().collect(Collectors.toList());
    }

    private Hand parseHand(String s) {
        String[] hand = s.split(" ");
        String cards = hand[0];
        int bid = Integer.parseInt(hand[1]);
        List<Card> cardList = IntStream.range(0, cards.length())
                .mapToObj(i -> Card.getCardByLabel(cards.charAt(i)))
                .collect(Collectors.toList());
        return new Hand(cardList, bid);
    }

    @Override
    public Integer solvePartOne() {
        return processHands(handList);
    }

    private int processHands(List<Hand> hands) {
        return IntStream.range(0, hands.size()).boxed().mapToInt(i -> (i + 1) * hands.get(i).bid).sum();
    }

    @Override
    public Integer solvePartTwo() {
        List<Hand> reOrderedHands = handList.stream().sorted(Hand.specialComparator).collect(Collectors.toList());
        return processHands(reOrderedHands);
    }

    private enum Card {
        TWO('2'), THREE('3'), FOUR('4'), FIVE('5'), SIX('6'), SEVEN('7'),
        EIGHT('8'), NINE('9'), T('T'), J('J'), Q('Q'), K('K'), A('A');

        final char label;

        Card(char label) {
            this.label = label;
        }

        static Card getCardByLabel(char label) {
            return Arrays.stream(Card.values()).filter(card -> card.label == label).findFirst().get();
        }
    }

    private static class Hand implements Comparable<Hand> {
        final static Comparator<Hand> specialComparator = (hand1, hand2) -> hand1.compare(hand2, true);
        List<Card> cards;
        int bid;

        Hand(List<Card> cards, int bid) {
            this.cards = cards;
            this.bid = bid;
        }

        @Override
        public int compareTo(Hand hand) {
            return this.compare(hand, false);
        }

        int compare(Hand hand, boolean checkJoker) {
            HandType type = this.getType(checkJoker);
            HandType otherType = hand.getType(checkJoker);

            if (type != otherType) {
                return Integer.compare(type.ordinal(), otherType.ordinal());
            } else {
                List<Card> cards1 = this.cards;
                List<Card> cards2 = hand.cards;

                int i = 0;
                while (i < cards1.size()) {
                    char label1 = cards1.get(i).label;
                    char label2 = cards2.get(i).label;

                    if (label1 != label2) {
                        if (checkJoker) {
                            if (label1 == Card.J.label) {
                                return -1;
                            } else if (label2 == Card.J.label) {
                                return 1;
                            }
                        }
                        return Integer.compare(cards1.get(i).ordinal(), cards2.get(i).ordinal());
                    }
                    i++;
                }
                return 0;
            }
        }

        HandType getType(boolean checkJoker) {
            HandType handType;
            Set<Card> cardSet = new HashSet<>(cards);
            if (cardSet.size() == cards.size()) {
                handType = HandType.HIGH_CARD;
            } else if (cardSet.size() == 4) {
                handType = HandType.ONE_PAIR;
            } else if (cardSet.size() == 3) {
                long count = groupCards().values().stream().filter(list -> list.size() == 2).count();
                handType = count == 2 ? HandType.TWO_PAIR : HandType.THREE_OF_A_KIND;
            } else if (cardSet.size() == 2) {
                long count = groupCards().values().stream().filter(list -> list.size() == 2).count();
                handType = count == 1 ? HandType.FULL_HOUSE : HandType.FOUR_OF_A_KIND;
            } else {
                handType = HandType.FIVE_OF_A_KIND;
            }
            if (checkJoker && cards.contains(Card.J)) {
                handType = modifyType(handType);
            }
            return handType;
        }

        private HandType modifyType(HandType handType) {
            switch (handType) {
                case HIGH_CARD:
                    handType = HandType.ONE_PAIR;
                    break;
                case ONE_PAIR:
                    handType = HandType.THREE_OF_A_KIND;
                    break;
                case TWO_PAIR:
                    Map<Character, List<Card>> cardGroups = groupCards();
                    List<Character> pairs = cardGroups.keySet().stream()
                            .filter(key -> cardGroups.get(key).size() == 2)
                            .collect(Collectors.toList());
                    if (pairs.stream().anyMatch(character -> character.equals(Card.J.label))) {
                        handType = HandType.FOUR_OF_A_KIND;
                    } else {
                        handType = HandType.FULL_HOUSE;
                    }
                    break;
                case FULL_HOUSE:
                case FOUR_OF_A_KIND:
                    handType = HandType.FIVE_OF_A_KIND;
                    break;
                case THREE_OF_A_KIND:
                    handType = HandType.FOUR_OF_A_KIND;
                    break;
                default:
            }
            return handType;
        }

        Map<Character, List<Card>> groupCards() {
            return cards.stream().collect(Collectors.groupingBy(card -> card.label));
        }
    }

    private enum HandType {
        HIGH_CARD, ONE_PAIR, TWO_PAIR, THREE_OF_A_KIND, FULL_HOUSE, FOUR_OF_A_KIND, FIVE_OF_A_KIND
    }
}
