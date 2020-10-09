package monopoly.logic

import tornadofx.runAsync
import tornadofx.ui

class Event(val game : Game) {
    private val view = game.view
    private val Ai = game.Ai
    private val board = game.board
    private var currentPlayer = game.currentPlayer
    private val dice = game.dice
    private val cntPls = game.cntPls

    fun handling(player: Player) {
        currentPlayer = game.currentPlayer
        //check cycle completed and reward according to the settings
        if (checkCircleComplete()) {
            game.triggerProperty(view.cycleCompleteProperty)
        }
        //buy realty
        if (realtyCanBuy()) {
            game.triggerProperty(view.offerToBuyInitProperty)
            if (player.ai) {
                Ai.buyInstructions(player)
                game.endMotion()
            }
            if (diceDoubleCheck()) game.triggerProperty(view.diceDoubleProperty)
            return
        }
        //pay penalty
        if (punishmentOrPenalty()) {
            game.triggerProperty(view.penaltyInitProperty)
            if (player.ai) {
                Ai.punisment(player)
            }
            if (diceDoubleCheck()) game.triggerProperty(view.diceDoubleProperty)
            return
        }
        //player to prison
        if (ifToPrison()) {
            playerToPrison(player)
            return
        }
        //get stonks
        if (stonksAction()) {
            game.triggerProperty(view.stonksActionProperty)
        }
        //secret action
        if (ifSecret()) {
            secretAction()
            if (diceDoubleCheck()) game.triggerProperty(view.diceDoubleProperty)
            return
        }
        //start field
        if (startAction()) {
            game.triggerProperty(view.startActionProperty)
        }
        //player owner field
        if (board.fields[player.position].owner != null && board.fields[player.position].owner!!.id == player.id) {
            view.notifyInView.value = "Игрок пришел с проверкой на свое поле"
        }

        if (player.position == 7) view.notifyInView.value =
            (player.name + " решил прогуляться в парке, рядом с темницей.")

        if (player.position == 14) view.notifyInView.value = (player.name + " наслаждается отдыхом на природе.")

        if (diceDoubleCheck()) game.triggerProperty(view.diceDoubleProperty)
        game.endMotion()
    }

    //settings reward
    private fun checkCircleComplete(): Boolean {
        if ((currentPlayer.finishCircle && currentPlayer.circlesCompleted < 5) ||
            (currentPlayer.finishCircle && currentPlayer.circlesCompleted < 10 && cntPls < 4)
        ) {
            currentPlayer.moneyChange(2000)
            return true
        }
        return false
    }

    private fun realtyCanBuy() =
        board.fields[currentPlayer.position].couldBuy && board.fields[currentPlayer.position].owner == null

    private fun diceDoubleCheck(): Boolean {
        if (dice.double) {
            currentPlayer.doubleInARow++
            return true
        }
        currentPlayer.doubleInARow = 0
        return false
    }

    private fun punishmentOrPenalty() = ifPunishment() ||
            (board.fields[currentPlayer.position].owner != null && board.fields[currentPlayer.position].owner!!.id != currentPlayer.id)

    fun ifPunishment() = board.fields[currentPlayer.position].type == Type.Punisment

    private fun ifToPrison() = board.fields[currentPlayer.position].type == Type.ToPrison

    fun playerToPrison(current: Player) {
        current.goToPrison()
        game.statsVisitedField[current.position]++
        dice.double = false
        game.triggerProperty(view.toPrisonViewProperty)
        runAsync { if (game.delay) Thread.sleep(200) } ui { game.endMotion() }
    }

    private fun stonksAction(): Boolean {
        if (ifStonks()) {
            currentPlayer.moneyChange(3000)
            return true
        }
        return false
    }

    fun ifStonks() = board.fields[currentPlayer.position].type == Type.Stonks

    fun ifSecret() = board.fields[currentPlayer.position].type == Type.Secret

    private fun secretAction() {
        //positive
        if (secretIsPositive()) {
            positiveSecret()
            game.triggerProperty(view.positiveEventInitProperty)
            game.endMotion()
        } else {//negative
            game.triggerProperty(view.negativeEventInitProperty)
            if (currentPlayer.ai) {
                Ai.negativeEvent(currentPlayer)
            }
        }
    }

    private fun secretIsPositive(): Boolean {
        dice.secretAction()
        if (dice.secret.first) return true
        return false
    }

    private fun positiveSecret() {
        when (dice.secret.second) {
            SecretAction.Action1 -> currentPlayer.moneyChange(250)
            SecretAction.Action2 -> currentPlayer.moneyChange(500)
            SecretAction.Action3 -> currentPlayer.moneyChange(300)
            SecretAction.Action4 -> currentPlayer.moneyChange(750)
            else -> currentPlayer.moneyChange(100)
        }
    }

    private fun startAction(): Boolean {
        if (ifStart()) {
            currentPlayer.moneyChange(1000)
            return true
        }
        return false
    }

    fun ifStart(): Boolean = board.fields[currentPlayer.position].type == Type.Start
}