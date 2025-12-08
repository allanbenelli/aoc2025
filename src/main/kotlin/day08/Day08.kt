package day08

import Point3D
import UnionFind
import println
import readInput

private val day = "day08"
fun main() {
    
    fun getSortedEdgesByDistance(points: List<Point3D>): MutableList<Triple<Int, Int, Double>> {
        val edges = mutableListOf<Triple<Int, Int, Double>>()
        val n = points.size
        for (i in 0 until n) {
            for (j in i + 1 until n) {
                edges += Triple(i, j, points[i].distTo(points[j]))
            }
        }
        edges.sortBy { it.third }
        return edges
    }
    
    fun List<String>.to3DPoints() = this.map { line ->
        val (x, y, z) = line.split(",").map { it.trim().toLong() }
        Point3D(x, y, z)
    }
    
    fun part1(input: List<String>, connections: Int): Long {
        val points = input.to3DPoints()
        val n = points.size
        val uf = UnionFind(n)
        
        val edges = getSortedEdgesByDistance(points)
        
        var used = 0
        for ((a, b, _) in edges) {
            used++
            uf.union(a, b)
            if (used == connections) break
        }
        val sizes = uf.componentSizes().sortedDescending()
        return sizes[0] * sizes[1] * sizes[2]
    }
    
    
    fun part2(input: List<String>): Long {
        val points = input.to3DPoints()
        val n = points.size
        val uf = UnionFind(n)
        
        val edges = getSortedEdgesByDistance(points)
        
        for ((a, b, _) in edges) {
            uf.union(a, b)
            if (uf.componentSizes().size == 1) {
                return points[a].x * points[b].x
            }
        }
        
        error("No solution found")
    }
    
    val testInput = readInput("$day/test")
    check(part1(testInput, 10) == 40L)
    check(part2(testInput) == 25272L)
    
    val input = readInput("$day/input")
    part1(input, 1000).println()
    part2(input).println()
}
