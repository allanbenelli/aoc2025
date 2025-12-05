package day05

import println
import readInput

private val day = "day05"
fun main() {
    
    fun splitInput(input: List<String>): Pair<List<String>, List<String>> {
        val emptyIndex = input.indexOf("")
        
        val first = input.subList(0, emptyIndex)
        val second = input.subList(emptyIndex + 1, input.size)
        
        return first to second
    }

    
    fun part1(input: List<String>): Long {
        var count = 0L
        val (rangesBlock, numbersBlock) = splitInput(input)
        
        val ranges = rangesBlock.map { line ->
            val (a, b) = line.split("-").map { it.toLong() }
            a..b
        }
        
        numbersBlock.map { it.toLong() }.forEach { number ->
            if (ranges.any { it.contains(number) }) count++
        }
        return count
    }
    
    
    fun part2(input: List<String>): Long {
        var count = 0L
        val (rangesBlock, _) = splitInput(input)
        
        val ranges = rangesBlock.map { line ->
            val (a, b) = line.split("-").map { it.toLong() }
            a..b
        }
        val sortedRanges = ranges.sortedBy { it.first }
        
        var curStart = sortedRanges.first().first
        var curEnd = sortedRanges.first().last
        
        for (first in sortedRanges.drop(1)) {
            if (first.first <= curEnd + 1) { // overlap -> extend
                curEnd = maxOf(curEnd, first.last)
            } else {
                // sum up
                count += (curEnd - curStart + 1)
                curStart = first.first
                curEnd = first.last
            }
        }
        
        // last
        count += (curEnd - curStart + 1)
        return count
    }

    val testInput = readInput("$day/test")
    check(part1(testInput) == 3L)
    check(part2(testInput) == 14L)
    
    val input = readInput("$day/input")
    part1(input).println()
    part2(input).println()
}
