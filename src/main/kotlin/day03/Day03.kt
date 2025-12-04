package day03

import deleteAtEnd
import parseToDigit
import println
import readInput
import toLength

private val day = "day03"
fun main() {
    fun String.maxPair(): Int {
        var best = -1
        
        for (i in indices) {
            val a = this[i].parseToDigit()
            for (j in i + 1 until length) {
                val b = this[j].parseToDigit()
                best = maxOf(best, a * 10 + b)
            }
        }
        
        return best
    }
    
    fun String.max12(): Long {
        var remove = length - 12
        val stack = StringBuilder(12)
        
        for (c in this) {
            while (remove > 0 && stack.isNotEmpty() && stack.last() < c) {
                stack.deleteAtEnd()
                remove--
            }
            stack.append(c)
        }
        
        return stack.toLength(12).toLong()
    }
    
    fun part1(input: List<String>): Long =
        input.sumOf { it.maxPair().toLong() }
    
    fun part2(input: List<String>): Long =
        input.sumOf { it.max12() }

    val testInput = readInput("$day/test")
    check(part1(testInput) == 357L)
    check(part2(testInput) == 3121910778619L)
    
    val input = readInput("$day/input")
    part1(input).println()
    part2(input).println()
}
