import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

class TelegramBotService(private val botToken: String) {

    fun getUpdates(updateId: Int): String {
        val urlGetUpdates = "https://api.telegram.org/bot$botToken/getUpdates?offset=$updateId"
        val client: HttpClient = HttpClient.newBuilder().build()
        val request: HttpRequest = HttpRequest
            .newBuilder(URI(urlGetUpdates))
            .build()
        val response: HttpResponse<String> = client.send(request, HttpResponse.BodyHandlers.ofString())

        return String(response.body().toByteArray(), Charsets.UTF_8)
    }

    fun sendMessage(chatId: String, messageText: String): Boolean {

        var result = false
        val successCode = 200

        try {
            val urlSendMessage = "https://api.telegram.org/bot$botToken/sendMessage?chat_id=$chatId&text=$messageText"
            val client = HttpClient.newBuilder().build()
            val request = HttpRequest.newBuilder(URI(urlSendMessage)).build()
            val response = client.send(request, HttpResponse.BodyHandlers.ofString())
            if (response.statusCode() == successCode) result = true
        } catch (e: Exception) {
            return false
        }

        return result
    }

    fun parseResponse(regex: Regex, response: String): String? {
        val matchChatIdResult = regex.find(response)

        return matchChatIdResult?.groups?.get(1)?.value
    }

}