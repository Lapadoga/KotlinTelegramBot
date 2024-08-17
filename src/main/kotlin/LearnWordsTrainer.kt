import java.io.File

const val MIN_CORRECT_ANSWERS = 3
const val TEST_WORDS_COUNT = 4
const val FILE_PATH = "words.txt"

data class Statistics(
    val total: Int,
    val learned: Int,
    val percent: Int,
)

data class Question(
    val variants: List<Word>,
    val correctAnswer: Word,
)

class LearnWordsTrainer {

    private val dictionary = loadDictionary(FILE_PATH)
    private var question: Question? = null

    fun getStatistics(): Statistics {

        val learned = dictionary.filter { it.correctAnswersCount >= MIN_CORRECT_ANSWERS }.size
        val total = dictionary.size
        val percent = learned * 100 / total

        return Statistics(total, learned, percent)
    }

    fun getNextQuestion(): Question? {

        val listOfUnlearnedWords = dictionary.filter { it.correctAnswersCount < MIN_CORRECT_ANSWERS }
        if (listOfUnlearnedWords.isEmpty()) return null

        val shuffledList = listOfUnlearnedWords.shuffled()
        val testList = shuffledList.take(TEST_WORDS_COUNT)

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