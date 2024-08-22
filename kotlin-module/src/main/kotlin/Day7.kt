import util.common.Solver

fun main() {
    Day7SolverK("day7.txt").printResults()
}

class Day7SolverK(
    fileName: String,
) : Solver<Int>(fileName) {
    private val handList: List<Hand> = parseHands()

    private fun parseHands(): List<Hand> = puzzle.map { Hand.from(it) }

    override fun solvePartOne(): Int = calculateScore(handList.sorted())

    private fun calculateScore(hands: List<Hand>): Int = hands.indices.sumOf { (it + 1) * hands[it].bid }

    override fun solvePartTwo(): Int = calculateScore(handList.sortedWith(Hand.specialComparator))

    private data class Hand(
        val cards: List<Card>,
        val bid: Int,
    ) : Comparable<Hand> {
        companion object {
            val specialComparator = Comparator { hand1: Hand, hand2: Hand -> hand1.compare(hand2, true) }

            fun from(line: String): Hand {
                val (cardsStr, bidStr) = line.split(" ")
                val cardList = cardsStr.map { Card.from(it) }
                val bid = bidStr.toInt()
                return Hand(cardList, bid)
            }
        }

        override fun compareTo(other: Hand): Int = compare(other, false)

        fun compare(
            other: Hand,
            checkJoker: Boolean,
        ): Int {
            val thisType = getType(checkJoker)
            val otherType = other.getType(checkJoker)

            return when {
                thisType != otherType -> thisType.compareTo(otherType)
                else -> {
                    cards.indices.firstNotNullOfOrNull { i ->
                        val thisCard = cards[i]
                        val otherCard = other.cards[i]
                        when {
                            thisCard != otherCard -> {
                                if (checkJoker) {
                                    when {
                                        thisCard == Card.J -> -1
                                        otherCard == Card.J -> 1
                                        else -> thisCard.compareTo(otherCard)
                                    }
                                } else {
                                    thisCard.compareTo(otherCard)
                                }
                            }

                            else -> null
                        }
                    } ?: 0
                }
            }
        }

        fun getType(checkJoker: Boolean): HandType {
            val uniqueCards = cards.toSet()
            val uniqueCount = uniqueCards.size

            var type =
                when (uniqueCount) {
                    5 -> HandType.HIGH_CARD
                    4 -> HandType.ONE_PAIR
                    3 -> if (countPairs() == 2L) HandType.TWO_PAIR else HandType.THREE_OF_A_KIND
                    2 -> if (countPairs() == 1L) HandType.FULL_HOUSE else HandType.FOUR_OF_A_KIND
                    else -> HandType.FIVE_OF_A_KIND
                }

            if (checkJoker && cards.contains(Card.J)) {
                type = modifyTypeForJoker(type)
            }

            return type
        }

        fun countPairs(): Long = groupCards().values.count { it.size == 2 }.toLong()

        fun groupCards(): Map<Char, List<Card>> = cards.groupBy { it.label }

        fun modifyTypeForJoker(type: HandType): HandType =
            when (type) {
                HandType.HIGH_CARD -> HandType.ONE_PAIR
                HandType.ONE_PAIR -> HandType.THREE_OF_A_KIND
                HandType.TWO_PAIR -> if (hasJokerInPairs()) HandType.FOUR_OF_A_KIND else HandType.FULL_HOUSE
                HandType.FULL_HOUSE, HandType.FOUR_OF_A_KIND -> HandType.FIVE_OF_A_KIND
                HandType.THREE_OF_A_KIND -> HandType.FOUR_OF_A_KIND
                else -> type
            }

        fun hasJokerInPairs(): Boolean = groupCards().entries.any { (key, value) -> value.size == 2 && key == Card.J.label }
    }

    private enum class HandType {
        HIGH_CARD,
        ONE_PAIR,
        TWO_PAIR,
        THREE_OF_A_KIND,
        FULL_HOUSE,
        FOUR_OF_A_KIND,
        FIVE_OF_A_KIND,
    }

    private enum class Card(
        val label: Char,
    ) {
        TWO('2'),
        THREE('3'),
        FOUR('4'),
        FIVE('5'),
        SIX('6'),
        SEVEN('7'),
        EIGHT('8'),
        NINE('9'),
        T('T'),
        J('J'),
        Q('Q'),
        K('K'),
        A('A'),
        ;

        companion object {
            fun from(label: Char): Card =
                entries.firstOrNull { it.label == label }
                    ?: throw IllegalArgumentException("Illegal card label: $label")
        }
    }
}
