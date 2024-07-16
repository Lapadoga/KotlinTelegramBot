import java.io.File

fun main() {
    val file = File("words.txt")

    val lines = file.readLines()
    lines.forEach {
        println(it)
    }
}