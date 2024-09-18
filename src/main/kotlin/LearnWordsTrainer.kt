import java.io.File

const val FILE_PATH = "words.txt"

data class Statistics(
    val total: Int,
    val learned: Int,
    val percent: Int,
)

class LearnWordsTrainer(private val learnedWordsCount: Int = 3, private val countOfQuestionWords: Int = 4) {

    private val dictionary = loadDictionary(FILE_PATH)
    private var question: Question? = null

    fun getStatistics(): Statistics {

        val learned = dictionary.filter { it.correctAnswersCount >= learnedWordsCount }.size
        val total = dictionary.size
        val percent = learned * 100 / total

        return Statistics(total, learned, percent)
    }

    fun getStatisticsString(): String {
        val statistics = getStatistics()

        return "Выучено ${statistics.learned} из ${statistics.total} слов | ${statistics.percent}%"
    }

    fun getNextQuestion(): Question? {

        val listOfUnlearnedWords = dictionary.filter { it.correctAnswersCount < learnedWordsCount }.toMutableList()
        if (listOfUnlearnedWords.isEmpty()) return null
        if (listOfUnlearnedWords.size < countOfQuestionWords) {
            val learnedList = dictionary.filter { it.correctAnswersCount >= countOfQuestionWords }.shuffled()
            listOfUnlearnedWords += learnedList.take(countOfQuestionWords - listOfUnlearnedWords.size)
        }

        val testList = listOfUnlearnedWords.shuffled().take(countOfQuestionWords)

        question = Question(
            testList,
            testList.random()
        )

        return question
    }

    fun checkAnswer(userAnswerIndex: Int?): Boolean {

        return question?.let {
            val correctAnswerIndex = it.variants.indexOf(it.correctAnswer)

            if (correctAnswerIndex == userAnswerIndex) {
                it.correctAnswer.correctAnswersCount++
                saveDictionary(dictionary)
                true
            } else
                false
        } ?: false
    }

    fun getCurrentQuestion(): Question? {
        return question
    }

    private fun loadDictionary(path: String): List<Word> {

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

    private fun saveDictionary(listOfWords: List<Word>) {

        val file = File(FILE_PATH)
        file.writeText("")

        listOfWords.forEach {
            val stringToWrite = "${it.original}|${it.translate}|${it.correctAnswersCount}\n"
            file.appendText(stringToWrite)
        }
    }
}