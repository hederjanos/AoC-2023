import util.common.Solver
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.sqrt

fun main() {
    Day6SolverK("day6.txt").printResults()
}

class Day6SolverK(fileName: String) : Solver<Long>(fileName) {

    override fun solvePartOne(): Long =
        RaceInfo.parseMultiple(puzzle)
            .map { it.calculateWaysToBeatRecord() }
            .fold(1L) { acc, value -> acc * value }

    override fun solvePartTwo(): Long = RaceInfo.parseCombined(puzzle).calculateWaysToBeatRecord()

    private class RaceInfo(private val time: Long, private val bestDistance: Long) {

        companion object {
            private val NUMBER_PATTERN = "\\d+".toRegex()

            fun parseMultiple(puzzle: List<String>): List<RaceInfo> {
                val times = extractNumbers(puzzle.first())
                val distances = extractNumbers(puzzle.last())

                return times.zip(distances) { time, distance -> RaceInfo(time, distance) }
            }

            fun parseCombined(puzzle: List<String>): RaceInfo {
                val time = extractCombinedNumber(puzzle.first())
                val bestDistance = extractCombinedNumber(puzzle.last())
                return RaceInfo(time, bestDistance)
            }

            private fun extractNumbers(input: String): List<Long> =
                NUMBER_PATTERN.findAll(input).map { it.value.toLong() }.toList()

            private fun extractCombinedNumber(input: String): Long =
                input.replace("\\D".toRegex(), "").toLong()
        }

        fun calculateWaysToBeatRecord(): Long {
            val a = 1.0
            val b = -time.toDouble()
            val c = bestDistance.toDouble()

            val discriminant = b * b - 4 * a * c

            if (discriminant < 0) {
                error("Discriminant is negative, no real roots exist.")
            }

            val sqrtDiscriminant = sqrt(discriminant)

            var root1 = (-b - sqrtDiscriminant) / (2 * a)
            var root2 = (-b + sqrtDiscriminant) / (2 * a)

            root1 = if (root1 == floor(root1)) root1 + 1 else ceil(root1)
            root2 = if (root2 == ceil(root2)) root2 - 1 else floor(root2)

            return (root2 - root1 + 1).toLong()
        }
    }
}