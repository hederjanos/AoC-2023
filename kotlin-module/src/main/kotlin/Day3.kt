import util.common.Solver

fun main() {
    Day3SolverK("day3.txt").printResults()
}

class Day3SolverK(fileName: String) : Solver<Int>(fileName) {
    private val engineSchematic: EngineSchematic = EngineSchematic.from(puzzle)

    override fun solvePartOne(): Int = engineSchematic.getSumOfValidPartNumbers()

    override fun solvePartTwo(): Int = engineSchematic.getSumOfGearRatios()

    private class EngineSchematic(
        val width: Int,
        val height: Int,
        val partNumbers: Set<PartNumber>,
        val partMap: Map<Coordinate, PartNumber>,
        val symbolMap: Map<Coordinate, Symbol>
    ) {
        companion object {
            private val NUMBER_PATTERN = "\\d+".toRegex()
            private const val PERIOD = '.'
            private const val ASTERISK = '*'

            fun from(puzzle: List<String>): EngineSchematic {
                val partNumbers = mutableSetOf<PartNumber>()
                val partMap = mutableMapOf<Coordinate, PartNumber>()
                val symbolMap = mutableMapOf<Coordinate, Symbol>()

                puzzle.forEachIndexed { y, line ->
                    NUMBER_PATTERN.findAll(line).forEach { match ->
                        val coordinates = match.range.map { x -> Coordinate(x, y) }.toSet()
                        val part = PartNumber(match.value.toInt(), coordinates)

                        partNumbers.add(part)
                        coordinates.forEach { partMap[it] = part }
                    }

                    line.forEachIndexed { x, ch ->
                        if (!ch.isDigit() && ch != PERIOD) {
                            val coord = Coordinate(x, y)
                            symbolMap[coord] = Symbol(ch, coord)
                        }
                    }
                }
                return EngineSchematic(puzzle.first().length, puzzle.size, partNumbers, partMap, symbolMap)
            }
        }

        fun getSumOfValidPartNumbers(): Int =
            partNumbers.filter { partNumberIsAdjacentToSymbol(it) }.sumOf { it.value }

        fun partNumberIsAdjacentToSymbol(partNumber: PartNumber): Boolean =
            getNeighbours(partNumber).any { symbolMap.containsKey(it) }

        fun getNeighbours(partNumber: PartNumber): Set<Coordinate> =
            partNumber.getNeighbours().filter { isCoordinateInBounds(it) }.toSet()

        fun isCoordinateInBounds(coordinate: Coordinate): Boolean =
            coordinate.x in 0 until width && coordinate.y in 0 until height

        fun getSumOfGearRatios(): Int =
            symbolMap.values
                .filter { it.symbol == ASTERISK }
                .map { getAdjacentPartNumbersToSymbol(it) }
                .filter { it.size == 2 }
                .sumOf { gearParts ->
                    val iter = gearParts.iterator()
                    iter.next().value * iter.next().value
                }

        fun getAdjacentPartNumbersToSymbol(symbol: Symbol): Set<PartNumber> =
            symbol.coordinate.getAdjacentCoordinates().mapNotNull { partMap[it] }.toSet()
    }

    private data class PartNumber(val value: Int, val coordinates: Set<Coordinate>) {
        fun getNeighbours(): Set<Coordinate> = coordinates.flatMap { it.getAdjacentCoordinates() }.toSet()
    }

    private data class Symbol(val symbol: Char, val coordinate: Coordinate)

    data class Coordinate(val x: Int, val y: Int) {
        fun getAdjacentCoordinates(): Set<Coordinate> =
            Direction.entries.map { Coordinate(this.x + it.dx, this.y + it.dy) }.toSet()
    }

    enum class Direction(val dx: Int, val dy: Int) {
        UP(0, -1), UPPER_RIGHT(1, -1), RIGHT(1, 0), DOWN_RIGHT(1, 1),
        DOWN(0, 1), DOWN_LEFT(-1, 1), LEFT(-1, 0), UPPER_LEFT(-1, -1)
    }


}