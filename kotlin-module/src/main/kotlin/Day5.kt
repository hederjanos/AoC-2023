import util.common.Solver

fun main() {
    Day5SolverK("day5.txt").printResults()
}

class Day5SolverK(
    fileName: String,
) : Solver<Long>(fileName) {
    private val seeds: List<Long> = "\\d+".toRegex().findAll(puzzle[0]).map { it.value.toLong() }.toList()
    private val rangeMaps: RangeMaps = RangeMaps.from(puzzle)

    override fun solvePartOne(): Long = seeds.minOf { rangeMaps.getLocationFrom(it) }

    override fun solvePartTwo(): Long {
        val reversedMaps = rangeMaps.reverse()

        val ranges = (0 until seeds.size - 1 step 2).map { j -> LongRange(seeds[j], seeds[j] + seeds[j + 1] - 1) }

        return generateSequence(0L, Long::inc)
            .first { testSrc ->
                val dest = reversedMaps.getLocationFrom(testSrc)
                ranges.any { range -> dest in range }
            }
    }

    private class RangeMaps(
        val rangeMaps: List<RangeMap>,
    ) {
        companion object {
            fun from(puzzle: List<String>): RangeMaps {
                val rangeMaps = mutableListOf<RangeMap>()
                var rangeMap: RangeMap? = null

                for (i in 2 until puzzle.size) {
                    if (puzzle[i].isEmpty()) {
                        rangeMap?.let { rangeMaps.add(it) }
                        continue
                    }
                    val alphabetic = puzzle[i][0].isLetter()
                    if (alphabetic) {
                        rangeMap = RangeMap(puzzle[i].split(" ")[0])
                    } else {
                        val nums = puzzle[i].split(" ").map { it.toLong() }
                        val (destStart, srcStart, range) = nums
                        rangeMap?.addRange(
                            LongRange(srcStart, srcStart + range - 1),
                            LongRange(destStart, destStart + range - 1),
                        )
                    }
                    if (i == puzzle.size - 1) {
                        rangeMap?.let { rangeMaps.add(it) }
                    }
                }
                return RangeMaps(rangeMaps)
            }
        }

        fun getLocationFrom(src: Long): Long =
            rangeMaps.fold(src) { dest, rangeMap ->
                rangeMap.findRangePair(dest)?.let { rangeMap.getDestination(it, dest) } ?: dest
            }

        fun reverse(): RangeMaps {
            val reversedMaps = rangeMaps.reversed()
            val flippedMaps =
                reversedMaps.map { rangeMap ->
                    val newRangeMap = RangeMap(rangeMap.name)
                    rangeMap.rangeMap.entries.forEach { (k, v) -> newRangeMap.addRange(v, k) }
                    newRangeMap
                }
            return RangeMaps(flippedMaps)
        }
    }

    private class RangeMap(
        val name: String,
    ) {
        val rangeMap = mutableMapOf<LongRange, LongRange>()

        fun addRange(
            src: LongRange,
            dest: LongRange,
        ) {
            rangeMap[src] = dest
        }

        fun findRangePair(number: Long): Map.Entry<LongRange, LongRange>? = rangeMap.entries.firstOrNull { (key, _) -> number in key }

        fun getDestination(
            rangePair: Map.Entry<LongRange, LongRange>,
            number: Long,
        ): Long {
            val (src, dest) = rangePair
            return dest.first + (number - src.first)
        }
    }
}
