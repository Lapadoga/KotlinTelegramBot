import java.io.File

fun main() {
    val file = File("words.txt")
    val listOfWords = mutableListOf<Word>()

    val fileLines = file.readLines()
    fileLines.forEach {
        val lineParts = it.split("|")

        val word = Word(
            original = lineParts[0],
            translate = lineParts[1],
            correctAnswersCount = lineParts.getOrNull(2)?.toIntOrNull() ?: 0
        )
        listOfWords.add(word)
    }

    listOfWords.forEach {
        println(it)
    }
}