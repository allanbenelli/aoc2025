package day06

import parseToDigit
import println
import readInput
import transpose
import kotlin.ranges.until

private val day = "day06"


fun main() {
    fun splitInputInNumbersAndOperators(input: List<String>): Pair<List<List<Long>>, List<String>> {
        val operators = input.last().trim().split("\\s+".toRegex())
        val numbers = input.dropLast(1).map { it.trim().split("\\s+".toRegex()).map { it.toLong() } }
        return numbers.transpose() to operators
    }
    
    fun part1(input: List<String>): Long {
        var count = 0L
        val (numbers, operators) = splitInputInNumbersAndOperators(input)
        
        val operations = operators.size
        for (j in 0 until operations) {
            val op = operators[j]
            val nums = numbers[j]
            var colCount = when (op) {
                "+" -> nums.sum()
                "*" -> nums.fold(1L) { pre, value -> pre * value }
                else -> error("Unknown operator: $op")
            }
            
            count += colCount
        }
        return count
    }
    
    
    fun getNumbersListForPart2(input: List<String>): List<List<Long>> {
        val maxWidth = input.maxOf { it.length }
        
        val out: MutableList<List<Long>> = mutableListOf()
        var colList = mutableListOf<Long>()
        for (j in 0 until maxWidth) {
            var allEmpty = true
            var colNumber = 0L
            for (i in input.indices) {
                if (input[i].length <= j) continue
                val char = input[i][j]
                if (char != ' ') {
                    allEmpty = false
                    colNumber *= 10
                    colNumber += char.parseToDigit()
                }
            }
            if (colNumber != 0L) {
                colList.add(colNumber)
            }
            if (allEmpty) {
                out.add(colList)
                colList = mutableListOf()
            }
        }
        out.add(colList)
        return out
    }
    
    fun part2(input: List<String>): Long {
        val numbers = getNumbersListForPart2(input.dropLast(1))
        val operators = input.last().trim().split("\\s+".toRegex())
        
        var count = 0L
        
        val operations = operators.size
        for (j in 0 until operations) {
            val op = operators[j]
            val nums = numbers[j]
            var colCount = when (op) {
                "+" -> nums.sum()
                "*" -> nums.fold(1L) { pre, value -> pre * value }
                else -> error("Unknown operator: $op")
            }
            
            count += colCount
        }
        return count
    }
    
    val testInput = readInput("$day/test")
    check(part1(testInput) == 4277556L)
    check(part2(testInput) == 3263827L)
    
    val input = readInput("$day/input")
    
    part1(input).println()
    part2(input).println()
}
