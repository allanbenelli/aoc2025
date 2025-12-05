package day05_backup

import println
import readInput
import splitInputByBlankLine
import toLongRange

private val day = "day05_backup"
fun main() {
    
    fun part1(input: List<String>): Long {
        var count = 0L
        val (rangesBlock, numbersBlock) = splitInputByBlankLine(input)
        
        val ranges = rangesBlock.map { it.toLongRange() }
        
        numbersBlock.map { it.toLong() }.forEach { number ->
            if (ranges.any { it.contains(number) }) count++
        }
        return count
    }
    
    
    fun part2(input: List<String>): Long {
        var count = 0L
        val (rangesBlock, _) = splitInputByBlankLine(input)
        
        val ranges = rangesBlock.map { it.toLongRange() }
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
