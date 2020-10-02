package monopoly.`interface`

import javafx.embed.swing.JFXPanel
import monopoly.logic.Difficulty
import monopoly.logic.Game
import monopoly.logic.Player

fun main() {
    testHardVsEasy()
    testEasyVsHard()
    EasyVsMediumVsHard()
    MediumVsEasyVsHard()

    //one of the conclusions
    //there is no dependence in the order of the move in the approximation
}

fun testHardVsEasy() {//Hard move first
    val startTime = System.currentTimeMillis();
    val fxPanel = JFXPanel()
    var game = Game()
    var easyWinCnt = 0.0
    var hardWinCnt = 0.0
    for (i in 0..999) {// 100 game about 8.5 seconds
        game.delay = false
        game.data[0].ai = true
        game.data[1].ai = true
        game.data[0].aiDifficulty = Difficulty.Hard
        game.data[1].aiDifficulty = Difficulty.Easy
        game.motion()
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
    val executionTimne = (endTime - startTime) / 1000
    println("Время выполнения $executionTimne секунд")
    return
}

fun testEasyVsHard() {//Easy move first
    val startTime = System.currentTimeMillis();
    val fxPanel = JFXPanel()
    var game = Game()
    var easyWinCnt = 0.0
    var hardWinCnt = 0.0
    for (i in 0..999) {// 100 game about 8.5 seconds
        game.delay = false
        game.data[0].ai = true
        game.data[1].ai = true
        game.data[1].aiDifficulty = Difficulty.Hard
        game.data[0].aiDifficulty = Difficulty.Easy
        game.motion()
        while (!game.gameIsEnd) Thread.sleep(50)
        if (game.gameWinner!!.aiDifficulty == Difficulty.Hard) hardWinCnt++
        else easyWinCnt++
        game = Game()
    }
    val percentWinHard: Double = hardWinCnt / (easyWinCnt + hardWinCnt) * 100
    println("Статистика игр, первый ходит легкий бот:")
    println("Легкий выйграл - $easyWinCnt, это ${100 - percentWinHard}% игр")
    println("Сложный выйграл - $hardWinCnt, это $percentWinHard% игр")
    val endTime = System.currentTimeMillis();
    val executionTimne = (endTime - startTime) / 1000
    println("Время выполнения $executionTimne секунд")
    return
}

fun EasyVsMediumVsHard() {
    val startTime = System.currentTimeMillis();
    val fxPanel = JFXPanel()
    var game = Game()
    var easyWinCnt = 0.0
    var mediumWinCnt = 0.0
    var hardWinCnt = 0.0
    for (i in 0..999) {// 100 game about 12 seconds
        game.delay = false
        game.data[0].ai = true
        game.data[1].ai = true
        game.data[0].aiDifficulty = Difficulty.Hard
        game.data[1].aiDifficulty = Difficulty.Medium
        game.data.add(Player(3))
        game.data[2].ai = true
        game.data[2].aiDifficulty = Difficulty.Easy
        game.cntPls = 3
        game.motion()
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

fun MediumVsEasyVsHard() {
    val startTime = System.currentTimeMillis();
    val fxPanel = JFXPanel()
    var game = Game()
    var easyWinCnt = 0.0
    var mediumWinCnt = 0.0
    var hardWinCnt = 0.0
    for (i in 0..999) {// 100 game about 12 seconds
        game.delay = false
        game.data[0].ai = true
        game.data[1].ai = true
        game.data[0].aiDifficulty = Difficulty.Easy
        game.data[1].aiDifficulty = Difficulty.Hard
        game.data.add(Player(3))
        game.data[2].ai = true
        game.data[2].aiDifficulty = Difficulty.Medium
        game.cntPls = 3
        game.motion()
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
    println("Порядок: Легкий, Сложный, Средний")
    println("Легкий выйграл - $easyWinCnt, это $percentWinEasy% игр")
    println("Средний выйграл - $mediumWinCnt, это $percentWinMedium% игр")
    println("Сложный выйграл - $hardWinCnt, это $percentWinHard% игр")
    val endTime = System.currentTimeMillis();
    val executionTimne = (endTime - startTime) / 1000
    println("Время выполнения $executionTimne секунд")
    return
}