package monopoly.logic


import javafx.beans.property.SimpleBooleanProperty
import tornadofx.runAsync
import tornadofx.ui

class Game {

    var statsCntOfMotion = 0
    val statsVisitedField = IntArray(28)
    var delay = true

    //manipulate with view
    var gameIsEnd = false
    var gameWinner: Player? = null
    var endProperty = SimpleBooleanProperty(gameIsEnd)
    val view = NotifyInView()

    val data = mutableListOf<Player>()
    var cntPls = 0 // count of players
    val board = GameBoard()
    var motionPlayer = 0
    var currentPlayer = Player(1) //the player who is currently walking in action
    var loosers = mutableListOf<Int>()
    val dice = Dice()
    var event = Event(this)
    var playerAnswer = FeedBackPlayer(this)
    var currentExchange = ExchangeOffer()
    var currentAct = FieldAction() // for action with realty
    var prisonByDouble = false
    var pause = false

    init {
        data.add(currentPlayer)
        data.add(Player(2))
        currentPlayer = data.first()
        cntPlsUpdate()
    }

    fun cntPlsUpdate() {
        cntPls = data.size
    }

    fun triggerProperty(property: SimpleBooleanProperty) {
        property.value = !property.value
    }

    fun canExchange(sender: Player, receiver: Player): Boolean {
        if (sender.ai || currentPlayer.id != sender.id) return false
        currentExchange = ExchangeOffer(sender, receiver)
        currentExchange.exchangePause = sender.id != receiver.id && receiver.id !in loosers
        return currentExchange.exchangePause
    }

    fun canControl(number: Int): Boolean {
        if (board.fields[number].owner == currentPlayer && !currentPlayer.ai) {
            currentAct = FieldAction(currentPlayer, board.fields[number])
            return true
        }
        return false
    }

    private fun setBalance() {
        if (data.size == 2) {
            for (i in 0..1) data[i].moneyChange(10000)
        }
        if (data.size == 3) {
            for (i in 0..2) data[i].moneyChange(1000)
        }
        if (data.size == 4) {
            for (i in 0..2) data[i].moneyChange(-2000)
        }
        if (data.size == 5) {
            for (i in 0..4) data[i].moneyChange(-5000)
        }
    }

    fun start(){
        cntPlsUpdate()
        setBalance()
        data.map {
            it.aiInstruction = when(it.aiDifficulty) {
            Difficulty.EASY -> AIEasy(this, it)
            Difficulty.MEDIUM -> AIMedium(this, it)
            Difficulty.HARD -> AIHard(this,it)
            else -> AIHardest(this, it)
        }
        }//initialize ai instruction depending on the difficulty
        if (data.first().ai) motion()
    }

    //---------------------------------------------------------------
    //---------------------------------------------------------------
    //-------------------The logic of one move-----------------------
    //---------------------------------------------------------------
    //---------------------------------------------------------------

    fun motion() {//function start
        val player = currentPlayer
        if (player.playerInPrison()) {
            triggerProperty(view.prisonInitProperty)
            if (player.ai) {
                player.aiInstruction.prisonInstructions()
            }
            return
        }
        dice.roll()
        runAsync { if (delay) Thread.sleep(500) } ui {
            if (event.checkPrisonByDouble(player)) {
                event.playerToPrison(player)
            } else {
                playerMove(player)
            }
        }
    }

    fun playerMove(player: Player) {
        runAsync {
            if (delay) Thread.sleep(250)
        } ui {
            player.positionChange(dice.count)
            statsVisitedField[player.position]++

            runAsync {
                if (delay) Thread.sleep(500)
            } ui {
                event.handling(player)
            }
        }
    }

    fun endMotion() {
        runAsync { if (delay) Thread.sleep(300) } ui {
            endMotionLogic()
            triggerProperty(view.viewEndMotionProperty)
            val player = currentPlayer
            if (!dice.double && !player.justOutJail) {
                view.notifyInView.value = ""
                view.notifyInView.value = ("Ваш ход, ${player.name} !")
            }
            player.justOutJail = false
            if (player.ai) {
                for (i in player.aiInstruction.instructions()) {
                    view.notifyInView.value =
                        (player.name + " строит филиал. Количество филиалов на поле " + board.fields[i].name + " - " + board.fields[i].upgrade)
                }
                triggerProperty(view.updateUpgradeView)
            }
            runAsync {
                while (currentExchange.exchangePause || pause) {
                    Thread.sleep(10)
                }
                runAsync { if (delay) Thread.sleep(500) } ui {
                    if ((player.playerInPrison() || player.ai) && !gameIsEnd) {
                        motion()
                    }
                }
            }
        }
    }

    fun endMotionLogic() {
        currentPlayer.currentMotionUpgrade.clear()
        if (!dice.double && !currentPlayer.justOutJail) {
            motionPlayer++
            motionPlayer %= cntPls
            statsCntOfMotion++
        }
        while (motionPlayer in loosers) {
            motionPlayer++
            motionPlayer %= cntPls
        }
        currentPlayer = data[motionPlayer]
    }

    fun playerSurrender() {
        playerLose(currentPlayer)
        for (i in currentPlayer.realty) {
            fieldClear(i)
        }
        triggerProperty(view.surrenderViewProperty)
        dice.double = false
        endMotion()
        checkEndGame()
    }

    fun playerLose(player: Player) {
        loosers.add(data.indexOf(player))
    }

    private fun fieldClear(i: Field) {
        board.fields[i.location].ownerUpdate(null)
        board.fields[i.location].upgrade = 0
        if (board.fields[i.location].hasMonopoly) board.fields[i.location].monopolyChange()
        board.fields[i.location].penaltyUpdate()
    }

    private fun checkEndGame() {
        runAsync { if (delay) Thread.sleep(350) } ui {
            if (gameIsEnd()) {
                setGameStatus(false)
            }
        }
    }

    fun gameIsEnd() = cntPls - loosers.size == 1

    fun setGameStatus(status: Boolean) {
        currentExchange.exchangePause = false
        pause = false
        gameWinner = data.first { data.indexOf(it) !in loosers }
        gameIsEnd = !status
        endProperty.value = gameIsEnd
    }

}


