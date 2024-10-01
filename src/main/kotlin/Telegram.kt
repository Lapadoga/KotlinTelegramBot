fun main(args: Array<String>) {

    val botToken = args[0]
    var updateId = 0L
    val telegramService = TelegramBotService(botToken)
    val trainers = hashMapOf<Long, LearnWordsTrainer>()

    telegramService.setCommands()

    while (true) {
        Thread.sleep(2000)
        val response = telegramService.getUpdates(updateId)
        println(response)

        if (response.result.isEmpty()) continue
        val sortedUpdates = response.result.sortedBy { it.updateId }
        sortedUpdates.forEach { handleUpdate(it, telegramService, trainers) }
        updateId = sortedUpdates.last().updateId + 1
    }
}

fun handleUpdate(
    update: Result,
    telegramService: TelegramBotService,
    trainers: HashMap<Long, LearnWordsTrainer>
) {
    val chatId = update.message?.chat?.id ?: update.callback?.message?.chat?.id ?: return
    val text = update.message?.text
    val data = update.callback?.data
    val trainer = try {
        trainers.getOrPut(chatId) { LearnWordsTrainer("$chatId.txt") }
    } catch (e: Exception) {
        println("Невозможно загрузить словарь для $chatId")
        return
    }

    when {
        text != null && text.equals("/start", ignoreCase = true) -> try {
            val buttonsData = mapOf(
                "Изучение слов" to LEARN_WORDS_CLICKED,
                "Статистика" to STATISTICS_CLICKED,
                "Сбросить прогресс" to RESET_CLICKED
            )
            telegramService.sendMessage(chatId, "Основное меню", buttonsData)
        } catch (e: Exception) {
            println(e.message)
        }

        data != null -> {
            when {
                data == LEARN_WORDS_CLICKED -> {
                    try {
                        telegramService.checkNextQuestionAndSend(trainer, chatId)
                    } catch (e: Exception) {
                        println(e.message)
                    }
                }

                data == STATISTICS_CLICKED -> {
                    try {
                        telegramService.sendMessage(chatId, trainer.getStatisticsString())
                    } catch (e: Exception) {
                        println(e.message)
                    }
                }

                data == RESET_CLICKED -> {
                    trainer.resetProgress()
                    telegramService.sendMessage(chatId, "Прогресс сброшен")
                }

                data.startsWith(CALLBACK_DATA_ANSWER_PREFIX) -> {
                    val answerIndex = data.substringAfter('_').toInt()
                    if (trainer.checkAnswer(answerIndex))
                        telegramService.sendMessage(chatId, "Правильно!")
                    else {
                        val currentQuestion = trainer.getCurrentQuestion()
                        val message =
                            "Неправильно! ${currentQuestion?.correctAnswer?.original ?: ""} - это ${currentQuestion?.correctAnswer?.translate ?: ""}"
                        telegramService.sendMessage(chatId, message)
                    }
                    telegramService.checkNextQuestionAndSend(trainer, chatId)
                }
            }
        }
    }
}

