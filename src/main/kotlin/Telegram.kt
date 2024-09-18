fun main(args: Array<String>) {

    val botToken = args[0]
    var updateId = 0
    val telegramService = TelegramBotService(botToken)
    val trainer = try {
        LearnWordsTrainer()
    } catch (e: Exception) {
        println("Невозможно загрузить словарь")
        return
    }

    val dataRegex = "\"data\":\"(.+)\"".toRegex()
    val textMessageRegex = "\"text\":\"(.+?)\"".toRegex()
    val chatIdRegex = "\"chat\":\\{\"id\":(\\d+)".toRegex()
    val messageIdRegex = "\"update_id\":(\\d+)".toRegex()

    telegramService.setCommands()

    while (true) {
        Thread.sleep(2000)
        val updates = telegramService.getUpdates(updateId)
        println(updates)

        val updateIdString = telegramService.parseResponse(messageIdRegex, updates)
        if (updateIdString != null)
            updateId = updateIdString.toInt() + 1

        val chatIdString = telegramService.parseResponse(chatIdRegex, updates) ?: continue
        val text = telegramService.parseResponse(textMessageRegex, updates)
        val data = telegramService.parseResponse(dataRegex, updates)

        if (text != null) {
            if (text.equals("/start", ignoreCase = true))
                try {
                    val buttonsData = mapOf("Изучение слов" to LEARN_WORDS_CLICKED, "Статистика" to STATISTICS_CLICKED)
                    telegramService.sendMessage(chatIdString, "Основное меню", buttonsData)
                } catch (e: Exception) {
                    println(e.message)
                }
        }
        if (data != null) {
            when {
                data == LEARN_WORDS_CLICKED -> {
                    try {
                        telegramService.checkNextQuestionAndSend(trainer, chatIdString)
                    } catch (e: Exception) {
                        println(e.message)
                    }
                }

                data == STATISTICS_CLICKED -> {
                    try {
                        telegramService.sendMessage(chatIdString, trainer.getStatisticsString())
                    } catch (e: Exception) {
                        println(e.message)
                    }
                }

                data.startsWith(CALLBACK_DATA_ANSWER_PREFIX) -> {
                    val answerIndex = data.substringAfter('_').toInt()
                    if (trainer.checkAnswer(answerIndex))
                        telegramService.sendMessage(chatIdString, "Правильно!")
                    else {
                        val currentQuestion = trainer.getCurrentQuestion()
                        val message =
                            "Неправильно! ${currentQuestion?.correctAnswer?.original ?: ""} - это ${currentQuestion?.correctAnswer?.translate ?: ""}"
                        telegramService.sendMessage(chatIdString, message)
                    }
                    telegramService.checkNextQuestionAndSend(trainer, chatIdString)
                }
            }
        }
    }

}

