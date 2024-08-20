import util.common.Solver

fun main() {
    Day1SolverK("day1.txt").printResults()
}

class Day1SolverK(
    filename: String,
) : Solver<Int>(filename) {
    override fun solvePartOne(): Int = puzzle.sumOf { line -> CalibrationValue.from(line).value }

    override fun solvePartTwo(): Int = puzzle.sumOf { line -> CalibrationValue.from(line, withSpelled = true).value }

    private class CalibrationValue(
        val value: Int,
    ) {
        companion object {
            fun from(
                line: String,
                withSpelled: Boolean = false,
            ): CalibrationValue {
                val digits = mutableListOf<Int>()
                var i = 0
                while (i < line.length) {
                    var matchedWithSpelled = false
                    val charAt = line[i]
                    if (charAt.isDigit()) {
                        digits.add(charAt.digitToInt())
                    } else if (withSpelled) {
                        val substring = line.substring(i)
                        for (digit in Digit.entries) {
                            if (substring.startsWith(digit.name.lowercase())) {
                                digits.add(digit.value)
                                i += digit.name.length - 1
                                matchedWithSpelled = true
                                break
                            }
                        }
                    }
                    if (!matchedWithSpelled) {
                        i++
                    }
                }
                return CalibrationValue("${digits.first()}${digits.last()}".toInt())
            }
        }
    }

    private enum class Digit(
        val value: Int,
    ) {
        ONE(1),
        TWO(2),
        THREE(3),
        FOUR(4),
        FIVE(5),
        SIX(6),
        SEVEN(7),
        EIGHT(8),
        NINE(9),
    }
}
