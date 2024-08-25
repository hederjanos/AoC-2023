import util.common.Solver
import java.util.regex.Pattern
import kotlin.math.abs

fun main() {
    Day8SolverK("day8.txt").printResults()
}

class Day8SolverK(
    fileName: String,
) : Solver<Long>(fileName) {
    private val network: Network = Network.from(puzzle)

    override fun solvePartOne(): Long = network.countStepsFrom("AAA") { s: String -> s == "ZZZ" }

    override fun solvePartTwo(): Long {
        val startPredicate: (String) -> Boolean = { it.endsWith('A') }
        val targetPredicate: (String) -> Boolean = { it.endsWith('Z') }

        return network.allNodeLabels
            .filter(startPredicate)
            .map { label -> network.countStepsFrom(label, targetPredicate) }
            .reduce { prev, next -> calculateLCM(prev, next) }
    }

    private fun calculateLCM(
        a: Long,
        b: Long,
    ): Long = (abs((a * b).toDouble()) / calculateGCD(a, b)).toLong()

    private fun calculateGCD(
        a: Long,
        b: Long,
    ): Long = if (b == 0L) a else calculateGCD(b, a % b)

    private data class Network(
        val instructions: String,
        val nodeMap: Map<String, Node>,
    ) {
        companion object {
            fun from(puzzle: List<String>): Network {
                val pattern = Pattern.compile("^(\\w+)\\s=\\s\\((\\w+),\\s(\\w+)\\)$")
                val nodeMap: MutableMap<String, Node> = HashMap()
                for (i in 2 until puzzle.size) {
                    val matcher = pattern.matcher(puzzle[i])
                    while (matcher.find()) {
                        val parent = matcher.group(1)
                        val left = matcher.group(2)
                        val right = matcher.group(3)
                        nodeMap[parent] = Node(parent, left, right)
                    }
                }
                return Network(puzzle[0], nodeMap)
            }
        }

        val allNodeLabels: Set<String>
            get() = nodeMap.keys

        fun countStepsFrom(
            start: String,
            targetPredicate: (String) -> Boolean,
        ): Long {
            var current = nodeMap[start] ?: throw IllegalArgumentException("Invalid start node: $start")
            var steps: Long = 0
            while (!targetPredicate(current.label)) {
                val instruction = instructions[(steps++ % instructions.length).toInt()]
                current = if (instruction == 'L') {
                    nodeMap[current.left]
                } else {
                    nodeMap[current.right]
                } ?: throw IllegalStateException("Instruction: $instruction from node: $current is incomprehensible")
            }
            return steps
        }
    }

    private data class Node(
        val label: String,
        val left: String,
        val right: String,
    )
}
