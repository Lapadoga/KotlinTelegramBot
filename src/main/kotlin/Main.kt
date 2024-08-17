fun main() {

    val trainer = LearnWordsTrainer()

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
                while (true) {
                    val question = trainer.getNextQuestion()

                    if (question == null) {
                        println("Вы выучили все слова")
                        break

                    } else {
                        println(question.asConsoleString())

                        val userAnswerInput = readln().toIntOrNull()

                        if (userAnswerInput == 0)
                            break
                        if (trainer.checkAnswer(userAnswerInput?.minus(1)))
                            println("Правильно!")
                        else
                            println("Неправильно! ${question.correctAnswer.original} - это ${question.correctAnswer.translate}")

                    }
                }
            }

            2 -> {
                val statistics = trainer.getStatistics()
                println("Выучено ${statistics.learned} из ${statistics.total} слов | ${statistics.percent}%")
            }

            0 -> break
            else -> println("Введено неверное значение, повторите ввод")
        }
    }
}

fun Question.asConsoleString(): String {

    return this.variants.mapIndexed { index, word ->
        "${index + 1}. ${word.translate}"
    }.joinToString(separator = "\n", postfix = "\n0. Меню", prefix = this.correctAnswer.original + "\n")
}



