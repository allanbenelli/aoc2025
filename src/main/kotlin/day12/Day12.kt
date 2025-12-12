package day12

import Point
import println
import readInput
import com.google.ortools.Loader
import com.google.ortools.sat.CpModel
import com.google.ortools.sat.CpSolver
import com.google.ortools.sat.CpSolverStatus
import com.google.ortools.sat.IntVar
import com.google.ortools.sat.LinearExpr
import java.util.concurrent.atomic.AtomicInteger

private val day = "day12"

fun main() {
    Loader.loadNativeLibraries()
    
    data class Shape(val cells: List<Point>)
    
    data class Region(
        val width: Int,
        val height: Int,
        val required: List<Int>,
    )

    fun parseInput(input: List<String>): Pair<List<Shape>, List<Region>> {
        val regionStart = input.indexOfFirst { it.matches(Regex("""\d+x\d+:\s.*""")) }
        
        val shapesLines = input.subList(0, regionStart)
        val regionLines = input.subList(regionStart, input.size)
        
        val shapes = shapesLines.filterNot { it.endsWith(":") }
            .joinToString("\n")
            .split("\n\n")
            .map { block ->
                val cells = mutableListOf<Point>()
                block.lines().forEachIndexed { y, row ->
                    row.forEachIndexed { x, ch ->
                        if (ch == '#') cells += Point(x, y)
                    }
                }
                Shape(cells)
            }
        
        val regions = regionLines.filter { it.isNotBlank() }.map { line ->
            // "4x4: 0 0 0 0 2 0"
            val (left, right) = line.split(":")
            val (wStr, hStr) = left.split("x")
            val width = wStr.trim().toInt()
            val height = hStr.trim().toInt()
            val counts = right.trim()
                .split(" ")
                .filter { it.isNotBlank() }
                .map { it.trim().toInt() }
            
            Region(width, height, counts)
        }
        
        return shapes to regions
    }
    
    fun normalize(cells: List<Point>): List<Point> {
        val minX = cells.minOf { it.x }
        val minY = cells.minOf { it.y }
        return cells
            .map { Point(it.x - minX, it.y - minY) }
            .sortedWith(compareBy<Point> { it.y }.thenBy { it.x })
    }
    
    fun allVariants(shape: Shape): List<List<Point>> {
        val base = shape.cells
        
        fun rot90(p: Point) = Point(-p.y, p.x)
        fun rot(p: Point, times: Int): Point {
            var r = p
            repeat(times) { r = rot90(r) }
            return r
        }
        fun flipX(p: Point) = Point(-p.x, p.y)
        
        val variants = mutableSetOf<List<Point>>()
        
        for (r in 0 until 4) {
            val rotated = base.map { rot(it, r) }
            val rotatedNorm = normalize(rotated)
            variants += rotatedNorm
            
            val flipped = rotated.map(::flipX)
            val flippedNorm = normalize(flipped)
            variants += flippedNorm
        }
        
        return variants.toList()
    }
    
    data class Placement(
        val shapeIdx: Int,
        val cells: IntArray, // (y * W + x)
    )
    
    fun buildPlacementsForRegion(
        shapes: List<Shape>,
        region: Region
    ): List<Placement> {
        val (w, h, required) = region
        val placements = mutableListOf<Placement>()
        
        shapes.forEachIndexed { sIdx, shape ->
            if (required[sIdx] == 0) return@forEachIndexed
            
            val variants = allVariants(shape)
            for (variant in variants) {
                val maxX = variant.maxOf { it.x }
                val maxY = variant.maxOf { it.y }
                
                if (maxX >= w || maxY >= h) continue
                
                for (oy in 0..(h - maxY - 1)) {
                    for (ox in 0..(w - maxX - 1)) {
                        val cellsIdx = IntArray(variant.size) { idx ->
                            val p = variant[idx]
                            val x = ox + p.x
                            val y = oy + p.y
                            y * w + x
                        }
                        placements += Placement(sIdx, cellsIdx)
                    }
                }
            }
        }
        
        return placements
    }
    
    fun canFillRegion(shapes: List<Shape>, region: Region): Boolean {
        val (w, h, required) = region
        
        val totalArea = shapes.indices.sumOf { s ->
            val areaS = shapes[s].cells.size
            areaS.toLong() * required[s].toLong()
        }
        if (totalArea > w.toLong() * h.toLong()) return false
        
        val placements = buildPlacementsForRegion(shapes, region)
        if (placements.isEmpty() && required.any { it != 0 }) return false
        
        val cellCount = w * h
        
        val cellToPlacements = Array(cellCount) { mutableListOf<Int>() }
        placements.forEachIndexed { pIdx, p ->
            p.cells.forEach { c -> cellToPlacements[c] += pIdx }
        }
        
        // check: wenn eine Form gebraucht wird, aber sie nie irgendwo platziert werden kann: impossible
        val shapeHasPlacement = BooleanArray(shapes.size)
        for (p in placements) {
            shapeHasPlacement[p.shapeIdx] = true
        }
        for (s in shapes.indices) {
            if (required[s] > 0 && !shapeHasPlacement[s]) return false
        }
        
        val model = CpModel()
        
        val varPlacement: Array<IntVar> = Array(placements.size) { pIdx ->
            model.newBoolVar("placement-$pIdx")
        }
        
        // c1: jede Zelle max einmal belegt
        for (c in 0 until cellCount) {
            val list = cellToPlacements[c]
            if (list.isEmpty()) continue
            val vars = list.map { varPlacement[it] }.toTypedArray()
            model.addLessOrEqual(LinearExpr.sum(vars), 1)
        }
        
        // c2: Shape-Counts
        shapes.indices.forEach { s ->
            val need = required[s]
            if (need == 0) return@forEach
            val plist = placements.withIndex()
                .filter { it.value.shapeIdx == s }
                .map { varPlacement[it.index] }

            model.addEquality(LinearExpr.sum(plist.toTypedArray()), need.toLong())
            
        }
        
        model.minimize(LinearExpr.sum(varPlacement))
        
        val solver = CpSolver()
        val status = solver.solve(model)
        
        return setOf(CpSolverStatus.OPTIMAL, CpSolverStatus.FEASIBLE).contains(status)
    }
    
    fun part1(input: List<String>): Long {
        val (shapes, regions) = parseInput(input)
        
        //val counter = AtomicInteger(0)
        
        return regions
            .parallelStream()
            .filter { region ->
                //val now = counter.incrementAndGet()
                //println("cur: $now")
                canFillRegion(shapes, region)
            }.count()
        
    
    }
    
    
    val testInput = readInput("$day/test")
    check(part1(testInput) == 2L)
    
    val input = readInput("$day/input")
    part1(input).println()
}
