import util.common.Solver

fun main() {
    Day2SolverK("day2.txt").printResults()
}

class Day2SolverK(
    filename: String,
) : Solver<Int>(filename) {

    private val games: List<Game> = puzzle.map { Game.from(it) }

    override fun solvePartOne(): Int {
        val config = mapOf("red" to 12, "green" to 13, "blue" to 14)
        return games.filter { it.isValid(config) }.sumOf { it.gameId }
    }

    override fun solvePartTwo(): Int = games.sumOf { it.getPower() }

    private class Game(
        val gameId: Int,
        val colorMap: Map<String, Int>,
    ) {
        companion object {
            private val GAME_PATTERN = Regex("""^Game\s+(\d+):""")
            private val CUBE_PATTERN = Regex("""(\d+)\s+(\w+)""")

            fun from(line: String): Game {
                val gameId = GAME_PATTERN.find(line)?.groupValues?.get(1)?.toInt() ?: 0

                val colorCounts = mutableMapOf<String, Int>()

                CUBE_PATTERN.findAll(line).forEach { match ->
                    val count = match.groupValues[1].toInt()
                    val color = match.groupValues[2]

                    colorCounts[color] = maxOf(colorCounts[color] ?: 0, count)
                }

                return Game(gameId, colorCounts)
            }
        }

        fun isValid(config: Map<String, Int>): Boolean =
            colorMap.all { (color, count) -> (config[color] ?: 0) >= count }

        fun getPower(): Int {
            val redCount = colorMap["red"] ?: 1
            val greenCount = colorMap["green"] ?: 1
            val blueCount = colorMap["blue"] ?: 1

            return redCount * greenCount * blueCount
        }
    }
}
