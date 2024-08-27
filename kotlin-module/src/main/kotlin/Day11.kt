import util.common.Solver
import kotlin.math.abs

fun main() {
    Day11SolverK("day11.txt").printResults()
}

class Day11SolverK(
    fileName: String,
) : Solver<Long>(fileName) {
    private val universe: Universe = Universe.from(puzzle)

    override fun solvePartOne(): Long = universe.expand(2).calculateSumOfTheShortestPaths()

    override fun solvePartTwo(): Long = universe.expand(1_000_000).calculateSumOfTheShortestPaths()

    private class Universe(
        private val galaxies: List<Coordinate>,
    ) {
        private var minX: Long = Long.MAX_VALUE
        private var maxX: Long = Long.MIN_VALUE
        private var minY: Long = Long.MAX_VALUE
        private var maxY: Long = Long.MIN_VALUE

        init {
            for (c in galaxies) {
                minX = minOf(minX, c.x)
                maxX = maxOf(maxX, c.x)
                minY = minOf(minY, c.y)
                maxY = maxOf(maxY, c.y)
            }
        }

        companion object {
            fun from(puzzle: List<String>): Universe {
                val galaxies =
                    puzzle.flatMapIndexed { y, row ->
                        row.mapIndexedNotNull { x, char ->
                            if (char == '#') Coordinate(x.toLong(), y.toLong()) else null
                        }
                    }
                return Universe(galaxies)
            }
        }

        private val width: Long get() = maxX - minX

        private val length: Long get() = maxY - minY

        fun expand(expansion: Int): Universe {
            val xs = galaxies.map { it.x }.toSet()
            val ys = galaxies.map { it.y }.toSet()

            val emptyXs = (0 until width).filterNot { it in xs }
            val emptyYs = (0 until length).filterNot { it in ys }

            val newGalaxies =
                galaxies.map { c ->
                    val countX = emptyXs.count { it < c.x }
                    val countY = emptyYs.count { it < c.y }
                    Coordinate(c.x + countX * (expansion - 1), c.y + countY * (expansion - 1))
                }
            return Universe(newGalaxies)
        }

        fun calculateSumOfTheShortestPaths(): Long =
            galaxies
                .flatMapIndexed { i, current ->
                    galaxies.drop(i + 1).map { other ->
                        abs(current.x - other.x) + abs(current.y - other.y)
                    }
                }.sum()
    }

    private data class Coordinate(
        val x: Long,
        val y: Long,
    )
}
