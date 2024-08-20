import util.common.Solver
import util.coordinate.Coordinate

fun main() {
    Day3SolverK("day3.txt").printResults()
}

class Day3SolverK(
    fileName: String,
) : Solver<Int>(fileName) {
    private val engineSchematic: EngineSchematic = EngineSchematic.from(puzzle)

    override fun solvePartOne(): Int = engineSchematic.getSumOfValidPartNumbers()

    override fun solvePartTwo(): Int = engineSchematic.getSumOfGearRatios()

    private class EngineSchematic(
        val width: Int,
        val height: Int,
        val symbols: Set<Symbol>,
        val partNumbers: Set<PartNumber>,
    ) {
        companion object {
            private const val PERIOD = '.'
            private const val ASTERISK = '*'

            fun from(puzzle: List<String>): EngineSchematic {
                val partNumbers = mutableSetOf<PartNumber>()
                val symbols = mutableSetOf<Symbol>()

                puzzle.forEachIndexed { i, line ->
                    "\\d+"
                        .toRegex()
                        .findAll(line)
                        .forEach { match ->
                            val coordinates =
                                (match.range.first..match.range.last)
                                    .map { j -> Coordinate(j, i) }
                                    .toSet()
                            partNumbers.add(PartNumber(match.value.toInt(), coordinates))
                        }

                    line.forEachIndexed { j, ch ->
                        if (!ch.isDigit() && ch != PERIOD) {
                            symbols.add(Symbol(ch, Coordinate(j, i)))
                        }
                    }
                }
                return EngineSchematic(puzzle.first().length, puzzle.size, symbols, partNumbers)
            }
        }

        fun getSumOfValidPartNumbers(): Int = partNumbers.filter { partNumberIsAdjacentToSymbol(it) }.sumOf { it.value }

        fun partNumberIsAdjacentToSymbol(partNumber: PartNumber): Boolean =
            getNeighbours(partNumber).any { neighbour -> symbols.any { symbol -> symbol.coordinate == neighbour } }

        fun getNeighbours(partNumber: PartNumber): Set<Coordinate> = partNumber.getNeighbours().filter { isCoordinateInBounds(it) }.toSet()

        fun isCoordinateInBounds(coordinate: Coordinate): Boolean = coordinate.x in 0 until width && coordinate.y in 0 until height

        fun getSumOfGearRatios(): Int =
            symbols
                .filter { it.symbol == ASTERISK }
                .map { getAdjacentPartNumbersToSymbol(it) }
                .filter { it.size == 2 }
                .sumOf { gearParts -> gearParts.map { it.value }.reduce { a, b -> a * b } }

        fun getAdjacentPartNumbersToSymbol(symbol: Symbol): Set<PartNumber> =
            symbol.coordinate
                .getAdjacentCoordinates()
                .mapNotNull { findPartNumber(it) }
                .toSet()

        fun findPartNumber(coordinate: Coordinate): PartNumber? = partNumbers.find { it.contains(coordinate) }

        fun findSymbol(coordinate: Coordinate): Symbol? = symbols.find { it.coordinate == coordinate }

        override fun toString(): String =
            (0 until height)
                .joinToString(separator = System.lineSeparator()) { i ->
                    (0 until width)
                        .joinToString(separator = "") { j -> getPrintAt(i, j).toString() }
                }

        fun getPrintAt(
            i: Int,
            j: Int,
        ): Char {
            val coordinate = Coordinate(j, i)
            return findPartNumber(coordinate)?.let {
                (it.value + '0'.code).toChar()
            } ?: findSymbol(coordinate)?.symbol ?: PERIOD
        }
    }

    private data class PartNumber(
        val value: Int,
        val coordinates: Set<Coordinate>,
    ) {
        fun contains(coordinate: Coordinate): Boolean = coordinates.contains(coordinate)

        fun getNeighbours(): Set<Coordinate> = coordinates.flatMap { it.getAdjacentCoordinates() }.toSet()
    }

    private data class Symbol(
        val symbol: Char,
        val coordinate: Coordinate,
    )
}
