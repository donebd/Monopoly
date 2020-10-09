package monopoly.logic


import javafx.beans.property.SimpleBooleanProperty
import tornadofx.runAsync
import tornadofx.ui

class Game {

    var statsCntOfMotion = 0
    val statsVisitedField = IntArray(28)

    var delay = true

    val data = mutableListOf<Player>()

    var cntPls = 0 // count of players

    fun cntPlsUpdate() {
        cntPls = data.size
    }

    init {
        data.add(Player(1))
        data.add(Player(2))
        cntPls = data.size
    }

    //manipulate with view
    var gameIsEnd = false
    var gameWinner: Player? = null
    var endProperty = SimpleBooleanProperty(gameIsEnd)
    val view = NotifyInView()

    fun triggerProperty(property: SimpleBooleanProperty) {
        property.value = !property.value
    }

    private fun setGameStatus(status: Boolean) {
        gameIsEnd = !status
        gameWinner = data.first { data.indexOf(it) !in loosers }
        endProperty.value = gameIsEnd
    }

    val board = GameBoard()

    var motionPlayer = 0

    var currentPlayer = data.first() //the player who is currently walking in action

    var loosers = mutableListOf<Int>()

    val dice = Dice()

    var currentExchange = ExchangeOffer()

    val Ai = AiInstruction(this)

    val event = Event(this)

    fun canExchange(sender: Player, receiver: Player): Boolean {
        if (sender.ai || currentPlayer.id != sender.id) return false
        currentExchange = ExchangeOffer(sender, receiver)
        currentExchange.exchangePause = sender.id != receiver.id && receiver.id !in loosers
        return currentExchange.exchangePause
    }

    var currentAct = FieldAction() // for action with realty

    fun canControl(number: Int): Boolean {
        if (board.fields[number].owner == currentPlayer && !currentPlayer.ai) {
            currentAct = FieldAction(currentPlayer, board.fields[number])
            return true
        }
        return false
    }

    var prisonByDouble = false

