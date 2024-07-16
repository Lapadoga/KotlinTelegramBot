import java.io.File

fun main() {
    val file = File("words.txt")
    val listOfWords = mutableListOf<Word>()

    val fileLines = file.readLines()
    fileLines.forEach {
        val lineParts = it.split("|")
        val correctAnswers = if (lineParts[2] == "") 0 else lineParts[2].toInt()
        val word = Word(original = lineParts[0], translate = lineParts[1], correctAnswers)
        listOfWords.add(word)
    }

    listOfWords.forEach {
        println(it)
    }
}