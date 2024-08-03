import java.io.File

const val MIN_CORRECT_ANSWERS = 3
const val TEST_WORDS_COUNT = 4

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

    do {

        val listOfUnlearnedWords = listOfWords.filter { it.correctAnswersCount < MIN_CORRECT_ANSWERS }
        if (listOfUnlearnedWords.isEmpty())
            return true

        val shuffledList = listOfUnlearnedWords.shuffled()
        var testList = shuffledList.take(TEST_WORDS_COUNT)

        val testWord = testList[0]
        println(testWord.original)
        testList = testList.shuffled()
        println(
            testList.mapIndexed { index, word ->
                "${index + 1}. ${word.translate}"
            }.joinToString(separator = "\n", postfix = "\n0. Меню")
        )

        var userAnswer = readln().toIntOrNull()
        while (true) {

            if (userAnswer == null || userAnswer !in 0..TEST_WORDS_COUNT) {
                println("Неверный ввод, повторите попытку")
                userAnswer = readln().toIntOrNull()
            } else
                break
        }

        if (userAnswer != 0)
            if (testList.getOrNull(userAnswer!! - 1) == testWord)
                println("Верно!")
            else
                println("Неверно")

    } while (userAnswer != 0)

    return false
}


