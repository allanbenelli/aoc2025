import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.nio.file.Files
import java.nio.file.Path
import java.time.Duration

private val day = null

fun main(args: Array<String>) {
    val options = parseArgs(args.toList())
    val preProvideddayValue = options["day"] ?: day
    val dayValue = if (preProvideddayValue == null) {
        println("Please provide --day with a numeric value (e.g. --day=06)")
        readln()
    } else preProvideddayValue
    val dayNumber = dayValue.toIntOrNull() ?: error("--day must be a number, received '$dayValue'")
    require(dayNumber in 1..25) { "--day must be between 1 and 25" }
    
    val paddedDay = "%02d".format(dayNumber)
    val packageDir = Path.of("src/main/kotlin/day$paddedDay")
    Files.createDirectories(packageDir)
    
    val dayFile = packageDir.resolve("Day$paddedDay.kt")
    if (Files.notExists(dayFile)) {
        Files.writeString(dayFile, dayTemplate(paddedDay))
        println("Created ${dayFile.toAbsolutePath()}")
    } else {
        println("Day file already exists, skipping creation: ${dayFile.toAbsolutePath()}")
    }
    
    val testFile = packageDir.resolve("test.txt")
    if (Files.notExists(testFile)) {
        Files.writeString(testFile, "")
        println("Created empty test file at ${testFile.toAbsolutePath()}")
    }
    
    val sessionToken = options["session"]?.takeIf { it.isNotBlank() } ?: System.getenv("AOC_SESSION")
    if (sessionToken.isNullOrBlank()) {
        println("No session token provided; skipping input download. Set --session or AOC_SESSION to enable download.")
        ensureInputFile(packageDir.resolve("input.txt"))
        return
    }
    
    val inputFile = packageDir.resolve("input.txt")
    if (Files.exists(inputFile)) {
        println("Input file already exists, skipping download: ${inputFile.toAbsolutePath()}")
        return
    }
    
    downloadInput(dayNumber, sessionToken, inputFile)
}

private fun parseArgs(args: List<String>): Map<String, String> = buildMap {
    args.forEach { arg ->
        when {
            arg.startsWith("--day=") -> put("day", arg.substringAfter("--day="))
            arg.startsWith("--session=") -> put("session", arg.substringAfter("--session="))
            arg == "--help" || arg == "-h" -> {
                printUsage()
                kotlin.system.exitProcess(0)
            }
            else -> error("Unknown argument: $arg")
        }
    }
}

private fun ensureInputFile(inputFile: Path) {
    if (Files.notExists(inputFile)) {
        Files.writeString(inputFile, "")
        println("Created empty input file at ${inputFile.toAbsolutePath()}")
    }
}

private fun downloadInput(dayNumber: Int, session: String, targetFile: Path) {
    val url = URI("https://adventofcode.com/2025/day/$dayNumber/input")
    val request = HttpRequest.newBuilder(url)
        .header("Cookie", "session=$session")
        .header("User-Agent", "github.com/oliverbenelli/aoc2025 prepare-day script")
        .timeout(Duration.ofSeconds(15))
        .GET()
        .build()
    
    val client = HttpClient.newBuilder()
        .connectTimeout(Duration.ofSeconds(10))
        .build()
    
    val response = client.send(request, HttpResponse.BodyHandlers.ofString())
    if (response.statusCode() != 200) {
        error("Failed to download input (status ${response.statusCode()}): ${response.body().take(200)}")
    }
    
    Files.writeString(targetFile, response.body().trimEnd() + "\n")
    println("Downloaded input to ${targetFile.toAbsolutePath()}")
}

private fun dayTemplate(paddedDay: String) = """
    package day$paddedDay

    import println
    import readInput

    private val day = "day$paddedDay"
    fun main() {
        
        fun part1(input: List<String>): Long {
            var count = 0L

            return count
        }
        
        
        fun part2(input: List<String>): Long {
            var count = 0L
        
            return count
        }

        val testInput = readInput("${'$'}day/test")
        //check(part1(testInput) == 3L)
        //check(part2(testInput) == 14L)
        
        val input = readInput("${'$'}day/input")
        part1(input).println()
        part2(input).println()
    }
""".trimIndent() + "\n"

private fun printUsage() {
    println(
        """
        Usage: kotlin -classpath build/libs/aoc2025-1.0-SNAPSHOT.jar PrepareDayKt --day=XX [--session=TOKEN]

        Options:
          --day=XX        Two-digit day number to scaffold (e.g. 06)
          --session=TOKEN Optional Advent of Code session cookie for downloading input.
                           Falls back to AOC_SESSION environment variable.
          -h, --help      Show this help message.
        """.trimIndent()
    )
}