import util.common.Solver

fun main() {
    Day5SolverK("day5.txt").printResults()
}

class Day5SolverK(fileName: String) : Solver<Long>(fileName) {
    private val seeds: List<Long> = "\\d+".toRegex().findAll(puzzle.first()).map { it.value.toLong() }.toList()
    private val rangeMaps: RangeMaps = RangeMaps.from(puzzle)

    override fun solvePartOne(): Long = seeds.minOf { rangeMaps.getLocationFrom(it) }

    override fun solvePartTwo(): Long {
        val reversedMaps = rangeMaps.reverse()

        val seedRanges = (seeds.indices step 2).map { j ->
            seeds[j] until (seeds[j] + seeds[j + 1])
        }.toTypedArray()

        var testSrc = 0L
        while (true) {
            val dest = reversedMaps.getLocationFrom(testSrc)

            for (range in seedRanges) {
                if (dest in range) {
                    return testSrc
                }
            }
            testSrc++
        }
    }

    private class RangeMaps(private val rangeMaps: List<RangeMap>) {
        companion object {
            fun from(puzzle: List<String>): RangeMaps {
                val rangeMaps = mutableListOf<RangeMap>()
                var currentName: String? = null
                var currentPairs = mutableListOf<Pair<LongRange, LongRange>>()

                for (i in 2 until puzzle.size) {
                    if (puzzle[i].isEmpty()) {
                        currentName?.let { rangeMaps.add(RangeMap(it, currentPairs.toList())) }
                        currentName = null
                        currentPairs = mutableListOf()
                        continue
                    }
                    if (puzzle[i].first().isLetter()) {
                        currentName = puzzle[i].substringBefore(" ")
                    } else {
                        val (destStart, srcStart, length) = puzzle[i].split(" ").map { it.toLong() }
                        currentPairs.add(
                            (srcStart until (srcStart + length)) to (destStart until (destStart + length))
                        )
                    }
                    if (i == puzzle.size - 1) {
                        currentName?.let { rangeMaps.add(RangeMap(it, currentPairs.toList())) }
                    }
                }
                return RangeMaps(rangeMaps.toList())
            }
        }

        fun getLocationFrom(src: Long): Long {
            var current = src
            for (rangeMap in rangeMaps) {
                current = rangeMap.getDestination(current)
            }
            return current
        }

        fun reverse(): RangeMaps {
            val flippedMaps = rangeMaps.reversed()
                .map { rangeMap ->
                    val flippedPairs = rangeMap.rangePairs.map { (src, dest) -> dest to src }
                    RangeMap(rangeMap.name, flippedPairs)
                }
            return RangeMaps(flippedMaps)
        }
    }

    private data class RangeMap(val name: String, val rangePairs: List<Pair<LongRange, LongRange>>) {
        fun getDestination(number: Long): Long {
            for ((src, dest) in rangePairs) {
                if (number in src) {
                    return dest.first + (number - src.first)
                }
            }
            return number
        }
    }
}