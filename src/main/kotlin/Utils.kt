import kotlin.io.path.Path
import kotlin.io.path.readText

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