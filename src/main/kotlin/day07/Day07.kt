package day07

import Point
import println
import readInput

private val day = "day07"
fun main() {
    
    fun inBounds(pos: Point, grid: List<String>) = pos.x in grid.indices && pos.y in grid[0].indices
    fun List<String>.getCols() = this[0].length
    fun List<String>.getRows() = this.size
    fun Point.down() = Point(x + 1, y)
    fun Point.downLeft() = Point(x + 1, y - 1)
    fun Point.downRight() = Point(x + 1, y + 1)
    
    fun splits(
        grid: List<String>, pos: Point, visited: MutableSet<Point> = mutableSetOf()
    ): Long {
        if (!inBounds(pos, grid) || !visited.add(pos)) return 0
        
        return if (grid[pos.x][pos.y] == '^') {
            1 + splits(grid, pos.downLeft(), visited) + splits(grid, pos.downRight(), visited)
        } else {
            splits(grid, pos.down(), visited)
        }
    }
    
    fun getStartPoint(input: List<String>): Point {
        for (i in input.indices) {
            val line = input[i]
            val chars = line.toCharArray()
            for (j in chars.indices) {
                if (chars[j] == 'S') {
                    return Point(i, j)
                }
            }
        }
        error("no start point")
    }
    
    fun part1(input: List<String>): Long {
        return splits(input, getStartPoint(input))
    }
    
    fun part2(input: List<String>): Long {
        val rows = input.size
        val width = input[0].length
        var start = getStartPoint(input)
        
        val dp = Array(input.getRows()) { LongArray(input.getCols()) }
        
        dp[start.x][start.y] = 1L
        
        for (x in start.x until input.getRows() - 1) {
            for (y in 0 until width) {
                val ways = dp[x][y]
                if (ways == 0L) continue
                val cur = Point(x, y)
                
                if (input[x][y] == '^') {
                    val left = cur.downLeft()
                    val right = cur.downRight()
                    
                    if (inBounds(left, input)) dp[left.x][left.y] += ways
                    if (inBounds(right, input)) dp[right.x][right.y] += ways
                } else {
                    val down = cur.down()
                    if (inBounds(down, input)) dp[down.x][down.y] += ways
                }
            }
        }
        return dp[rows - 1].sum()
    }
    
    val testInput = readInput("$day/test")
    check(part1(testInput) == 21L)
    check(part2(testInput) == 40L)
    
    val input = readInput("$day/input")
    part1(input).println()
    part2(input).println()
}
