package monopoly.`interface`

import javafx.scene.control.Button
import javafx.scene.control.CheckMenuItem
import javafx.scene.control.Label
import javafx.scene.control.TextArea
import javafx.scene.image.ImageView
import javafx.scene.layout.AnchorPane
import javafx.scene.layout.GridPane
import javafx.scene.shape.Rectangle
import javafx.scene.text.Text
import javafx.util.Duration
import monopoly.logic.ExchangeOffer
import monopoly.logic.Player
import monopoly.logic.SecretAction
import tornadofx.*

class GamePlay : View("Monopoly") {

    override val root: AnchorPane by fxml()

    private val alertCheck: CheckMenuItem by fxid()
    private val actionLogChek: CheckMenuItem by fxid()

    private val textArea: TextArea by fxid()

    //Offer to buy field block
    private val offerToBuy: AnchorPane by fxid()
    private val offerCostRealty: Label by fxid()
    private val offerTypeRealty: Label by fxid()
    private val offerNotEnoughMoney: Label by fxid()

    private fun offerToBuyInit(player: Player) {
        sendln("Игрок попал на поле ${game.board.fields[player.position].name}, приобритет ли он его?")
        if (!player.ai) {
            playerOfferToBuy(player)
        }
    }

    private fun playerOfferToBuy(player: Player) {
        offerToBuy.opacity = 1.0
        offerToBuy.disableProperty().value = false
        offerCostRealty.text = "${game.board.fields[player.position].cost}"
        offerTypeRealty.text = "${game.board.fields[player.position].type}"
    }

    fun offerAccept() {
        val player = game.currentPlayer
        if (game.playerAcceptBuyRealty()) {
            offerNotEnoughMoney.opacity = 0.0
            offerToBuy.opacity = 0.0
            offerToBuy.disableProperty().value = true
            offerAcceptView(player)
            game.endMotion()
            return
        }
        offerNotEnoughMoney.opacity = 1.0
    }

    private fun offerAcceptView(player: Player) {
        sendln("${player.name} приобретает поле ${game.board.fields[player.position].name}, за ${game.board.fields[player.position].cost}$!")
    }

    fun offerReject() {
        offerNotEnoughMoney.opacity = 0.0
        offerToBuy.opacity = 0.0
        offerToBuy.disableProperty().value = true
        game.endMotion()
    }

    //Pay penalty block
    private val payPenalty: AnchorPane by fxid()
    private val penaltyOwner: Label by fxid()
    private val penaltyCost: Label by fxid()
    private val penaltyNotEnoughMoney: Label by fxid()
    private val textPunisment: Label by fxid()

    private fun penaltyInit(player: Player) {
        if (!game.ifPunishment()) sendln("Игрок попал на поле ${game.board.fields[player.position].owner!!.name}, и должен ему ${game.board.fields[player.position].penalty}$!")
        else sendln("${player.name}, вас уличили за неуплату налогов! Вы должны оплатить штраф 2000$.")
        if (!player.ai) {
            penaltyPlayer(player)
        }
    }

    private fun penaltyPlayer(player: Player) {
        textPunisment.text = "Вы платите игроку"
        penaltyOwner.text = ""
        payPenalty.opacity = 1.0
        payPenalty.disableProperty().value = false
        if (!game.ifPunishment()) {
            penaltyOwner.text = game.board.fields[player.position].owner!!.name
        } else {
            textPunisment.text = "Вас уличили за неуплату налогов!"
        }
        penaltyCost.text = "${game.board.fields[player.position].penalty}"
    }

    private fun penaltyClose() {
        penaltyNotEnoughMoney.opacity = 0.0
        payPenalty.opacity = 0.0
        payPenalty.disableProperty().value = true
    }

    fun penaltyAccept() {
        if (game.playerPayPenalty()) {
            penaltyClose()
            game.endMotion()
            return
        }
        penaltyNotEnoughMoney.opacity = 1.0
    }

    fun penaltySurrender() {
        penaltyClose()
        game.playerSurrender()
    }


    //Secret action block
    private val negativeAction: AnchorPane by fxid()
    private val negativeText: Text by fxid()
    private val negativeNotEnoughMoney: Label by fxid()

