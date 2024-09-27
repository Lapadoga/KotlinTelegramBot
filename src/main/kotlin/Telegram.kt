import kotlinx.serialization.json.Json

fun main(args: Array<String>) {

    val botToken = args[0]
    var updateId = 0L
    val telegramService = TelegramBotService(botToken)
    val json = Json { ignoreUnknownKeys = true }

    val trainer = try {
        LearnWordsTrainer()
    } catch (e: Exception) {
        println("Невозможно загрузить словарь")
        return
    }

    telegramService.setCommands()

    while (true) {
        Thread.sleep(2000)
        val responseString = telegramService.getUpdates(updateId)
        println(responseString)

        val response = json.decodeFromString<Response>(responseString)
        val updates = response.result
        val firstUpdate = updates.firstOrNull() ?: continue
        val chatId = firstUpdate.message?.chat?.id ?: firstUpdate.callback?.message?.chat?.id ?: continue
        val text = firstUpdate.message?.text
        val data = firstUpdate.callback?.data

        updateId = firstUpdate.updateId + 1

        if (text != null) {
            if (text.equals("/start", ignoreCase = true))
                try {
                    val buttonsData = mapOf("Изучение слов" to LEARN_WORDS_CLICKED, "Статистика" to STATISTICS_CLICKED)
                    telegramService.sendMessage(json, chatId, "Основное меню", buttonsData)
                } catch (e: Exception) {
                    println(e.message)
                }
        }
        if (data != null) {
            when {
                data == LEARN_WORDS_CLICKED -> {
                    try {
                        telegramService.checkNextQuestionAndSend(json, trainer, chatId)
                    } catch (e: Exception) {
                        println(e.message)
                    }
                }

                data == STATISTICS_CLICKED -> {
                    try {
                        telegramService.sendMessage(json, chatId, trainer.getStatisticsString())
                    } catch (e: Exception) {
                        println(e.message)
                    }
                }

                data.startsWith(CALLBACK_DATA_ANSWER_PREFIX) -> {
                    val answerIndex = data.substringAfter('_').toInt()
                    if (trainer.checkAnswer(answerIndex))
                        telegramService.sendMessage(json, chatId, "Правильно!")
                    else {
                        val currentQuestion = trainer.getCurrentQuestion()
                        val message =
                            "Неправильно! ${currentQuestion?.correctAnswer?.original ?: ""} - это ${currentQuestion?.correctAnswer?.translate ?: ""}"
                        telegramService.sendMessage(json, chatId, message)
                    }
                    telegramService.checkNextQuestionAndSend(json, trainer, chatId)
                }
            }
        }
    }

}

