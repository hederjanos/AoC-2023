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

    private class RangeMaps(val rangeMaps: List<RangeMap>) {
        companion object {
            fun from(puzzle: List<String>): RangeMaps {
                val rangeMaps = mutableListOf<RangeMap>()
                var rangeMap: RangeMap? = null

                for (i in 2 until puzzle.size) {
                    if (puzzle[i].isEmpty()) {
                        rangeMap?.let { rangeMaps.add(it) }
                        continue
                    }
                    if (puzzle[i].first().isLetter()) {
                        rangeMap = RangeMap(puzzle[i].substringBefore(" "))
                    } else {
                        val (destStart, srcStart, length) = puzzle[i].split(" ").map { it.toLong() }
                        rangeMap?.addMapping(
                            srcStart until (srcStart + length),
                            destStart until (destStart + length),
                        )
                    }
                    if (i == puzzle.size - 1) {
                        rangeMap?.let { rangeMaps.add(it) }
                    }
                }
                return RangeMaps(rangeMaps)
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
                    val newRangeMap = RangeMap(rangeMap.name)
                    for ((src, dest) in rangeMap.rangePairs) {
                        newRangeMap.addMapping(dest, src)
                    }
                    newRangeMap
                }
            return RangeMaps(flippedMaps)
        }
    }

    private class RangeMap(val name: String) {
        val rangePairs = mutableListOf<Pair<LongRange, LongRange>>()

        fun addMapping(src: LongRange, dest: LongRange) {
            rangePairs.add(src to dest)
        }

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