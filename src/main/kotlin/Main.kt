import java.io.File

const val MIN_CORRECT_ANSWERS = 3
const val TEST_WORDS_COUNT = 4
const val FILE_PATH = "words.txt"

fun main() {
    val listOfWord = parseFile(FILE_PATH)

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

        when (userAnswer) {
            1 -> {
                val wordsLearned = startLearning(listOfWord)
                if (wordsLearned) {
                    println("Вы выучили все слова")
                    return
                }
            }

            2 -> println(statisticsString(listOfWord))
            0 -> break
            else -> println("Введено неверное значение, повторите ввод")
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

fun statisticsString(listOfWord: List<Word>): String {

    val listOfLearnedWord = listOfWord.filter { it.correctAnswersCount >= MIN_CORRECT_ANSWERS }
    val percentString = String.format("%.0f", listOfLearnedWord.size.toFloat() / listOfWord.size * 100)

    return "Выучено ${listOfLearnedWord.size} из ${listOfWord.size} слов | ${percentString}%"
}

fun startLearning(listOfWords: List<Word>): Boolean {

    while (true) {

        val listOfUnlearnedWords = listOfWords.filter { it.correctAnswersCount < MIN_CORRECT_ANSWERS }
        if (listOfUnlearnedWords.isEmpty())
            return true

        val shuffledList = listOfUnlearnedWords.shuffled()
        val testList = shuffledList.take(TEST_WORDS_COUNT)

        val wordIndex = testList.indices.random()
        println(testList[wordIndex].original)

        println(
            testList.mapIndexed { index, word ->
                "${index + 1}. ${word.translate}"
            }.joinToString(separator = "\n", postfix = "\n0. Меню")
        )

        val userAnswer = readln().toIntOrNull()

        when (userAnswer) {
            wordIndex + 1 -> {
                println("Правильно!")
                testList[wordIndex].correctAnswersCount++
                saveDictionary(listOfWords)
            }

            !in 0..TEST_WORDS_COUNT -> println("Неверный ввод")
            0 -> break
            else -> println("Неправильно - слово ${testList[wordIndex].translate}")
        }
    }

    return false
}

fun saveDictionary(listOfWords: List<Word>) {

    val file = File(FILE_PATH)
    file.writeText("")

    listOfWords.forEach {
        val stringToWrite = "${it.original}|${it.translate}|${it.correctAnswersCount}\n"
        file.appendText(stringToWrite)
    }

}


