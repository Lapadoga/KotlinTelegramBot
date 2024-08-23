import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

fun main(args: Array<String>) {

    val botToken = args[0]
    val urlGetMe = "https://api.telegram.org/bot$botToken/getMe"
    val urlGetUpdates = "https://api.telegram.org/bot$botToken/getUpdates"

    val client: HttpClient = HttpClient.newBuilder().build()

    val botInformationRequest: HttpRequest = HttpRequest.newBuilder().uri(URI(urlGetMe)).build()
    val botInformationResponse: HttpResponse<String> = client.send(botInformationRequest, HttpResponse.BodyHandlers.ofString())
    println(botInformationResponse.body())

    val botUpdatesRequest: HttpRequest = HttpRequest.newBuilder().uri(URI(urlGetUpdates)).build()
    val botUpdatesResponse: HttpResponse<String> = client.send(botUpdatesRequest, HttpResponse.BodyHandlers.ofString())
    println(botUpdatesResponse.body())

}