    private fun negativeEventInit(player: Player) {
        send(
            when (game.dice.secret.second) {
                SecretAction.Action1 -> "Вас обокрали на 300$"
                SecretAction.Action2 -> "Вы попали на распродажу и потратили там 500$"
                SecretAction.Action3 -> "Вы испортили свои любимые штаны за 40$"
                SecretAction.Action4 -> "В банке произошла ошибка, и с вас списали 750$"
                else -> "Вы простудились, и потратили 250$ в аптеке"
            }
        )
        sendln(", " + player.name)
        if (!player.ai) playerNegativeSecret()
    }

    private fun positiveEventInit(player: Player) {
        send(player.name + ", ")
        sendln(
            when (game.dice.secret.second) {
                SecretAction.Action1 -> "вы нашли в зимней куртке забытые 250$"
                SecretAction.Action2 -> "вы выйграли на ставках 500$"
                SecretAction.Action3 -> "вам вернули долг 300$"
                SecretAction.Action4 -> "в банке произошла ошибка, на ваш счет перечислено 750$"
                else -> "ваша собака принесла вам 100$"
            }
        )
        if (!player.ai && showAlerts) find<SomeActionAlert>().openModal(resizable = false)
    }

    private fun playerNegativeSecret() {
        negativeAction.opacity = 1.0
        negativeAction.disableProperty().value = false
        when (game.dice.secret.second) {
            SecretAction.Action1 -> negativeText.text = "Вас обокрали на 300"
            SecretAction.Action2 -> negativeText.text = "Вы попали на распродажу и потратили там 500"
            SecretAction.Action3 -> negativeText.text = "Вы испортили свои любимые штаны за 40"
            SecretAction.Action4 -> negativeText.text = "В банке произошла ошибка, и с вас списали 750"
            else -> negativeText.text = "Вы простудились, и потратили 250 в аптеке"
        }
    }

    private fun negativeClose() {
        negativeAction.opacity = 0.0
        negativeAction.disableProperty().value = true
        negativeNotEnoughMoney.opacity = 0.0
    }

    fun negativePay() {
        if (game.negativePay()) {
            negativeClose()
            game.endMotion()
            return
        }
        negativeNotEnoughMoney.opacity = 1.0
    }

    fun negativeSurrender() {
        negativeClose()
        game.playerSurrender()
    }

    //Prison block
    private val prison: AnchorPane by fxid()
    private val prisonCountMoves: Label by fxid()
    private val prisonNotEnoughMoney: Label by fxid()
    private val prisonMessage: Label by fxid()
    private val prisonTryButton: Button by fxid()
    private val prisonSurrenderButton: Button by fxid()

    private fun prisonInit(player: Player) {
        sendln("${player.name}, вы находитесь в тюрьме")
        if (!player.ai) {
            prisonPlayer()
        }
    }

    private fun prisonPlayer() {
        prisonMessage.text = "Заплатите 500, или выбейте дубль"
        prisonNotEnoughMoney.opacity = 0.0
        prison.opacity = 1.0
        prison.disableProperty().value = false
        prisonTryButton.disableProperty().value = false
        if (game.prisonPayDay(game.currentPlayer)) {
            prisonMessage.text = "Заплатите 750"
            prisonTryButton.disableProperty().value = true
            prisonSurrenderButton.opacity = 1.0
            prisonSurrenderButton.disableProperty().value = false
        }
        prisonCountMoves.text = "${4 - game.currentPlayer.prisonDays}"
    }

    private fun prisonClose() {
        prison.opacity = 0.0
        prison.disableProperty().value = true
        prisonSurrenderButton.opacity = 0.0
        prisonSurrenderButton.disableProperty().value = true
    }

    fun prisonPay() {
        val tmp = game.prisonPay()
        if (tmp in 1..2) {
            prisonClose()
            return
        }
        prisonNotEnoughMoney.opacity = 1.0
    }

    fun prisonTryView() {
        prisonClose()
        game.prisonTryLogic()
    }

    fun prisonSurrender() {
        prisonClose()
        game.playerSurrender()
    }

    private fun cycleComplete(player: Player) {
        sendln("Положенная награда в 2000$ за проход круга ваша, " + player.name)
        if (!player.ai && showAlerts) runAsync { Thread.sleep(300) } ui { find<CycleComplete>().openModal(resizable = false) }
    }

    private fun stonksAction(player: Player) {
        sendln("Выигрышь в лотерею в размере 3000$!")
        if (!player.ai && showAlerts) find<SomeActionAlert>().openModal(resizable = false)
    }

