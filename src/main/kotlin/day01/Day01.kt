package day01

import println
import readInput
import kotlin.math.abs

private val day = "day01"
fun main() {
    
    fun part1(input: List<String>): Int {
        var count = 0
        var startPosition = 50
        input.forEach { str ->
            val dir = str.first()
            val value = str.substring(1).toInt()
            if (dir == 'R') {
                startPosition += value
            } else {
                startPosition -= value
            }
            startPosition %= 100
            if (startPosition == 0) count++
        }
        return count
    }
    

    fun part2(input: List<String>): Int {
        var count = 0
        var startPosition = 50
        input.forEach { str ->
            val dir = str.substring(0,1).first()
            var value = str.substring(1).toInt()
            if (value > 100) {
                count += value / 100
                value = value.mod(100)
            }
            if (dir == 'R') {
                if (startPosition + value > 100) count++
                startPosition += value
                
            } else {
                if (startPosition != 0 && startPosition - value < 0) count++
                startPosition -= value
            }
            startPosition = startPosition.mod(100)
            if (startPosition == 0) count++
        }
        return count
    }



    // Test if implementation meets criteria from the description, like:
    //check(part1(readInput("Day01_test")) == 11)

    // Or read a large test input from the `src/test.txt` file:
    val testInput = readInput("$day/test")
    check(part1(testInput) == 3)
    check(part2(testInput) == 6)
    
    // Read the input from the `src/input.txt` file.
    val input = readInput("$day/input")
    part1(input).println()
    part2(input).println()
}
