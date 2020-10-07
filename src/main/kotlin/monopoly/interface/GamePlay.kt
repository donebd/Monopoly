package monopoly.`interface`

import javafx.scene.control.Button
import javafx.scene.control.CheckMenuItem
import javafx.scene.control.Label
import javafx.scene.control.TextArea
import javafx.scene.image.ImageView
import javafx.scene.layout.AnchorPane
import javafx.scene.layout.GridPane
import javafx.scene.layout.HBox
import javafx.scene.layout.VBox
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

    fun offerToBuyInit(player: Player) {
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

    fun offerAcceptView(player: Player) {
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

    fun negativeEventInit(player: Player) {
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

    fun positiveEventInit(player: Player) {
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
        if (game.prisonPayDay()) {
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
        game.view.notifyInView.value = ("Не скучайте за решёткой, ${game.currentPlayer.name}!")
        if (!player.ai && showAlerts) find<SomeActionAlert>().openModal(resizable = false)
    }

    //Board functional
    private val idMotion: Label by fxid()

    val buttonRoll: Button by fxid()

    //models of players & moves
    private val model1: Rectangle by fxid()
    private val model2: Rectangle by fxid()
    private val model3: Rectangle by fxid()
    private val model4: Rectangle by fxid()
    private val model5: Rectangle by fxid()
    //animation
    private val player1Default = Pair(10.0, 30.0)
    private val player2Default = Pair(10.0, 40.0)
    private val player3Default = Pair(30.0, 30.0)
    private val player4Default = Pair(30.0, 40.0)
    private val player5Default = Pair(10.0, 50.0)
    private val prisonPosition = Pair(810.0, 50.0)
    private fun movePlayer1(a: Int, prison: Boolean) {
        timeline {
            keyframe(Duration.seconds(0.5)) {
                if (prison) {
                    keyvalue(model1.layoutXProperty(), player1Default.first + prisonPosition.first)
                    keyvalue(model1.layoutYProperty(), player1Default.second + prisonPosition.second)
                } else {
                    keyvalue(model1.layoutXProperty(), player1Default.first + game.board.fields[a].layoutX)
                    keyvalue(model1.layoutYProperty(), player1Default.second + game.board.fields[a].layoutY)
                }
            }
        }
    }

    private fun movePlayer2(a: Int, prison: Boolean) {
        timeline {
            keyframe(Duration.seconds(0.5)) {
                if (prison) {
                    keyvalue(model2.layoutXProperty(), player2Default.first + prisonPosition.first)
                    keyvalue(model2.layoutYProperty(), player2Default.second + prisonPosition.second)
                } else {
                    keyvalue(model2.layoutXProperty(), player2Default.first + game.board.fields[a].layoutX)
                    keyvalue(model2.layoutYProperty(), player2Default.second + game.board.fields[a].layoutY)
                }
            }
        }
    }

    private fun movePlayer3(a: Int, prison: Boolean) {
        timeline {
            keyframe(Duration.seconds(0.5)) {
                if (prison) {
                    keyvalue(model3.layoutXProperty(), player3Default.first + prisonPosition.first)
                    keyvalue(model3.layoutYProperty(), player3Default.second + prisonPosition.second)
                } else {
                    keyvalue(model3.layoutXProperty(), player3Default.first + game.board.fields[a].layoutX)
                    keyvalue(model3.layoutYProperty(), player3Default.second + game.board.fields[a].layoutY)
                }
            }
        }
    }

    private fun movePlayer4(a: Int, prison: Boolean) {
        timeline {
            keyframe(Duration.seconds(0.5)) {
                if (prison) {
                    keyvalue(model4.layoutXProperty(), player4Default.first + prisonPosition.first)
                    keyvalue(model4.layoutYProperty(), player4Default.second + prisonPosition.second)
                } else {
                    keyvalue(model4.layoutXProperty(), player4Default.first + game.board.fields[a].layoutX)
                    keyvalue(model4.layoutYProperty(), player4Default.second + game.board.fields[a].layoutY)
                }
            }
        }
    }

    private fun movePlayer5(a: Int, prison: Boolean) {
        timeline {
            keyframe(Duration.seconds(0.5)) {
                if (prison) {
                    keyvalue(model5.layoutXProperty(), player5Default.first + prisonPosition.first)
                    keyvalue(model5.layoutYProperty(), player5Default.second + prisonPosition.second)
                } else {
                    keyvalue(model5.layoutXProperty(), player5Default.first + game.board.fields[a].layoutX)
                    keyvalue(model5.layoutYProperty(), player5Default.second + game.board.fields[a].layoutY)
                }
            }
        }
    }
    //start model position

    //player property description
    private val playerField1: VBox by fxid()
    private val playerField2: VBox by fxid()
    private val playerField3: VBox by fxid()
    private val playerField4: VBox by fxid()
    private val playerField5: VBox by fxid()
    private val moneylbl1: Label by fxid()
    private val moneylbl2: Label by fxid()
    private val moneylbl3: Label by fxid()
    private val moneylbl4: Label by fxid()
    private val moneylbl5: Label by fxid()
    private val pl1: Label by fxid()
    private val pl2: Label by fxid()
    private val pl3: Label by fxid()
    private val pl4: Label by fxid()
    private val pl5: Label by fxid()

    fun player1Offer() {
        if (game.canExchange(game.currentPlayer, game.data[0])) openOfferWindow()
    }

    fun player2Offer() {
        if (game.canExchange(game.currentPlayer, game.data[1])) openOfferWindow()
    }

    fun player3Offer() {
        if (game.data.size > 2 && game.canExchange(game.currentPlayer, game.data[2])) openOfferWindow()
    }

    fun player4Offer() {
        if (game.data.size > 3 && game.canExchange(game.currentPlayer, game.data[3])) openOfferWindow()
    }

    fun player5Offer() {
        if (game.data.size > 4 && game.canExchange(game.currentPlayer, game.data[4])) openOfferWindow()
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
    private val firstdice1: ImageView by fxid()
    private val firstdice2: ImageView by fxid()
    private val firstdice3: ImageView by fxid()
    private val firstdice4: ImageView by fxid()
    private val seconddice1: ImageView by fxid()
    private val seconddice2: ImageView by fxid()
    private val seconddice3: ImageView by fxid()
    private val seconddice4: ImageView by fxid()
    //animation
    private fun diceRoll(a: Int, b: Int) {
        if (a == b) sendln("Поздравляем, на кубиках выпало ${a + b}, дублем!")
        else sendln("На кубиках выпало ${a + b}!")
        timeline {
            keyframe(Duration.seconds(0.5)) {
                when (a) {
                    1 -> keyvalue(firstdice1.opacityProperty(), 1.0)
                    2 -> keyvalue(firstdice2.opacityProperty(), 1.0)
                    3 -> keyvalue(firstdice3.opacityProperty(), 1.0)
                    else -> keyvalue(firstdice4.opacityProperty(), 1.0)
                }
                when (b) {
                    1 -> keyvalue(seconddice1.opacityProperty(), 1.0)
                    2 -> keyvalue(seconddice2.opacityProperty(), 1.0)
                    3 -> keyvalue(seconddice3.opacityProperty(), 1.0)
                    else -> keyvalue(seconddice4.opacityProperty(), 1.0)
                }
            }
        }
        runAsync { Thread.sleep(1000) } ui {
            timeline {
                keyframe(Duration.seconds(0.5)) {
                    keyvalue(firstdice1.opacityProperty(), 0.0)
                    keyvalue(firstdice2.opacityProperty(), 0.0)
                    keyvalue(firstdice3.opacityProperty(), 0.0)
                    keyvalue(firstdice4.opacityProperty(), 0.0)
                    keyvalue(seconddice1.opacityProperty(), 0.0)
                    keyvalue(seconddice2.opacityProperty(), 0.0)
                    keyvalue(seconddice3.opacityProperty(), 0.0)
                    keyvalue(seconddice4.opacityProperty(), 0.0)
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

        idMotion.text = "Ход игрока ${game.data[0].name}"

        pl1.text += game.data[0].name
        moneylbl1.bind(game.data[0].moneyProperty)
        game.data[0].positionProperty.onChange { movePlayer1(game.data[0].position, game.data[0].playerInPrison()) }

        pl2.text += game.data[1].name
        moneylbl2.bind(game.data[1].moneyProperty)
        game.data[1].positionProperty.onChange { movePlayer2(game.data[1].position, game.data[1].playerInPrison()) }

        if (game.cntPls >= 3) {
            playerField3.opacity = 1.0
            model3.opacity = 1.0
            pl3.text += game.data[2].name
            moneylbl3.bind(game.data[2].moneyProperty)
            game.data[2].positionProperty.onChange { movePlayer3(game.data[2].position, game.data[2].playerInPrison()) }
        }
        if (game.cntPls >= 4) {
            playerField4.opacity = 1.0
            model4.opacity = 1.0
            pl4.text += game.data[3].name
            moneylbl4.bind(game.data[3].moneyProperty)
            game.data[3].positionProperty.onChange { movePlayer4(game.data[3].position, game.data[3].playerInPrison()) }
        }
        if (game.cntPls >= 5) {
            playerField5.opacity = 1.0
            model5.opacity = 1.0
            pl5.text += game.data[4].name
            moneylbl5.bind(game.data[4].moneyProperty)
            game.data[4].positionProperty.onChange { movePlayer5(game.data[4].position, game.data[4].playerInPrison()) }
        }
        game.setBalance()
        linkFieldInfo()

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

        sendln("Игра начинается!")
        sendln("Ваш ход, ${game.data[0].name} !")

        connectToModel()

        if (game.data[0].ai) motion()
    }

    private fun connectToModel() {
        game.endProperty.onChange { endGame() }
        game.dice.checkRollProperty.onChange { diceRoll(game.dice.first, game.dice.second) }
        game.view.prisonInitProperty.onChange { prisonInit(game.currentPlayer) }
        game.view.penaltyInitProperty.onChange { penaltyInit(game.currentPlayer) }
        game.view.offerToBuyInitProperty.onChange { offerToBuyInit(game.currentPlayer) }
        game.view.negativeEventInitProperty.onChange { negativeEventInit(game.currentPlayer) }
        game.view.positiveEventInitProperty.onChange { positiveEventInit(game.currentPlayer) }
        game.view.diceDoubleProperty.onChange { diceDoubleAlert() }
        game.view.cycleCompleteProperty.onChange { cycleComplete(game.currentPlayer) }
        game.view.stonksActionProperty.onChange { stonksAction(game.currentPlayer) }
        game.view.startActionProperty.onChange { startAction(game.currentPlayer) }
        game.view.notifyInView.onChange { sendln(game.view.notifyInView.value) }
        game.view.viewEndMotionProperty.onChange { viewEndMotion(game.data[game.motionPlayer]) }
        game.view.toPrisonViewProperty.onChange { playerToPrisonView(game.currentPlayer) }
        game.view.surrenderViewProperty.onChange { playerSurrenderView(game.currentPlayer) }
        game.view.fieldBoughtProperty.onChange { offerAcceptView(game.currentPlayer) }
        game.view.sellUpgradeViewProperty.onChange { sellUpgradeView(game.currentPlayer) }
        game.view.fieldSelledProperty.onChange {
            sellAndVacateFieldView(
                game.board.fields[game.view.fieldSelledProperty.value],
                game.currentPlayer
            )
        }
    }

    private fun linkFieldInfo() {
        for (field in game.board.fields) {
            if (field.couldBuy) game.board.fields[field.location].ownerProperty.onChange { paintField(field) }
        }

        for (i in 1..7) {
            if (game.board.fields[i].couldBuy) {
                (upperPane.children[i].getChildList()!![1] as Label).bind(game.board.fields[i].penaltyProperty)
                upperPane.children[i].setOnMouseClicked { if (game.canControl(i)) find<Field>().openModal(resizable = false) }
            }
        }
        for (i in 8..13) {
            if (game.board.fields[i].couldBuy) {
                (rightPane.children[i - 8].getChildList()!![0].getChildList()!![0] as Label).bind(game.board.fields[i].penaltyProperty)
                rightPane.children[i - 8].setOnMouseClicked { if (game.canControl(i)) find<Field>().openModal(resizable = false) }
            }
        }
        for (i in 15..19) {
            if (game.board.fields[i].couldBuy) {
                (bottomPane.children.reversed()[i - 14].getChildList()!![0] as Label).bind(game.board.fields[i].penaltyProperty)
                bottomPane.children[i - 14].setOnMouseClicked { if (game.canControl(i)) find<Field>().openModal(resizable = false) }
            }
        }
        for (i in 22..27) {
            if (game.board.fields[i].couldBuy) {
                (leftPane.children.reversed()[i - 22].getChildList()!![1].getChildList()!![0] as Label).bind(game.board.fields[i].penaltyProperty)
                leftPane.children[i - 22].setOnMouseClicked { if (game.canControl(i)) find<Field>().openModal(resizable = false) }
            }
        }
    }

    private val player1Color = c("#f13030")
    private val player2Color = c("#f27330")
    private val player3Color = c("green")
    private val player4Color = c("#03a3d1")
    private val player5Color = c("#eb15dc")
    private val defaultColor = c("#d2edd7")
    fun paintField(field: monopoly.logic.Field) {
        val color = when (game.data.indexOf(field.owner)) {
            0 -> player1Color
            1 -> player2Color
            2 -> player3Color
            3 -> player4Color
            4 -> player5Color
            else -> defaultColor
        }
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
        when (current.id) {
            1 -> {
                playerField1.opacity = 0.0
                model1.opacity = 0.0
            }
            2 -> {
                playerField2.opacity = 0.0
                model2.opacity = 0.0
            }
            3 -> {
                playerField3.opacity = 0.0
                model3.opacity = 0.0
            }
            4 -> {
                playerField4.opacity = 0.0
                model4.opacity = 0.0
            }
            else -> {
                playerField5.opacity = 0.0
                model5.opacity = 0.0
            }
        }
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

    fun motion() {
        buttonRoll.disableProperty().value = true
        game.motion()
    }

    private fun viewEndMotion(player: Player) {
        when (game.data.indexOf(player)) {
            0 -> idMotion.text = "Ход игрока ${game.data[0].name}"
            1 -> idMotion.text = "Ход игрока ${game.data[1].name}"
            2 -> idMotion.text = "Ход игрока ${game.data[2].name}"
            3 -> idMotion.text = "Ход игрока ${game.data[3].name}"
            else -> idMotion.text = "Ход игрока ${game.data[4].name}"
        }
        if (!game.gameIsEnd && !player.ai && !player.playerInPrison()) buttonRoll.disableProperty().value = false
    }


    fun endGame() {
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