    private fun startAction(player: Player) {
        sendln("Получите и распишитесь, ${player.name}, 1000$ за попадание на поле Старт!")
        if (!player.ai && showAlerts) find<SomeActionAlert>().openModal(resizable = false)
    }

    private fun playerToPrisonView(player: Player) {
        if (game.prisonByDouble) { sendln("Вы отправляетесь в тюрьму, за махинации с кубиками, ${game.currentPlayer.name}!") }
        else { sendln("Вы отправляетесь в тюрьму, за неуплату налогов, ${game.currentPlayer.name}.") }
        if (!player.ai && showAlerts) find<SomeActionAlert>().openModal(resizable = false)
    }

    //Board functional
    private val idMotion: Label by fxid()

    val buttonRoll: Button by fxid()

    //models of players & moves
    private val playerPane: GridPane by fxid()
    private val model1: Rectangle by fxid()
    private val model2: Rectangle by fxid()
    private val model3: Rectangle by fxid()
    private val model4: Rectangle by fxid()
    private val model5: Rectangle by fxid()
    private val arrayModel = arrayListOf(model1, model2, model3, model4, model5)
    //animation
    private val prisonPosition = Pair(810.0, 50.0)
    private val arrayPosition = arrayOf(Pair(10.0, 30.0), Pair(10.0, 40.0), Pair(30.0, 30.0), Pair(30.0, 40.0), Pair(10.0, 50.0))
    private fun movePlayer(a: Int, prison: Boolean, player: Int) {
        timeline {
            keyframe(Duration.seconds(0.5)) {
                keyvalue(arrayModel[player].layoutXProperty(), arrayPosition[player].first + if (prison) prisonPosition.first else game.board.fields[a].layoutX)
                keyvalue(arrayModel[player].layoutYProperty(), arrayPosition[player].second + if (prison) prisonPosition.second else game.board.fields[a].layoutY)
            }
        }
    }

    private fun openOfferWindow() {
        find<ExchangeToPlayer>().openModal(resizable = false)!!.setOnCloseRequest {
            game.currentExchange.exchangePause = false
        }
    }

    fun exchangeOfferLog(exchange: ExchangeOffer) {
        send("${exchange.exchangeSender.name} получает при обмене: ")
        for (i in exchange.exchangeReceiverList.withIndex()) {
            if (i.index != exchange.exchangeReceiverList.size - 1) {
                send(" ${i.value.name},")
            } else {
                sendln(" ${i.value.name} и ${exchange.exchangeMoneyReceiver}$.")
            }
        }
        send("${exchange.exchangeReceiver.name} получает при обмене: ")
        for (i in exchange.exchangeSenderList.withIndex()) {
            if (i.index != exchange.exchangeSenderList.size - 1) {
                send(" ${i.value.name}, ")
            } else {
                sendln(" ${i.value.name} и ${exchange.exchangeMoneySender}$.")
            }
        }
    }

    //game realty fields
    private val upperPane: GridPane by fxid()//0..7
    private val rightPane: GridPane by fxid()//8..13
    private val bottomPane: GridPane by fxid()//14..21
    private val leftPane: GridPane by fxid()//15..27

    //game dice
    private val firstDice1: ImageView by fxid()
    private val firstDice2: ImageView by fxid()
    private val firstDice3: ImageView by fxid()
    private val firstDice4: ImageView by fxid()
    private val secondDice1: ImageView by fxid()
    private val secondDice2: ImageView by fxid()
    private val secondDice3: ImageView by fxid()
    private val secondDice4: ImageView by fxid()
    private val arrayDice = arrayOf(firstDice1, firstDice2, firstDice3, firstDice4, secondDice1, secondDice2, secondDice3, secondDice4)
    //animation
    private fun diceRoll(a: Int, b: Int) {
        if (a == b) sendln("Поздравляем, на кубиках выпало ${a + b}, дублем!")
        else sendln("На кубиках выпало ${a + b}!")
        timeline {
            keyframe(Duration.seconds(0.5)) {
                keyvalue(arrayDice[a - 1].opacityProperty(), 1.0)
                keyvalue(arrayDice[b + 3].opacityProperty(), 1.0)
            }
        }
        runAsync { Thread.sleep(1000) } ui {
            timeline {
                keyframe(Duration.seconds(0.5)) {
                    keyvalue(arrayDice[a - 1].opacityProperty(), 0.0)
                    keyvalue(arrayDice[b + 3].opacityProperty(), 0.0)
                }
            }
        }
    }

