import util.common.Solver

fun main() {
    Day4SolverK("day4.txt").printResults()
}

class Day4SolverK(
    fileName: String,
) : Solver<Int>(fileName) {
    private val cards: Array<Card> = puzzle.map(Card::from).toTypedArray()

    override fun solvePartOne(): Int = cards.sumOf { (1 shl (it.numberOfMatches - 1)) }

    override fun solvePartTwo(): Int {
        val counter = IntArray(cards.size) { 1 }
        counter.forEachIndexed { i, value ->
            (1..cards[i].numberOfMatches)
                .filter { i + it < counter.size }
                .forEach { counter[i + it] += value }
        }
        return counter.sum()
    }

    private class Card(
        val winningNums: Set<Int>,
        val ownedNums: Set<Int>,
    ) {
        companion object {
            fun from(line: String): Card {
                val (_, numsPart) = line.split(":", limit = 2)
                val (win, own) = numsPart.split("|", limit = 2)
                return Card(extractInts(win), extractInts(own))
            }

            private fun extractInts(nums: String): Set<Int> = "\\d+".toRegex().findAll(nums).map { it.value.toInt() }.toSet()
        }

        val numberOfMatches: Int get() = winningNums.intersect(ownedNums).size
    }
}