    fun setBalance() {
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

    private fun checkPrisonByDouble(): Boolean {
        if ((dice.double && currentPlayer.doubleInARow == 2)) {
            prisonByDouble = true
            return true
        }
        prisonByDouble = false
        return false
    }

    fun playerAcceptBuyRealty(): Boolean {
        if (currentPlayer.money >= board.fields[currentPlayer.position].cost) {
            currentPlayer.moneyChange(-board.fields[currentPlayer.position].cost)
            currentPlayer.realty.add(board.fields[currentPlayer.position])
            board.fields[currentPlayer.position].ownerUpdate(currentPlayer)
            currentPlayer.checkForMonopoly(board.fields[currentPlayer.position])
            board.fields[currentPlayer.position].penaltyUpdate()
            return true
        }
        return false
    }

    fun playerPayPenalty(): Boolean {
        if (currentPlayer.money >= board.fields[currentPlayer.position].penalty) {
            currentPlayer.moneyChange(-board.fields[currentPlayer.position].penalty)
            if (board.fields[currentPlayer.position].type != Type.Punisment) {
                board.fields[currentPlayer.position].owner!!.moneyChange(board.fields[currentPlayer.position].penalty)
            }
            return true
        }
        return false
    }

    private fun playerLose() {
        loosers.add(data.indexOf(currentPlayer))
    }

    private fun fieldClear(i: Field) {
        board.fields[i.location].ownerUpdate(null)
        board.fields[i.location].upgrade = 0
        if (board.fields[i.location].hasMonopoly) board.fields[i.location].monopolyChange()
        board.fields[i.location].penaltyUpdate()
    }

    private fun gameIsEnd() = cntPls - loosers.size == 1

    fun negativePay(): Boolean {
        when (dice.secret.second) {
            SecretAction.Action1 -> if (currentPlayer.money >= 300) {
                currentPlayer.moneyChange(-300)
                return true
            }
            SecretAction.Action2 -> if (currentPlayer.money >= 500) {
                currentPlayer.moneyChange(-500)
                return true
            }
            SecretAction.Action3 -> if (currentPlayer.money >= 40) {
                currentPlayer.moneyChange(-40)
                return true
            }
            SecretAction.Action4 -> if (currentPlayer.money >= 750) {
                currentPlayer.moneyChange(-750)
                return true
            }
            else -> if (currentPlayer.money >= 250) {
                currentPlayer.moneyChange(-250)
                return true
            }
        }
        return false
    }

    fun prisonPay(): Int {
        if (currentPlayer.prisonDays == 4 && currentPlayer.money >= 750) {
            currentPlayer.prisonDays = 0
            currentPlayer.moneyChange(-750)
            currentPlayer.justOutJail = true
            view.notifyInView.value = ("${currentPlayer.name} выходит из тюрьмы, заплатив 750$.")
            endMotion()
            return 1
        }
        if (currentPlayer.prisonDays != 4 && currentPlayer.money >= 500) {
            currentPlayer.prisonDays = 0
            currentPlayer.moneyChange(-500)
            currentPlayer.justOutJail = true
            view.notifyInView.value = ("${currentPlayer.name} выходит из тюрьмы, заплатив 500$.")
            endMotion()
            return 2
        }
        return -1
    }

    fun prisonTryLogic() {
        dice.roll()
        runAsync { if (delay) Thread.sleep(600) } ui {
            if (prisonTry()) {
                view.notifyInView.value = ("${currentPlayer.name} выходит из тюрьмы, выбив дубль!")
                playerMove(currentPlayer)
            } else {
                if (4 - currentPlayer.prisonDays + 1 != 1)
                    view.notifyInView.value =
                        ("Игрок остается в тюрьме еще на ${4 - currentPlayer.prisonDays + 1} хода.")
                else
                    view.notifyInView.value = ("Всего один ход отлучает ${currentPlayer.name}, от свободы!")
                endMotion()
            }
        }
    }

    fun prisonPayDay(player: Player) = player.prisonDays == 4

    private fun prisonTry(): Boolean {
        if (dice.double) {
            currentPlayer.prisonDays = 0
            dice.double = false
            return true
        }
        currentPlayer.prisonDays++
        return false
    }

    fun fieldSellByHalf(player: Player, field: Field) {
        player.moneyChange(field.cost / 2)
        player.realty.remove(field)
        field.ownerUpdate(null)
        field.upgrade = 0
        player.checkForMonopoly(field)
        field.penaltyUpdate()
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
                Ai.prisonInstructions(player)
            }
            return
        }
        dice.roll()
        runAsync { if (delay) Thread.sleep(500) } ui {
            if (checkPrisonByDouble()) {
                event.playerToPrison(player)
            } else {
                playerMove(player)
            }
        }
    }

    private fun playerMove(player: Player) {
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
            if (player.ai) {
                for (i in Ai.instructions(player)) {
                    view.notifyInView.value =
                        (player.name + " строит филиал. Количество филиалов на поле " + board.fields[i].name + " - " + board.fields[i].upgrade)
                }
                triggerProperty(view.updateUpgradeView)
            }
            if (!dice.double && !player.justOutJail) {
                view.notifyInView.value = ""
                view.notifyInView.value = ("Ваш ход, ${player.name} !")
            }
            player.justOutJail = false
            runAsync {
                while (currentExchange.exchangePause) {
                    Thread.sleep(100)
                }
                runAsync { if (delay) Thread.sleep(500) } ui {
                    if ((player.playerInPrison() || player.ai) && !gameIsEnd) {
                        motion()
                    }
                }
            }
        }
    }

    private fun endMotionLogic() {
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
        playerLose()
        for (i in currentPlayer.realty) {
            fieldClear(i)
        }
        triggerProperty(view.surrenderViewProperty)
        dice.double = false
        endMotion()
        checkEndGame()
    }

    private fun checkEndGame() {
        runAsync { if (delay) Thread.sleep(350) } ui {
            if (gameIsEnd()) {
                setGameStatus(false)
            }
        }
    }

}


