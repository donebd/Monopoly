package monopoly.logic

import javafx.embed.swing.JFXPanel

class AnalysisGame {

    val fieldEffectiveness = listOf(//efficiency of fields compared to the most effective purchased one
        0.7,//start
        0.716,//chanel, etc..
        0.732,
        0.733,
        0.733,
        0.752,
        0.753,
        1.909,
        0.738,
        0.852,
        0.835,
        0.971,
        0.971,
        1.0,
        0.925,
        0.969,
        0.892,
        0.928,
        0.937,
        0.936,
        0.922,
        0.910,
        0.908,
        0.852,
        0.789,
        0.724,
        0.651,
        0.695
    )//obtained during the experiment

    val chanceDiceDrop = listOf(
        0.0,//for convenience
        0.0,
        0.125,//2
        0.125,//3
        0.25,//4
        0.25,//5
        0.25,//6
        0.125,//7
        0.125//8
    )

    fun testGameFivePlayers(countOfSimulation: Int, difficulty: Difficulty) {
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
        for (i in 1..countOfSimulation) {// 100 game about 14 seconds
            game.delay = false
            game.data.add(Player(3))
            game.data.add(Player(4))
            game.data.add(Player(5))
            game.data.map { it.ai = true }
            game.data.map { it.aiDifficulty = difficulty }
            game.start()
            while (!game.gameIsEnd) Thread.sleep(10)
            when (game.gameWinner!!.id) {
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

    fun testGameTwoPlayers(countOfSimulation: Int, first: Difficulty, second: Difficulty) {//Hard move first
        val startTime = System.currentTimeMillis();
        val fxPanel = JFXPanel()
        var game = Game()
        var firstWinCnt = 0.0
        var secondWinCnt = 0.0
        for (i in 1..countOfSimulation) {// 100 game about 6 seconds
            game.delay = false
            game.data[0].ai = true
            game.data[1].ai = true
            game.data[0].aiDifficulty = first
            game.data[1].aiDifficulty = second
            game.start()
            while (!game.gameIsEnd) Thread.sleep(1)
            if (game.gameWinner!!.id == 2) secondWinCnt++
            else firstWinCnt++
            game = Game()
        }
        val percentWinSecond: Double = secondWinCnt / (firstWinCnt + secondWinCnt) * 100
        println("Статистика игр, первый ходит $first бот:")
        println("Первый $first выйграл - $firstWinCnt, это ${100 - percentWinSecond}% игр")
        println("Второй $second выйграл - $secondWinCnt, это $percentWinSecond% игр")
        val endTime = System.currentTimeMillis();
        val executionTime = (endTime - startTime) / 1000
        println("Время выполнения $executionTime секунд")
    }

    fun testGameThreePlayers(countOfSimulation: Int, first: Difficulty, second: Difficulty, third: Difficulty) {
        val startTime = System.currentTimeMillis();
        val fxPanel = JFXPanel()
        var game = Game()
        var firstWinCnt = 0.0
        var secondWinCnt = 0.0
        var thirdWinCnt = 0.0
        for (i in 1..countOfSimulation) {// 100 game about 10 seconds
            game.delay = false
            game.data[0].ai = true
            game.data[1].ai = true
            game.data[0].aiDifficulty = first
            game.data[1].aiDifficulty = second
            game.data.add(Player(3))
            game.data[2].ai = true
            game.data[2].aiDifficulty = third
            game.start()
            while (!game.gameIsEnd) Thread.sleep(5)
            when (game.gameWinner!!.id) {
                1 -> firstWinCnt++
                2 -> secondWinCnt++
                else -> thirdWinCnt++
            }
            game = Game()
        }
        val percentWinThird: Double = thirdWinCnt / (firstWinCnt + thirdWinCnt + secondWinCnt) * 100
        val percentWinSecond: Double = secondWinCnt / (firstWinCnt + thirdWinCnt + secondWinCnt) * 100
        val percentWinFirst: Double = firstWinCnt / (firstWinCnt + thirdWinCnt + secondWinCnt) * 100
        println("Порядок: $first, $second, $third")
        println("$first выйграл - $firstWinCnt, это $percentWinFirst% игр")
        println("$second выйграл - $secondWinCnt, это $percentWinSecond% игр")
        println("$third выйграл - $thirdWinCnt, это $percentWinThird% игр")
        val endTime = System.currentTimeMillis();
        val executionTime = (endTime - startTime) / 1000
        println("Время выполнения $executionTime секунд")
        return
    }
}

fun main() {
    val analysis = AnalysisGame()
    //analysis.testGameFivePlayers(1000, Difficulty.EASY)
    analysis.testGameTwoPlayers(10000, Difficulty.HARDEST, Difficulty.MEDIUM)
    analysis.testGameTwoPlayers(10000, Difficulty.MEDIUM, Difficulty.HARDEST)
    analysis.testGameTwoPlayers(10000, Difficulty.HARD, Difficulty.MEDIUM)
    analysis.testGameTwoPlayers(10000, Difficulty.MEDIUM, Difficulty.HARD)
    //analysis.testGameTwoPlayers(1000, Difficulty.Hard, Difficulty.Easy)
    //analysis.testGameTwoPlayers(1000, Difficulty.Easy, Difficulty.Hard   )
    //analysis.testGameThreePlayers(1000, Difficulty.Easy, Difficulty.Medium, Difficulty.Hard)

    //one of the conclusions
    //there is dependence in the order of the move in the approximation
}