    init {
        this.currentStage!!.setOnCloseRequest {
            game.currentExchange.exchangePause = false
        }
        primaryStage.width = 1024.0
        primaryStage.height = 1048.0
        primaryStage.centerOnScreen()

        game.cntPlsUpdate()
        game.setBalance()
        checkSettings()
        connectToModel()

        sendln("Игра начинается!")
        sendln("Ваш ход, ${game.data[0].name} !")

        if (game.data[0].ai) motion()
    }

    fun motion() {
        buttonRoll.disableProperty().value = true
        game.motion()
    }

    private fun viewEndMotion(player: Player) {
        idMotion.text = "Ход игрока ${game.data[game.data.indexOf(player)].name}"
        if (!game.gameIsEnd && !player.ai && !player.playerInPrison()) buttonRoll.disableProperty().value = false
    }


    private fun connectToModel() {
        idMotion.text = "Ход игрока ${game.data[0].name}"
        for (i in 0 until game.cntPls) {
            arrayModel[i].opacity = 1.0
            playerPane.children[i].opacity = 1.0
            (playerPane.children[i].getChildList()!![0] as Label).text = game.data[i].name
            (playerPane.children[i].getChildList()!![2] as Label).bind(game.data[i].moneyProperty)
            game.data[i].positionProperty.onChange {
                movePlayer(game.data[i].position, game.data[i].playerInPrison(), i)
            }
            playerPane.children[i].setOnMouseClicked { if (game.canExchange(game.currentPlayer, game.data[i])) openOfferWindow() }
        }//connect info about players

        linkFieldInfo()//connect field info

        val view = game.view//connect notify motion
        game.endProperty.onChange { endGame() }
        game.dice.checkRollProperty.onChange { diceRoll(game.dice.first, game.dice.second) }
        view.prisonInitProperty.onChange { prisonInit(game.currentPlayer) }
        view.penaltyInitProperty.onChange { penaltyInit(game.currentPlayer) }
        view.offerToBuyInitProperty.onChange { offerToBuyInit(game.currentPlayer) }
        view.negativeEventInitProperty.onChange { negativeEventInit(game.currentPlayer) }
        view.positiveEventInitProperty.onChange { positiveEventInit(game.currentPlayer) }
        view.diceDoubleProperty.onChange { diceDoubleAlert() }
        view.cycleCompleteProperty.onChange { cycleComplete(game.currentPlayer) }
        view.stonksActionProperty.onChange { stonksAction(game.currentPlayer) }
        view.startActionProperty.onChange { startAction(game.currentPlayer) }
        view.notifyInView.onChange { sendln(view.notifyInView.value) }
        view.viewEndMotionProperty.onChange { viewEndMotion(game.currentPlayer) }
        view.toPrisonViewProperty.onChange { playerToPrisonView(game.currentPlayer) }
        view.surrenderViewProperty.onChange { playerSurrenderView(game.currentPlayer) }
        view.fieldBoughtProperty.onChange { offerAcceptView(game.currentPlayer) }
        view.sellUpgradeViewProperty.onChange { sellUpgradeView(game.currentPlayer) }
        view.fieldSelledProperty.onChange {
            sellAndVacateFieldView(
                game.board.fields[view.fieldSelledProperty.value],
                game.currentPlayer
            )
        }
    }

    private fun checkSettings() {
        alertCheck.isSelected = showAlerts
        actionLogChek.isSelected = showActionLog
        if (actionLogChek.isSelected) {
            textArea.opacity = 1.0
            textArea.disableProperty().value = false
        } else {
            textArea.opacity = 0.0
            textArea.disableProperty().value = true
        }

        if (game.data.filter { it.ai }.size == game.data.size) buttonRoll.opacity = 0.0
    }

