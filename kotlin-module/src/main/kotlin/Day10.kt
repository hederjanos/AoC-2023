import util.common.Solver
import util.coordinate.Coordinate

fun main() {
    Day10SolverK("day10.txt").printResults()
}

class Day10SolverK(
    fileName: String,
) : Solver<Int>(fileName) {
    private val maze = Maze.from(puzzle)

    override fun solvePartOne(): Int = maze.lengthOfLoop() / 2

    override fun solvePartTwo(): Int = maze.areaOfLoop()

    private class Maze(
        private val width: Int,
        private val height: Int,
        start: Coordinate,
        private val pipeMap: Map<Coordinate, Char>,
    ) {
        private val startPipe: Pipe = initStartPipe(start)
        private val loop: Set<Coordinate> = findLoop()

        companion object {
            private const val V = '|'
            private const val H = '-'
            private const val L = 'L'
            private const val J = 'J'
            private const val T = '7'
            private const val F = 'F'
            private const val G = '.'
            private const val S = 'S'

            fun from(puzzle: List<String>): Maze {
                val width = puzzle[0].length
                val height = puzzle.size
                var start: Coordinate? = null
                val characterMap = mutableMapOf<Coordinate, Char>()

                puzzle.forEachIndexed { i, row ->
                    row.forEachIndexed { j, ch ->
                        val coordinate = Coordinate(j, i)
                        characterMap[coordinate] = ch
                        if (ch == S) start = coordinate
                    }
                }

                return Maze(
                    width,
                    height,
                    start ?: throw IllegalArgumentException("Start coordinate not found"),
                    characterMap,
                )
            }
        }

        fun initStartPipe(start: Coordinate): Pipe =
            listOf(V, H, L, J, T, F, G, S)
                .map { Pipe(start, it) }
                .firstOrNull { isValidPipe(it) }
                ?: throw IllegalStateException("No valid starting pipe found")

        fun isValidPipe(pipe: Pipe): Boolean {
            val connectors = getNeighbourCoordinates(pipe)
            return if (connectors.isNotEmpty() && connectors.all { isCoordinateInBounds(it) }) {
                connectors.all { coordinate ->
                    val ch = pipeMap[coordinate] ?: return false
                    when (pipe.ch) {
                        V ->
                            ch == V ||
                                coordinate.y < pipe.coordinate.y &&
                                (ch == T || ch == F) ||
                                coordinate.y > pipe.coordinate.y &&
                                (ch == L || ch == J) ||
                                ch == S

                        H ->
                            ch == H ||
                                coordinate.x < pipe.coordinate.x &&
                                (ch == L || ch == F) ||
                                coordinate.x > pipe.coordinate.x &&
                                (ch == J || ch == T) ||
                                ch == S

                        L ->
                            coordinate.y < pipe.coordinate.y &&
                                (ch == V || ch == F || ch == T) ||
                                coordinate.x > pipe.coordinate.x &&
                                (ch == H || ch == J || ch == T) ||
                                ch == S

                        J ->
                            coordinate.x < pipe.coordinate.x &&
                                (ch == H || ch == L || ch == F) ||
                                coordinate.y < pipe.coordinate.y &&
                                (ch == V || ch == F || ch == T) ||
                                ch == S

                        F ->
                            coordinate.y > pipe.coordinate.y &&
                                (ch == V || ch == L || ch == J) ||
                                coordinate.x > pipe.coordinate.x &&
                                (ch == H || ch == J || ch == T) ||
                                ch == S

                        T ->
                            coordinate.x < pipe.coordinate.x &&
                                (ch == H || ch == F || ch == L) ||
                                coordinate.y > pipe.coordinate.y &&
                                (ch == V || ch == L || ch == J) ||
                                ch == S

                        G -> true
                        else -> throw IllegalStateException("Unexpected pipe type: ${pipe.ch}")
                    }
                }
            } else {
                false
            }
        }

        fun getNeighbourCoordinates(pipe: Pipe): Set<Coordinate> =
            when (pipe.ch) {
                V ->
                    setOf(
                        Coordinate(pipe.coordinate.x, pipe.coordinate.y - 1),
                        Coordinate(pipe.coordinate.x, pipe.coordinate.y + 1),
                    )

                H ->
                    setOf(
                        Coordinate(pipe.coordinate.x - 1, pipe.coordinate.y),
                        Coordinate(pipe.coordinate.x + 1, pipe.coordinate.y),
                    )

                L ->
                    setOf(
                        Coordinate(pipe.coordinate.x, pipe.coordinate.y - 1),
                        Coordinate(pipe.coordinate.x + 1, pipe.coordinate.y),
                    )

                J ->
                    setOf(
                        Coordinate(pipe.coordinate.x, pipe.coordinate.y - 1),
                        Coordinate(pipe.coordinate.x - 1, pipe.coordinate.y),
                    )

                T ->
                    setOf(
                        Coordinate(pipe.coordinate.x, pipe.coordinate.y + 1),
                        Coordinate(pipe.coordinate.x - 1, pipe.coordinate.y),
                    )

                F ->
                    setOf(
                        Coordinate(pipe.coordinate.x, pipe.coordinate.y + 1),
                        Coordinate(pipe.coordinate.x + 1, pipe.coordinate.y),
                    )

                else -> emptySet()
            }

        fun isCoordinateInBounds(coordinate: Coordinate): Boolean = coordinate.x in 0 until width && coordinate.y in 0 until height

        fun findLoop(): Set<Coordinate> {
            val visitedLocations = mutableSetOf<Coordinate>()
            val paths = ArrayDeque<Pipe>()
            paths.add(startPipe)
            visitedLocations.add(startPipe.coordinate)
            while (paths.isNotEmpty()) {
                val pipe = paths.removeFirst()
                getNeighbourCoordinates(pipe)
                    .filter { isCoordinateInBounds(it) }
                    .forEach { coordinate ->
                        val ch = pipeMap[coordinate]
                        ch?.let {
                            val newPipe = Pipe(coordinate, ch)
                            if (coordinate !in visitedLocations && isValidPipe(newPipe)) {
                                visitedLocations.add(coordinate)
                                paths.add(newPipe)
                            }
                        }
                    }
            }
            return visitedLocations
        }

        fun lengthOfLoop(): Int = loop.size

        fun areaOfLoop(): Int {
            val insidePoints = mutableSetOf<Coordinate>()
            for (i in 0 until height) {
                var isInside = false
                var prev = G
                for (j in 0 until width) {
                    val coordinate = Coordinate(j, i)
                    val curr = pipeMap[coordinate] ?: G
                    if (coordinate in loop) {
                        val ch = if (curr == S) startPipe.ch else curr
                        if (ch == V || (prev == F && ch == J) || (prev == L && ch == T)) {
                            isInside = !isInside
                        }
                        if (ch == L || ch == F) {
                            prev = ch
                        }
                    } else if (isInside) {
                        insidePoints.add(coordinate)
                    }
                }
            }
            return insidePoints.size
        }

        private data class Pipe(
            val coordinate: Coordinate,
            val ch: Char,
        )
    }
}
