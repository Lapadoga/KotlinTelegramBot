fun main() {
    while (true) {
        println(
            """
            Меню:
            1 - Учить слова
            2 - Статистика
            0 - Выход
        """.trimIndent()
        )

        val userAnswer = readln().toIntOrNull()
        if (userAnswer == null || userAnswer !in 0..2) {
            println("Введено неверное значение, повторите ввод")
            continue
        }

        when (userAnswer) {
            1 -> println("Учить слова")
            2 -> println("Статистика")
            0 -> break
        }
    }
}