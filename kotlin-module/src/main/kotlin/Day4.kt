import util.common.Solver

fun main() {
    Day4SolverK("day4.txt").printResults()
}

class Day4SolverK(fileName: String) : Solver<Int>(fileName) {
    private val cards: Array<Card> = puzzle.map { Card.from(it) }.toTypedArray()

    override fun solvePartOne(): Int = cards.sumOf { if (it.matches == 0) 0 else 1 shl (it.matches - 1) }

    override fun solvePartTwo(): Int {
        val counter = IntArray(cards.size) { 1 }

        for (i in counter.indices) {
            val matches = cards[i].matches
            for (j in 1..matches) {
                if (i + j < counter.size) {
                    counter[i + j] += counter[i]
                }
            }
        }
        return counter.sum()
    }

    @JvmInline
    value class Card(val matches: Int) {
        companion object {
            private val NUMBER_PATTERN = "\\d+".toRegex()

            fun from(line: String): Card {
                val (_, numsPart) = line.split(":", limit = 2)
                val (win, own) = numsPart.split("|", limit = 2)

                val winningNums = extractInts(win)
                val ownedNums = extractInts(own)

                return Card(winningNums.intersect(ownedNums).size)
            }

            private fun extractInts(nums: String): Set<Int> =
                NUMBER_PATTERN.findAll(nums).map { it.value.toInt() }.toSet()
        }
    }
}