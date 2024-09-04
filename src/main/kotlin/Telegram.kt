fun main(args: Array<String>) {

    val botToken = args[0]
    var updateId = 0
    val telegramService = TelegramBotService(botToken)

    val dataRegex = "\"data\":\"(.+)\"".toRegex()
    val textMessageRegex = "\"text\":\"(.+)\"".toRegex()
    val chatIdRegex = "\"chat\":\\{\"id\":(\\d+)".toRegex()
    val messageIdRegex = "\"update_id\":(\\d+)".toRegex()

    while (true) {
        Thread.sleep(2000)
        val updates = telegramService.getUpdates(updateId)
        println(updates)

        val updateIdString = telegramService.parseResponse(messageIdRegex, updates)
        if (updateIdString != null)
            updateId = updateIdString.toInt() + 1

        val chatIdString = telegramService.parseResponse(chatIdRegex, updates)
        val text = telegramService.parseResponse(textMessageRegex, updates)
        val data = telegramService.parseResponse(dataRegex, updates)

        if (chatIdString != null && text != null) {
            if (text.equals("menu", ignoreCase = true))
                try {
                    telegramService.sendMenu(chatIdString)
                } catch (e: Exception) {
                    println(e.message)
                }
        }
        if (chatIdString != null && data != null) {
            when (data.lowercase()) {
                "learn_words_clicked" -> {
                    try {
                        telegramService.sendMessage(chatIdString, "Изучение слов")
                    } catch (e: Exception) {
                        println(e.message)
                    }
                }

                "statistics_clicked" -> {
                    try {
                        telegramService.sendMessage(chatIdString, "Выучено 10 из 10 слов | 100%")
                    } catch (e: Exception) {
                        println(e.message)
                    }
                }
            }
        }
    }

}

