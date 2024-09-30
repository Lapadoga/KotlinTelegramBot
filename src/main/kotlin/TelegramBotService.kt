import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpRequest.BodyPublishers
import java.net.http.HttpResponse

const val HOST = "https://api.telegram.org/"
const val STATISTICS_CLICKED = "statistics_clicked"
const val LEARN_WORDS_CLICKED = "learn_words_clicked"
const val RESET_CLICKED = "reset_progress_clicked"
const val CALLBACK_DATA_ANSWER_PREFIX = "answer_"

@Serializable
data class Response(
    @SerialName("result")
    val result: List<Result>,
)

@Serializable
data class Result(
    @SerialName("update_id")
    val updateId: Long,
    @SerialName("callback_query")
    val callback: Callback? = null,
    @SerialName("message")
    val message: Message? = null,
)

@Serializable
data class Callback(
    @SerialName("data")
    val data: String,
    @SerialName("message")
    val message: Message,
)

@Serializable
data class Message(
    @SerialName("text")
    val text: String,
    @SerialName("chat")
    val chat: Chat,
)

@Serializable
data class Chat(
    @SerialName("id")
    val id: Long,
)

@Serializable
data class Request(
    @SerialName("chat_id")
    val chatId: Long,
    @SerialName("text")
    val text: String,
    @SerialName("reply_markup")
    val replyMarkup: ReplyMarkup? = null,
)

@Serializable
data class ReplyMarkup(
    @SerialName("inline_keyboard")
    val inlineKeyboard: List<List<InlineKeyboardButton>>,
)

@Serializable
data class InlineKeyboardButton(
    @SerialName("text")
    val text: String,
    @SerialName("callback_data")
    val callbackData: String,
)

class TelegramBotService(botToken: String) {

    private val hostWithToken = "${HOST}bot$botToken/"
    private val json = Json { ignoreUnknownKeys = true }

    fun getUpdates(updateId: Long): Response {
        val urlGetUpdates = "${hostWithToken}getUpdates?offset=$updateId"
        val client: HttpClient = HttpClient.newBuilder().build()
        val request: HttpRequest = HttpRequest
            .newBuilder(URI(urlGetUpdates))
            .build()
        val response: HttpResponse<String> = client.send(request, HttpResponse.BodyHandlers.ofString())

        return json.decodeFromString(response.body())
    }

    fun sendMessage(chatId: Long, messageText: String, buttonsData: Map<String, String>? = null) {
        val urlSendMessage = "${hostWithToken}sendMessage"
        val replyMarkup = if (buttonsData == null)
            null
        else
            ReplyMarkup(listOf(buttonsData.map {
                InlineKeyboardButton(it.key, it.value)
            }))
        val requestBody = json.encodeToString(
            Request(
                chatId,
                messageText,
                replyMarkup
            )
        )

        val client = HttpClient.newBuilder().build()
        val request = HttpRequest.newBuilder(URI(urlSendMessage))
            .header("Content-type", "application/json")
            .POST(BodyPublishers.ofString(requestBody))
            .build()
        client.send(request, HttpResponse.BodyHandlers.ofString())
    }

    fun checkNextQuestionAndSend(trainer: LearnWordsTrainer, chatId: Long) {
        val question = trainer.getNextQuestion()
        if (question == null)
            sendMessage(chatId, "Вы выучили все слова в базе")
        else
            sendQuestion(chatId, question)
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

    private fun sendQuestion(chatId: Long, question: Question) {
        val buttonsData = mutableMapOf<String, String>()

        question.variants.forEachIndexed { index, word ->
            buttonsData[word.translate] = "$CALLBACK_DATA_ANSWER_PREFIX$index"
        }

        sendMessage(chatId, question.correctAnswer.original, buttonsData)
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