import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

const val HOST = "https://api.telegram.org/"

class TelegramBotService(botToken: String) {

    private val hostWithToken = "${HOST}bot$botToken/"

    fun getUpdates(updateId: Int): String {
        val urlGetUpdates = "${hostWithToken}getUpdates?offset=$updateId"
        val client: HttpClient = HttpClient.newBuilder().build()
        val request: HttpRequest = HttpRequest
            .newBuilder(URI(urlGetUpdates))
            .build()
        val response: HttpResponse<String> = client.send(request, HttpResponse.BodyHandlers.ofString())

        return response.body()
    }

    fun sendMessage(chatId: String, messageText: String) {
        val urlSendMessage = "${hostWithToken}sendMessage?chat_id=$chatId&text=$messageText"
        val client = HttpClient.newBuilder().build()
        val request = HttpRequest.newBuilder(URI(urlSendMessage)).build()
        client.send(request, HttpResponse.BodyHandlers.ofString())
    }

    fun parseResponse(regex: Regex, response: String): String? {
        val matchChatIdResult = regex.find(response)

        return matchChatIdResult?.groups?.get(1)?.value
    }

}