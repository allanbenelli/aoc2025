import java.math.BigInteger
import java.security.MessageDigest
import kotlin.io.path.Path
import kotlin.io.path.readText
import kotlin.math.abs

/**
 * Reads lines from the given input txt file.
 */
fun readInput(name: String) = Path("src/main/kotlin/$name.txt").readText().trim().lines()

fun Char.parseToDigit() = this - '0'

fun StringBuilder.deleteAtEnd() =
    deleteCharAt(length - 1)

fun StringBuilder.toLength(n: Int): String =
    if (length > n) substring(0, n) else toString()


fun Int.mod(n: Int): Int = ((this % n) + n) % n



// All 8 directions

val diagonalDownRight = Pair(1,1)
val diagonalUpRight = Pair(-1,1)
val diagonalUpLeft = Pair(-1,-1)
val diagonalDownLeft = Pair(1,-1)
val directionUp = Pair(-1,0)
val directionLeft = Pair(0, -1)
val directionRight = Pair(0, 1)
val directionDown = Pair(1, 0)
val diagonalDirections = listOf(
    diagonalDownLeft,
    diagonalDownRight,
    diagonalUpLeft,
    diagonalUpRight
)
val normalDirections = listOf(
    directionUp,
    directionRight,
    directionDown,
    directionLeft
)
val directions = listOf(
    *normalDirections.toTypedArray(),
    *diagonalDirections.toTypedArray()
)

data class Point(val x: Int, val y: Int) {
    operator fun plus(other: Point) = Point(x + other.x, y + other.y)
    fun manhattanDistance(other: Point) = abs(x - other.x) + abs(y - other.y)
}




/**
 * Converts string to md5 hash.
 */
fun String.md5() = BigInteger(1, MessageDigest.getInstance("MD5").digest(toByteArray()))
    .toString(16)
    .padStart(32, '0')

/**
 * The cleaner shorthand for printing output.
 */
fun Any?.println() = println(this)
