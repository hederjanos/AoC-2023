import util.common.Solver

fun main() {
    Day9SolverK("day9.txt").printResults()
}

class Day9SolverK(
    fileName: String,
) : Solver<Long>(fileName) {
    private val histories: List<List<Long>> = puzzle.map { line -> line.split(" ").map { it.toLong() } }

    override fun solvePartOne(): Long = histories.sumOf { processHistory(it) }

    private fun processHistory(history: List<Long>): Long = calculateDiffLists(history).sumOf { it.last() }

    private fun calculateDiffLists(history: List<Long>): List<List<Long>> {
        val diffLists = mutableListOf(history)
        var currentHistory = history
        while (!isAllZero(currentHistory)) {
            val diffs = currentHistory.windowed(2, 1).map { it[1] - it[0] }
            diffLists.add(diffs)
            currentHistory = diffs
        }
        return diffLists
    }

    private fun isAllZero(history: List<Long>): Boolean = history.all { it == 0L }

    override fun solvePartTwo(): Long = histories.map { it.reversed() }.sumOf { processHistory(it) }
}
