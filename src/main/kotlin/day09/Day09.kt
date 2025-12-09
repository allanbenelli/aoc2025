package day09

import Point
import down
import getRows
import left
import println
import readInput
import right
import up
import kotlin.math.max
import kotlin.math.min

private val day = "day09"
fun main() {
    
    fun List<String>.toPoints() = this.map { line ->
        val (x, y) = line.trim().split(",").map { it.toInt() }
        Point(x, y)
    }
    
    fun part1(input: List<String>): Long {
        var count = 0L
        val points = input.toPoints()
        for (i in 0 until input.getRows()) {
            for (j in i + 1 until input.getRows()) {
                val smallerX = min(points[i].x, points[j].x)
                val largerX = max(points[i].x, points[j].x)
                val smallerY = min(points[i].y, points[j].y)
                val largerY = max(points[i].y, points[j].y)
                val disX = (largerX - smallerX + 1).toLong()
                val disY = (largerY - smallerY + 1).toLong()
                count = max(count, disX * disY)
            }
        }
        
        return count
    }
    
    
    fun Pair<Int, Int>.toMinMaxRange() = (minOf(first, second)..maxOf(first, second))
    
    fun part2(input: List<String>): Long {
        val red = input.toPoints()
        
        val xs = red.map { it.x }.distinct().sorted()
        val ys = red.map { it.y }.distinct().sorted()
        
        val xIndex = xs.withIndex().associate { it.value to it.index }
        val yIndex = ys.withIndex().associate { it.value to it.index }
        
        val widhtXs = xs.size
        val heightYs = ys.size
        
        val boundary = Array(widhtXs) { BooleanArray(heightYs) }
        
        for (i in red.indices) {
            val a = red[i]
            val b = red[(i + 1) % red.size]
            
            val ax = xIndex[a.x]!!
            val ay = yIndex[a.y]!!
            val bx = xIndex[b.x]!!
            val by = yIndex[b.y]!!
            
            if (ax == bx) {
                for (y in (ay to by).toMinMaxRange()) boundary[ax][y] = true
            } else if (ay == by) {
                for (x in (ax to bx).toMinMaxRange()) boundary[x][ay] = true
            }
        }
        
        val outside = Array(widhtXs) { BooleanArray(heightYs) }
        val queue = ArrayDeque<Point>()
        
        for (x in 0 until widhtXs) {
            if (!boundary[x][0]) {
                queue += Point(x, 0)
                outside[x][0] = true
            }
            if (!boundary[x][heightYs - 1]) {
                queue += Point(x, heightYs - 1)
                outside[x][heightYs - 1] = true
            }
        }
        for (y in 0 until heightYs) {
            if (!boundary[0][y]) {
                queue += Point(0, y)
                outside[0][y] = true
            }
            if (!boundary[widhtXs - 1][y]) {
                queue += Point(widhtXs - 1, y)
                outside[widhtXs - 1][y] = true
            }
        }
        
        while (queue.isNotEmpty()) {
            val p = queue.removeFirst()
            for (newP in listOf(p.up(), p.down(), p.right(), p.left())) {
                if (newP.x in 0 until widhtXs && newP.y in 0 until heightYs &&
                    !boundary[newP.x][newP.y] && !outside[newP.x][newP.y]
                ) {
                    outside[newP.x][newP.y] = true
                    queue += Point(newP.x, newP.y)
                }
            }
        }
        
        val allowed = Array(widhtXs) { BooleanArray(heightYs) }
        for (x in 0 until widhtXs) {
            for (y in 0 until heightYs) {
                if (boundary[x][y] || !outside[x][y]) {
                    allowed[x][y] = true
                }
            }
        }
        
        var best = 0L
        
        for (i in red.indices) {
            for (j in i + 1 until red.size) {
                val a = red[i]
                val b = red[j]
                
                val ax = xIndex[a.x]!!
                val ay = yIndex[a.y]!!
                val bx = xIndex[b.x]!!
                val by = yIndex[b.y]!!
                
                var ok = true
                loop@ for (x in (ax to bx).toMinMaxRange()) {
                    for (y in (ay to by).toMinMaxRange()) {
                        if (!allowed[x][y]) {
                            ok = false
                            break@loop
                        }
                    }
                }
                if (ok) {
                    val width = (max(a.x, b.x) - min(a.x, b.x) + 1).toLong()
                    val height = (max(a.y, b.y) - min(a.y, b.y) + 1).toLong()
                    best = max(best, width * height)
                }
            }
        }
        
        return best
    }
    
    val testInput = readInput("$day/test")
    check(part1(testInput) == 50L)
    check(part2(testInput) == 24L)
    
    val input = readInput("$day/input")
    part1(input).println()
    part2(input).println()
}
