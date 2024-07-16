import java.io.File

fun main() {
    val file = File("words.txt")
    val listOfWords = mutableListOf<Word>()

    val fileLines = file.readLines()
    fileLines.forEach {
        val lineParts = it.split("|")
        val correctAnswers = try {
            if (lineParts[2] == "") 0 else lineParts[2].toInt()
        } catch (e: IndexOutOfBoundsException) {
            0
        }

        val word = Word(original = lineParts[0], translate = lineParts[1], correctAnswers)
        listOfWords.add(word)
    }

    listOfWords.forEach {
        println(it)
    }
}