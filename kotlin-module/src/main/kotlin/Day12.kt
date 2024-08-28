import util.common.Solver

fun main() {
    Day12SolverK("day12.txt").printResults()
}

class Day12SolverK(
    fileName: String,
) : Solver<Long>(fileName) {
    override fun solvePartOne(): Long = puzzle.map { Springs.from(it) }.sumOf { it.countArrangements() }

    override fun solvePartTwo(): Long = puzzle.map { Springs.fromWithUnFolding(it) }.sumOf { it.countArrangements() }

    private data class Springs(
        val conditions: String,
        val damagedGroups: List<Int>,
    ) {
        companion object {
            private const val OPERATIONAL = '.'
            private const val DAMAGED = '#'
            private const val UNKNOWN = '?'

            fun from(line: String): Springs {
                line.split(" ").apply {
                    return Springs(first(), last().split(",").map { it.toInt() })
                }
            }

            fun fromWithUnFolding(line: String): Springs {
                line.split(" ").apply {
                    val conditions = List(5) { first() }.joinToString(separator = UNKNOWN.toString())
                    val groups =
                        List(5) { last() }
                            .joinToString(separator = ",")
                            .split(",")
                            .map { it.toInt() }
                    return Springs(conditions, groups)
                }
            }
        }

        fun countArrangements(): Long {
            fun isDamagedGroupCanBeFitted(
                conditions: String,
                damagedGroups: List<Int>,
            ): Boolean {
                val groupSize = damagedGroups.firstOrNull() ?: return false
                return conditions.length >= groupSize &&
                    !conditions.substring(0, groupSize).contains(OPERATIONAL) &&
                    conditions.getOrNull(groupSize) != DAMAGED
            }

            fun countArrangements(
                conditions: String,
                damagedGroups: List<Int>,
                memo: MutableMap<Springs, Long>,
            ): Long {
                val springs = Springs(conditions, damagedGroups)
                memo[springs]?.let { return it }

                if (conditions.isEmpty()) return if (damagedGroups.isEmpty()) 1 else 0
                if (damagedGroups.isEmpty()) return if (DAMAGED in conditions) 0 else 1

                val groupSize = damagedGroups.firstOrNull() ?: return 0
                if (conditions.length < groupSize) return 0
                if (conditions.length == groupSize) return if (OPERATIONAL in conditions) 0 else 1

                val sumOfDamaged = damagedGroups.sum()
                if (sumOfDamaged >= conditions.length) return 0

                var result = 0L
                if (conditions[0] == OPERATIONAL || conditions[0] == UNKNOWN) {
                    result += countArrangements(conditions.substring(1), damagedGroups, memo)
                }
                if (conditions[0] == DAMAGED || conditions[0] == UNKNOWN) {
                    if (isDamagedGroupCanBeFitted(conditions, damagedGroups)) {
                        result +=
                            countArrangements(
                                conditions.substring(damagedGroups[0] + 1),
                                damagedGroups.drop(1),
                                memo,
                            )
                    }
                }
                memo[springs] = result
                return result
            }

            return countArrangements(conditions, damagedGroups, mutableMapOf())
        }
    }
}
