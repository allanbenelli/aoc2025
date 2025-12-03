package day02

import println
import readInput

private val day = "day02"
fun main() {
    
    fun isMirroredHalf(x: Long): Boolean {
        val s = x.toString()
        
        if (s.length % 2 != 0) return false
        
        val mid = s.length / 2
        val left = s.substring(0, mid)
        val right = s.substring(mid)
        
        return left == right
    }
    
    
    fun isRepeatablePattern(x: Long): Boolean {
        val s = x.toString()
        val n = s.length
        
        for (len in 1..n / 2) {
            if (n % len == 0) {
                val pattern = s.substring(0, len)
                val repeadet = pattern.repeat(n / len)
                if (repeadet == s) {
                    return true
                }
            }
        }
        return false
    }
    fun part1(input: List<String>): Long {
        val list = input.first().split(",")
        var count = 0L
        list.forEach { value ->
            val range = value.split("-")
            val from = range.first().toLong()
            val to = range.last().toLong()
            for (i in from..to) {
                if (isMirroredHalf(i)) {
                    count += i
                }
            }
        }

        return count
    }
    


    fun part2(input: List<String>): Long {
        val list = input.first().split(",")
        var count = 0L
        list.forEach { value ->
            val range = value.split("-")
            val from = range.first().toLong()
            val to = range.last().toLong()
            for (i in from..to) {
                if (isRepeatablePattern(i)) {
                    count += i
                }
            }
        }
        
        return count
    }



    // Test if implementation meets criteria from the description, like:
    //check(part1(readInput("Day01_test")) == 11)

    // Or read a large test input from the `src/test.txt` file:
    val testInput = readInput("$day/test")
    check(part1(testInput) == 1227775554L)
    check(part2(testInput) == 4174379265L)
    
    // Read the input from the `src/input.txt` file.
    val input = readInput("$day/input")
    part1(input).println()
    part2(input).println()
}
