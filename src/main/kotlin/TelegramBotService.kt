import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpRequest.BodyPublishers
import java.net.http.HttpResponse

const val HOST = "https://api.telegram.org/"
const val STATISTICS_CLICKED = "statistics_clicked"
const val LEARN_WORDS_CLICKED = "learn_words_clicked"
const val CALLBACK_DATA_ANSWER_PREFIX = "answer_"

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

    fun sendMessage(chatId: String, messageText: String, buttonsData: Map<String, String>? = null) {
        val urlSendMessage = "${hostWithToken}sendMessage"
        val buttons = if (buttonsData == null) "" else getButtonsString(buttonsData)
        val requestBody = """
            {
            	"chat_id": $chatId,
            	"text": "$messageText"$buttons
            }
        """.trimIndent()

        val client = HttpClient.newBuilder().build()
        val request = HttpRequest.newBuilder(URI(urlSendMessage))
            .header("Content-type", "application/json")
            .POST(BodyPublishers.ofString(requestBody))
            .build()
        client.send(request, HttpResponse.BodyHandlers.ofString())
    }

    fun checkNextQuestionAndSend(trainer: LearnWordsTrainer, chatId: String) {
        val question = trainer.getNextQuestion()
        if (question == null)
            sendMessage(chatId, "Вы выучили все слова в базе")
        else
            sendQuestion(chatId, question)
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

    private fun sendQuestion(chatId: String, question: Question) {
        val buttonsData = mutableMapOf<String, String>()

        question.variants.forEachIndexed { index, word ->
            buttonsData[word.translate] = "$CALLBACK_DATA_ANSWER_PREFIX$index"
        }

        sendMessage(chatId, question.correctAnswer.original, buttonsData)
    }

    private fun getButtonsString(buttonsData: Map<String, String>): String {
        val buttonsList = mutableListOf<String>()
        buttonsData.forEach { (t, u) ->
            buttonsList.add(getButtonTemplate(t, u))
        }
        val buttonsString = buttonsList.joinToString(",\n")
        val result = """,
            "reply_markup": {
                "inline_keyboard": [
                    $buttonsString
                ]
            }
        """.trimIndent()

        return result
    }

    private fun getButtonTemplate(text: String, callbackData: String): String {
        return """
            [
                {
                    "text": "$text",
                    "callback_data": "$callbackData"
                }
            ]
        """.trimIndent()
    }
}

data class Word(
    val original: String,
    val translate: String,
    var correctAnswersCount: Int = 0,
)

data class Question(
    val variants: List<Word>,
    val correctAnswer: Word,
)