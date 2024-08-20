import util.common.Solver

fun main() {
    Day2SolverK("day2.txt").printResults()
}

class Day2SolverK(
    filename: String,
) : Solver<Int>(filename) {
    override fun solvePartOne(): Int {
        val config = mapOf("red" to 12, "green" to 13, "blue" to 14)
        return puzzle.map { Game.from(it) }.filter { it.isValid(config) }.sumOf { it.gameId }
    }

    override fun solvePartTwo(): Int = puzzle.map { Game.from(it) }.sumOf { it.getPower() }

    private class Game(
        val gameId: Int,
        val colorMap: Map<String, Int>,
    ) {
        companion object {
            fun from(line: String): Game {
                val gamePattern = Regex("^Game\\s(\\d+):(.+)$")
                val cubeStatePattern = Regex("^(\\d+)\\s(\\w+)$")

                val gameMatcher = gamePattern.find(line)
                val gameId = gameMatcher?.groupValues?.get(1)?.toInt() ?: 0
                val subsets = gameMatcher?.groupValues?.get(2) ?: ""

                val colorCounts = mutableMapOf<String, Int>()
                val sets = subsets.split(";")
                for (set in sets) {
                    val states = set.split(",")
                    for (state in states) {
                        val stateMatcher = cubeStatePattern.find(state.trim())
                        if (stateMatcher != null) {
                            val count = stateMatcher.groupValues[1].toInt()
                            val color = stateMatcher.groupValues[2]
                            colorCounts[color] = maxOf(colorCounts[color] ?: 0, count)
                        }
                    }
                }
                return Game(gameId, colorCounts)
            }
        }

        fun isValid(config: Map<String, Int>): Boolean = colorMap.all { (color, count) -> config.getOrDefault(color, 0) >= count }

        fun getPower(): Int {
            val redCount = colorMap.getOrDefault("red", 1)
            val greenCount = colorMap.getOrDefault("green", 1)
            val blueCount = colorMap.getOrDefault("blue", 1)
            return redCount * greenCount * blueCount
        }
    }
}
