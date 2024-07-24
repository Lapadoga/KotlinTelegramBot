import java.io.File

const val MIN_CORRECT_ANSWERS = 3
const val LAST_MENU_ELEMENT = 2

fun main() {
    val listOfWord = parseFile("words.txt")

    while (true) {
        println(
            """
            Меню:
            1 - Учить слова
            2 - Статистика
            0 - Выход
        """.trimIndent()
        )

        val userAnswer = readln().toIntOrNull()
        if (userAnswer == null || userAnswer !in 0..LAST_MENU_ELEMENT) {
            println("Введено неверное значение, повторите ввод")
            continue
        }

        when (userAnswer) {
            1 -> println("Учить слова")
            2 -> {
                val listOfLearnedWord = listOfWord.filter { it.correctAnswersCount >= MIN_CORRECT_ANSWERS }
                val percentString = String.format("%.0f", listOfLearnedWord.size.toFloat() / listOfWord.size * 100)
                println(
                    "Выучено ${listOfLearnedWord.size} из ${listOfWord.size} слов | ${percentString}%"
                )
            }

            0 -> break
        }
    }
}

fun parseFile(path: String): List<Word> {
    val file = File(path)
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
    return listOfWords
}
