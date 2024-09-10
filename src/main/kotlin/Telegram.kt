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
                    telegramService.sendMenu(chatIdString)
                } catch (e: Exception) {
                    println(e.message)
                }
        }
        if (data != null) {
            when (data.lowercase()) {
                LEARN_WORDS_CLICKED -> {
                    try {
                        telegramService.sendMessage(chatIdString, "Изучение слов")
                    } catch (e: Exception) {
                        println(e.message)
                    }
                }

                STATISTICS_CLICKED -> {
                    try {
                        telegramService.sendMessage(chatIdString, trainer.getStatisticsString())
                    } catch (e: Exception) {
                        println(e.message)
                    }
                }
            }
        }
    }

}

