package day10

import com.google.ortools.Loader
import com.google.ortools.sat.CpModel
import com.google.ortools.sat.CpSolver
import com.google.ortools.sat.IntVar
import com.google.ortools.sat.LinearExpr
import println
import readInput

private val day = "day10"
fun main() {
    Loader.loadNativeLibraries() // brauchts, sonst lädts nicht
    
    data class Machine(
        val target: Int,
        val buttons: List<Int>,
        val bits: Int,
        
        val joltageTarget: List<Int>,
        val buttonCounters: List<List<Int>>
    )
    
    fun parseMachine(line: String): Machine {
        val diag = "\\[(.+?)]".toRegex().find(line)!!.groupValues[1]
        val bits = diag.length
        val targetMask = diag.fold(0) { acc, c -> (acc shl 1) + if (c == '#') 1 else 0 }
        
        val buttonStrings = "\\((.*?)\\)".toRegex().findAll(line).map { it.groupValues[1] }.toList()
        
        val buttonMasks = buttonStrings.map { nums ->
            if (nums.isBlank()) 0
            else nums.split(",").map { it.trim().toInt() }.fold(0) { mask, i ->
                mask or (1 shl (bits - 1 - i))
            }
        }.toList()
        
        val buttonCounters = buttonStrings.map { nums ->
            if (nums.isBlank()) emptyList()
            else nums.split(",").map { it.trim().toInt() }
        }
        
        val joltageCounter = "\\{(.+?)}".toRegex().find(line)!!.groupValues[1].split(",").map { it.trim().toInt() }
        
        return Machine(target = targetMask, buttons = buttonMasks, bits = bits, joltageTarget = joltageCounter, buttonCounters = buttonCounters)
    }
    
    
    fun Machine.solveBitMask(): Long {
        val maxMask = 1 shl bits
        
        val dp = LongArray(maxMask) { Long.MAX_VALUE / 2 }
        dp[0] = 0
        
        for (btn in buttons) {
            for (state in 0 until maxMask) {
                val next = state xor btn
                dp[next] = minOf(dp[next], dp[state] + 1)
            }
        }
        
        return dp[target]
    }
    
    
    /**
     * Für mehr infos: https://developers.google.com/optimization/cp?_gl=1*1bbeduj*_up*MQ..*_ga*MTU5MjE4Nzk0My4xNzY1MzQ5NjY0*_ga_SM8HXJ53K2*czE3NjUzNDk2NjMkbzEkZzAkdDE3NjUzNDk2NjMkajYwJGwwJGgw
     */
    fun Machine.solveJoltageLinearProgWithGoogleOrTools(): Long {
       
        val model = CpModel()
        val maxClicks = joltageTarget.max().toLong()

        // variablen initialisieren -> jeder button kann max = maxClicks geklickt werden
        val buttonClicks: Array<IntVar> = Array(buttonCounters.size) { j ->
            model.newIntVar(0, maxClicks, "button-$j")
        }
        
        // Constraints:
        // für jeden Counter i: Sum_j (buttonClicks[j] * A[i][j]) == target[i]
        for (i in joltageTarget.indices) {
            val buttonWithImpact = mutableListOf<IntVar>()
            
            buttonCounters.forEachIndexed { buttonIdx, btn ->
                if (btn.contains(i)) {
                    buttonWithImpact += buttonClicks[buttonIdx]
                }
            }
            
            val coeffs = LongArray(buttonWithImpact.size) { 1L } // A[i][j] - jeder hat den faktor 1 da nur 1 mal erhäht
            val rhs = joltageTarget[i].toLong()
            
            model.addEquality(
                LinearExpr.weightedSum(buttonWithImpact.toTypedArray(), coeffs),
                rhs
            )
        }
        
        // minimiere anzahl buttonClicks
        model.minimize(LinearExpr.sum(buttonClicks))
        
        val solver = CpSolver()
        solver.solve(model) // resultat prüfung ignoriert
        
        var total = 0L
        for (j in buttonCounters.indices) {
            total += solver.value(buttonClicks[j])
        }
        return total
    }

    fun part1(input: List<String>): Long {
        return input.sumOf { line ->
            parseMachine(line).solveBitMask()
        }
    }
    
    
    fun part2(input: List<String>): Long {
        var long = 0
        return input.sumOf { line ->
            println("solved line: ${++long}")
            parseMachine(line).solveJoltageLinearProgWithGoogleOrTools().toLong()
        }
    }

    val testInput = readInput("$day/test")
    check(part1(testInput) == 7L)
    check(part2(testInput) == 33L)
    
    val input = readInput("$day/input")
    part1(input).println()
    part2(input).println()
}
