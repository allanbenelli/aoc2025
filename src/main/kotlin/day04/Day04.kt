package day04

import directions
import println
import readInput

private val day = "day04"
fun main() {
    
    fun accessible(i: Int, j: Int, grid: Array<CharArray>): Boolean {
        val h = grid.size
        val w = grid[0].size
        if (grid[i][j] != '@') return false
        

        var neighbors = 0
        for ((di, dj) in directions) {
            val ni = i + di
            val nj = j + dj
            if (ni in 0 until h && nj in 0 until w && grid[ni][nj] == '@') {
                neighbors++
            }
        }
        return neighbors < 4
    }
    
    fun part1(input: List<String>): Long {
        val h = input.size
        val w = input[0].length
        val grid = input.map { it.toCharArray() }.toTypedArray()
        
        var count = 0L
        for (i in 0 until h) {
            for (j in 0 until w) {
                if (accessible(i, j, grid)) count++
            }
        }
        return count
    }
    
    fun part2(input: List<String>): Long {
        val h = input.size
        val w = input[0].length
        val grid = input.map { it.toCharArray() }.toTypedArray()
        
        var totalRemoved = 0L
        
        while (true) {
            val toRemove = mutableListOf<Pair<Int, Int>>()
            
            for (i in 0 until h) {
                for (j in 0 until w) {
                    if (accessible(i, j, grid)) {
                        toRemove += i to j
                    }
                }
            }
            if (toRemove.isEmpty()) break
            
            toRemove.forEach { (i, j) -> grid[i][j] = '.' }
            totalRemoved += toRemove.size
        }
        
        return totalRemoved
    }

    val testInput = readInput("$day/test")
    check(part1(testInput) == 13L)
    check(part2(testInput) == 43L)
    
    val input = readInput("$day/input")
    part1(input).println()
    part2(input).println()
}
