import util.common.Solver

fun main() {
    Day1SolverK("day1.txt").printResults()
}

class Day1SolverK(filename: String) : Solver<Int>(filename) {
    override fun solvePartOne(): Int = puzzle.sumOf { line -> CalibrationValue.from(line).value }

    override fun solvePartTwo(): Int = puzzle.sumOf { line -> CalibrationValue.from(line, withSpelled = true).value }

    private data class CalibrationValue(val value: Int) {
        companion object {
            fun from(line: String, withSpelled: Boolean = false): CalibrationValue {
                var first = -1
                var last = -1

                for (i in line.indices) {
                    val c = line[i]
                    var currentDigit = -1

                    if (c.isDigit()) {
                        currentDigit = c.digitToInt()
                    } else if (withSpelled) {
                        currentDigit = Digit.matchAt(line, i)
                    }

                    if (currentDigit != -1) {
                        if (first == -1) {
                            first = currentDigit
                        }
                        last = currentDigit
                    }
                }
                return CalibrationValue(first * 10 + last)
            }
        }
    }

    private enum class Digit(val value: Int, val text: String) {
        ONE(1, "one"),
        TWO(2, "two"),
        THREE(3, "three"),
        FOUR(4, "four"),
        FIVE(5, "five"),
        SIX(6, "six"),
        SEVEN(7, "seven"),
        EIGHT(8, "eight"),
        NINE(9, "nine");

        companion object {
            fun matchAt(line: String, index: Int): Int {
                return entries.firstOrNull { line.startsWith(it.text, index) }?.value ?: -1
            }
        }
    }
}
