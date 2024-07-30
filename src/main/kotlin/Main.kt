import java.io.File

const val MIN_CORRECT_ANSWERS = 3
const val LAST_MENU_ELEMENT = 2
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
        if (userAnswer == null || userAnswer !in 0..LAST_MENU_ELEMENT) {
            println("Введено неверное значение, повторите ввод")
            continue
        }

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
        val testList = shuffledList.take(TEST_WORDS_COUNT)

        val wordIndex = testList.indices.random()
        println(testList[wordIndex].original)

        println(
            """            
            -----------------------
            1. ${testList[0].translate}
            2. ${testList.getOrNull(1)?.translate ?: "---"}
            3. ${testList.getOrNull(2)?.translate ?: "---"}
            4. ${testList.getOrNull(3)?.translate ?: "---"}
            0. Меню
        """.trimIndent()
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
            if (userAnswer!! - 1 == wordIndex) {
                println("Правильно!")
                testList[wordIndex].correctAnswersCount++
                saveDictionary(listOfWords)
            } else
                println("Неправильно - слово ${testList[wordIndex].translate}")

    } while (userAnswer != 0)

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