    private fun linkFieldInfo() {
        for (field in game.board.fields) {
            if (field.couldBuy) game.board.fields[field.location].ownerProperty.onChange { paintField(field) }
        }

        for (i in 1..7) {
            if (game.board.fields[i].couldBuy) {
                (upperPane.children[i].getChildList()!![1] as Label).bind(game.board.fields[i].penaltyProperty)
                upperPane.children[i].setOnMouseClicked { if (game.canControl(i)) find<Field>().openModal(resizable = false) }
                (upperPane.children[i].getChildList()!![2] as Label).bind(game.board.fields[i].view)
            }
        }
        for (i in 8..13) {
            if (game.board.fields[i].couldBuy) {
                (rightPane.children[i - 8].getChildList()!![1] as Label).bind(game.board.fields[i].penaltyProperty)
                rightPane.children[i - 8].setOnMouseClicked { if (game.canControl(i)) find<Field>().openModal(resizable = false) }
                (rightPane.children[i - 8].getChildList()!![2] as Label).bind(game.board.fields[i].view)
            }
        }
        for (i in 15..19) {
            if (game.board.fields[i].couldBuy) {
                (bottomPane.children.reversed()[i - 14].getChildList()!![0] as Label).bind(game.board.fields[i].penaltyProperty)
                bottomPane.children.reversed()[i - 14].setOnMouseClicked { if (game.canControl(i)) find<Field>().openModal(resizable = false) }
                (bottomPane.children.reversed()[i - 14].getChildList()!![2] as Label).bind(game.board.fields[i].view)
            }
        }
        for (i in 22..27) {
            if (game.board.fields[i].couldBuy) {
                (leftPane.children.reversed()[i - 22].getChildList()!![1] as Label).bind(game.board.fields[i].penaltyProperty)
                leftPane.children.reversed()[i - 22].setOnMouseClicked { if (game.canControl(i)) find<Field>().openModal(resizable = false) }
                (leftPane.children.reversed()[i - 22].getChildList()!![2] as Label).bind(game.board.fields[i].view)
            }
        }
    }

    private val defaultColor = c("#d2edd7")
    private val arrayPlayerColor = arrayOf(c("#f13030"), c("#f27330"), c("green"), c("#03a3d1"), c("#eb15dc"))
    private fun paintField(field: monopoly.logic.Field) {
        val color =  if (game.data.indexOf(field.owner) == -1) defaultColor else arrayPlayerColor[game.data.indexOf(field.owner)]
        when (field.location) {
            in 0..7 -> upperPane.children[field.location].style(append = true) { backgroundColor += color }
            in 8..13 -> rightPane.children[field.location - 8].style(append = true) { backgroundColor += color }
            in 15..19 -> bottomPane.children.reversed()[field.location - 14].style(append = true) { backgroundColor += color }
            else -> leftPane.children.reversed()[field.location - 22].style(append = true) { backgroundColor += color }
        }
    }

    private fun send(str: String) {
        if (showActionLog) textArea.appendText(str)
    }

    fun sendln(str: String) {
        if (showActionLog) textArea.appendText(str + "\n")
    }

    private fun clearFieldLooser(current: Player) {
        arrayModel[game.data.indexOf(current)].opacity = 0.0
        playerPane.children[game.data.indexOf(current)].opacity = 0.0
    }

    private fun playerSurrenderView(player: Player) {
        clearFieldLooser(player)
        sendln("")
        sendln("${player.name} не справляется с натиском конкурентов, и покиадет наш стол!")
    }

    private fun diceDoubleAlert() {
        if (!game.currentPlayer.ai && showAlerts) find<DiceDouble>().openModal(resizable = false)
    }

    private fun sellAndVacateFieldView(field: monopoly.logic.Field, player: Player) {
        sendln(player.name + " продает свое поле " + field.name + " за " + field.cost / 2 + ".")
    }

    private fun sellUpgradeView(player: Player) {
        sendln(
            player.name + " продает филиал. Количество филиалов на поле "
                    + game.board.fields[game.view.fieldDegradeProperty.value].name + " - "
                    + game.board.fields[game.view.fieldDegradeProperty.value].upgrade
        )
    }

    private fun endGame() {
        sendln("${game.gameWinner!!.name}, вы переиграли всех своих оппонентов!")
        find<FinishGame>().openModal(resizable = false)
    }

    fun howToPlay() {
        find<HowToPlay>().openModal(resizable = false)
    }

    fun newGame() {
        game.gameIsEnd = true
        replaceWith(Begin(), ViewTransition.Implode(0.5.seconds))
    }

    fun alertSwitch() {
        showAlerts = alertCheck.isSelected
    }

    fun actionLogSwitch() {
        showActionLog = actionLogChek.isSelected
        if (actionLogChek.isSelected) {
            textArea.opacity = 1.0
            textArea.disableProperty().value = false
        } else {
            textArea.opacity = 0.0
            textArea.disableProperty().value = true
        }
    }

    fun exit() {
        primaryStage.close()
    }
}