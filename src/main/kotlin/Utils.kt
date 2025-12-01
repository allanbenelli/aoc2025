import kotlin.io.path.Path
import kotlin.io.path.readText

/**
 * Reads lines from the given input txt file.
 */
fun readInput(name: String) = Path("src/main/kotlin/$name.txt").readText().trim().lines()


fun Int.mod(n: Int): Int = ((this % n) + n) % n