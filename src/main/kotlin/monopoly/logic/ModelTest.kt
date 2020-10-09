package monopoly.logic

import javafx.embed.swing.JFXPanel

fun main() {
    testGameFivePlayers(1000, Difficulty.Easy)
    //testGameTwoPlayers(1000, Difficulty.Hard, Difficulty.Easy)
    //testGameTwoPlayers(1000, Difficulty.Easy, Difficulty.Hard   )
    //testGameThreePlayers(1000, Difficulty.Easy, Difficulty.Medium, Difficulty.Hard)

    //one of the conclusions
    //there is dependence in the order of the move in the approximation
}

fun testGameFivePlayers(countOfSimulation: Int,difficulty: Difficulty) {
    val startTime = System.currentTimeMillis();
    val fxPanel = JFXPanel()
    var game = Game()
    var firstWin = 0.0
    var secondWin = 0.0
    var thirdWin = 0.0
    var fourthWin = 0.0
    var fifthWin = 5.0
    val visitFieldsAnalyse = IntArray(28)
    var cntOfMotion = 0
    for (i in 1..countOfSimulation) {// 100 game about 19 seconds
        game.delay = false
        game.data.add(Player(3))
        game.data.add(Player(4))
        game.data.add(Player(5))
        game.data.map { it.ai = true }
        game.data.map { it.aiDifficulty = difficulty }
        game.start()
        while (!game.gameIsEnd) Thread.sleep(50)
        when(game.gameWinner!!.id) {
            1 -> firstWin++
            2 -> secondWin++
            3 -> thirdWin++
            4 -> fourthWin++
            else -> fifthWin++
        }
        for (i in game.statsVisitedField.withIndex()) {
            visitFieldsAnalyse[i.index] += i.value
        }
        cntOfMotion += game.statsCntOfMotion
        game = Game()
    }
    val percentWinFirst: Double = firstWin / (firstWin + secondWin + thirdWin + fourthWin + fifthWin) * 100
    val percentWinSecond: Double = secondWin / (firstWin + secondWin + thirdWin + fourthWin + fifthWin) * 100
    val percentWinThird: Double = thirdWin / (firstWin + secondWin + thirdWin + fourthWin + fifthWin) * 100
    val percentWinFourth: Double = fourthWin / (firstWin + secondWin + thirdWin + fourthWin + fifthWin) * 100
    val percentWinFifth: Double = fifthWin / (firstWin + secondWin + thirdWin + fourthWin + fifthWin) * 100
    println("Статистика игр, пяти $difficulty ботов:")
    println("Первый выйграл - $firstWin это $percentWinFirst% игр")
    println("Второй выйграл - $secondWin, это $percentWinSecond% игр")
    println("Третий выйграл - $thirdWin, это $percentWinThird% игр")
    println("Четвертый выйграл - $fourthWin, это $percentWinFourth% игр")
    println("Пятый выйграл - $fifthWin, это $percentWinFifth% игр")
    visitFieldsAnalyse.map { print("[ $it ]") }
    println()
    println("Количество ходов - $cntOfMotion")
    println("Полей посещано - ${visitFieldsAnalyse.sum()}")
    val endTime = System.currentTimeMillis();
    val executionTime = (endTime - startTime) / 1000
    println("Время выполнения $executionTime секунд")
    return
}

fun testGameTwoPlayers(countOfSimulation: Int, first : Difficulty, second : Difficulty) {//Hard move first
    val startTime = System.currentTimeMillis();
    val fxPanel = JFXPanel()
    var game = Game()
    var easyWinCnt = 0.0
    var hardWinCnt = 0.0
    for (i in 1..countOfSimulation) {// 100 game about 8.5 seconds
        game.delay = false
        game.data[0].ai = true
        game.data[1].ai = true
        game.data[0].aiDifficulty = first
        game.data[1].aiDifficulty = second
        game.start()
        while (!game.gameIsEnd) Thread.sleep(50)
        if (game.gameWinner!!.aiDifficulty == Difficulty.Hard) hardWinCnt++
        else easyWinCnt++
        game = Game()
    }
    val percentWinHard: Double = hardWinCnt / (easyWinCnt + hardWinCnt) * 100
    println("Статистика игр, первый ходит сложный бот:")
    println("Легкий выйграл - $easyWinCnt, это ${100 - percentWinHard}% игр")
    println("Сложный выйграл - $hardWinCnt, это $percentWinHard% игр")
    val endTime = System.currentTimeMillis();
    val executionTime = (endTime - startTime) / 1000
    println("Время выполнения $executionTime секунд")
    return
}

fun testGameThreePlayers(countOfSimulation: Int, first: Difficulty, second: Difficulty, third: Difficulty) {
    val startTime = System.currentTimeMillis();
    val fxPanel = JFXPanel()
    var game = Game()
    var easyWinCnt = 0.0
    var mediumWinCnt = 0.0
    var hardWinCnt = 0.0
    for (i in 1..countOfSimulation) {// 100 game about 12 seconds
        game.delay = false
        game.data[0].ai = true
        game.data[1].ai = true
        game.data[0].aiDifficulty = first
        game.data[1].aiDifficulty = second
        game.data.add(Player(3))
        game.data[2].ai = true
        game.data[2].aiDifficulty = third
        game.start()
        while (!game.gameIsEnd) Thread.sleep(50)
        when (game.gameWinner!!.aiDifficulty) {
            Difficulty.Hard -> hardWinCnt++
            Difficulty.Medium -> mediumWinCnt++
            else -> easyWinCnt++
        }
        game = Game()
    }
    val percentWinHard: Double = hardWinCnt / (easyWinCnt + hardWinCnt + mediumWinCnt) * 100
    val percentWinMedium: Double = mediumWinCnt / (easyWinCnt + hardWinCnt + mediumWinCnt) * 100
    val percentWinEasy: Double = easyWinCnt / (easyWinCnt + hardWinCnt + mediumWinCnt) * 100
    println("Порядок: Сложный, Средний, Легкий")
    println("Легкий выйграл - $easyWinCnt, это $percentWinEasy% игр")
    println("Средний выйграл - $mediumWinCnt, это $percentWinMedium% игр")
    println("Сложный выйграл - $hardWinCnt, это $percentWinHard% игр")
    val endTime = System.currentTimeMillis();
    val executionTimne = (endTime - startTime) / 1000
    println("Время выполнения $executionTimne секунд")
    return
}