fun main(args: Array<String>) {

    val botToken = args[0]
    var updateId = 0
    val telegramService = TelegramBotService(botToken)

    while (true) {
        Thread.sleep(2000)
        val updates = telegramService.getUpdates(updateId)
        println(updates)

        val messageIdRegex = "\"update_id\":(\\d+)".toRegex()
        val updateIdString = telegramService.parseResponse(messageIdRegex, updates)
        if (updateIdString != null)
            updateId = updateIdString.toInt() + 1

        val chatIdRegex = "\"chat\":\\{\"id\":(\\d+)".toRegex()
        val chatIdString = telegramService.parseResponse(chatIdRegex, updates)

        val textMessageRegex = "\"text\":\"(.+)\"".toRegex()
        val text = telegramService.parseResponse(textMessageRegex, updates)

        if (chatIdString != null && text != null) {
            val result = telegramService.sendMessage(chatIdString, text)
            if (result)
                println("Сообщение отправлено")
            else
                println("Ошибка отправки сообщения")
        }
    }

}

