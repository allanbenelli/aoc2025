package day11

import println
import readInput

private val day = "day11"
fun main() {
    
    fun parseGraph(input: List<String>): Map<String, List<String>> {
        val graph = mutableMapOf<String, MutableList<String>>()
        
        input.forEach { line ->
            val (from, rest) = line.split(":")
            val targets = rest.trim().split(" ").filter { it.isNotBlank() }
            
            graph.getOrPut(from.trim()) { mutableListOf() } += targets
        }
        
        return graph
    }
    
    
    fun countPaths(graph: Map<String, List<String>>, start: String, end: String): Long {
        val memo = mutableMapOf<String, Long>()
        
        fun dfs(node: String): Long {
            if (node == end) return 1L
            
            memo[node]?.let { return it }
            
            var total = 0L
            val neighbors = graph[node] ?: emptyList()
            for (next in neighbors) {
                total += dfs(next)
            }
            
            memo[node] = total
            return total
        }
        
        return dfs(start)
    }
    
    fun part1(input: List<String>): Long {
        val graph = parseGraph(input)
        return countPaths(graph, "you", "out")
    }
    
    
    fun part2(input: List<String>): Long {
        val graph = parseGraph(input)
        
        val svr = "svr"
        val dac = "dac"
        val fft = "fft"
        val out = "out"
        
        fun p(a: String, b: String) = countPaths(graph, a, b)
        
        val dacThenFft = p(svr, dac) * p(dac, fft) * p(fft, out)
        val fftThenDac = p(svr, fft) * p(fft, dac) * p(dac, out)
        
        return dacThenFft + fftThenDac
    }

    val testInput = readInput("$day/test")
    val testInput2 = readInput("$day/test2")
    check(part1(testInput) == 5L)
    check(part2(testInput2) == 2L)
    
    val input = readInput("$day/input")
    part1(input).println()
    part2(input).println()
}
