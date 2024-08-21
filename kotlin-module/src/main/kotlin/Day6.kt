import util.common.Solver
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.sqrt

fun main() {
    Day6SolverK("day6.txt").printResults()
}

class Day6SolverK(
    fileName: String,
) : Solver<Long>(fileName) {
    private val raceInfoList: List<RaceInfo> = parseRaceList()

    private fun parseRaceList(): List<RaceInfo> =
        extractIntegers(puzzle.first())
            .zip(extractIntegers(puzzle.last())) { time, bestDistance ->
                RaceInfo(
                    time.toLong(),
                    bestDistance.toLong(),
                )
            }

    private fun extractIntegers(input: String): List<Int> = "\\d+".toRegex().findAll(input).map { it.value.toInt() }.toList()

    override fun solvePartOne(): Long = raceInfoList.map(::getNumberOfWaysToBeatRecord).fold(1L) { acc, value -> acc * value }

    private fun getNumberOfWaysToBeatRecord(raceInfo: RaceInfo): Long {
        val (time, bestDistance) = raceInfo
        val discriminant = time.toDouble().let { b -> b * b - 4 * bestDistance }

        if (discriminant < 0) throw IllegalStateException("Discriminant is negative, no real roots exist.")

        val sqrtDiscriminant = sqrt(discriminant)
        val root1 = ceil((-time + sqrtDiscriminant) / 2 * -1)
        val root2 = floor((-time - sqrtDiscriminant) / 2 * -1)

        return (root2 - root1 + 1).toLong()
    }

    override fun solvePartTwo(): Long = getNumberOfWaysToBeatRecord(parseRace())

    private fun parseRace(): RaceInfo =
        RaceInfo(
            extractIntegers(puzzle.first()).joinToString("").toLong(),
            extractIntegers(puzzle.last()).joinToString("").toLong(),
        )

    private data class RaceInfo(
        val time: Long,
        val bestDistance: Long,
    )
}
