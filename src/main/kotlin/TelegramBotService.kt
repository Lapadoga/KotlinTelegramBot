import java.net.URI
import java.net.URLEncoder
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpRequest.BodyPublishers
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
        val encodedMessage = URLEncoder.encode(messageText, Charsets.UTF_8)
        val urlSendMessage = "${hostWithToken}sendMessage?chat_id=$chatId&text=$encodedMessage"
        val client = HttpClient.newBuilder().build()
        val request = HttpRequest.newBuilder(URI(urlSendMessage)).build()
        client.send(request, HttpResponse.BodyHandlers.ofString())
    }

    fun sendMenu(chatId: String) {
        val urlSendMessage = "${hostWithToken}sendMessage"
        val menuBody = """
            {
            	"chat_id": $chatId,
            	"text": "Основное меню",
            	"reply_markup": {
            		"inline_keyboard": [
            			[
            				{
            					"text": "Изучить слова",
            					"callback_data": "learn_words_clicked"
            				}
            			],
            			[
            				{
            					"text": "Статистика",
            					"callback_data": "statistics_clicked"
            				}
            			]
            		]
            	}
            }
        """.trimIndent()

        val client = HttpClient.newBuilder().build()
        val request = HttpRequest.newBuilder(URI(urlSendMessage))
            .header("Content-type", "application/json")
            .POST(BodyPublishers.ofString(menuBody))
            .build()
        client.send(request, HttpResponse.BodyHandlers.ofString())
    }

    fun parseResponse(regex: Regex, response: String): String? {
        val matchChatIdResult = regex.find(response)
        val result = matchChatIdResult?.groups?.get(1)?.value

        return result
    }

    fun setCommands() {
        val urlSendMessage = "${hostWithToken}setMyCommands"
        val commandsBody = """
            {
            	"commands": [
            			{
            				"command": "/start",
            				"description": "Запустить бота"
            			}
            		]
            }
        """.trimIndent()

        val client = HttpClient.newBuilder().build()
        val request = HttpRequest.newBuilder(URI(urlSendMessage))
            .header("Content-type", "application/json")
            .POST(BodyPublishers.ofString(commandsBody))
            .build()
        client.send(request, HttpResponse.BodyHandlers.ofString())
    }
}

data class Word(
    val original: String,
    val translate: String,
    var correctAnswersCount: Int = 0,
)