package monopoly.`interface`

import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.image.ImageView
import javafx.scene.layout.AnchorPane
import javafx.scene.layout.HBox
import javafx.scene.layout.VBox
import javafx.scene.paint.Color
import javafx.scene.shape.Rectangle
import javafx.scene.text.Text
import javafx.util.Duration
import monopoly.logic.Game
import monopoly.logic.SecretAction
import tornadofx.*

class GamePlay: View("Monopoly"){

    override val root : AnchorPane by fxml()

    //Offer to buy field block
    private val offerToBuy : AnchorPane by fxid()
    private val offerCostRealty : Label by fxid()
    private val offerTypeRealty : Label by fxid()
    private val offerNotEnoughMoney : Label by fxid()

    private fun offerToBuy(){
        offerToBuy.opacity = 1.0
        offerToBuy.disableProperty().value = false
        offerCostRealty.text = "${game.board.fields[ game.data[game.presentId].position].cost}"
        offerTypeRealty.text = "${game.board.fields[ game.data[game.presentId].position].type}"
    }

    fun offerAccept(){
        if (game.playerAcceptBuyRealty()){
            when(game.presentId){
                0 -> paintField(game.data[game.presentId].position, c("#f13030"))
                1 -> paintField(game.data[game.presentId].position, c("#f27330"))
                2 -> paintField(game.data[game.presentId].position, c("green"))
                3 -> paintField(game.data[game.presentId].position, c("#03a3d1"))
                else -> paintField(game.data[game.presentId].position, c("#eb15dc"))
            }
            offerNotEnoughMoney.opacity = 0.0
            offerToBuy.opacity = 0.0
            offerToBuy.disableProperty().value = true
            endMotion()
            return
        }
        offerNotEnoughMoney.opacity = 1.0
    }

    fun offerReject(){
        offerNotEnoughMoney.opacity = 0.0
        offerToBuy.opacity = 0.0
        offerToBuy.disableProperty().value = true
        endMotion()
    }

    //Pay penalty block
    private val payPenalty : AnchorPane by fxid()
    private val penaltyOwner : Label by fxid()
    private val penaltyCost : Label by fxid()
    private val penaltyNotEnoughMoney : Label by fxid()
    private val textPunisment : Label by fxid()

    private fun payPenalty(){
        textPunisment.text = "Вы платите игроку"
        penaltyOwner.text = ""
        payPenalty.opacity = 1.0
        payPenalty.disableProperty().value = false
        if (!game.ifPunishment()) {
            penaltyOwner.text = game.board.fields[game.data[game.presentId].position].owner!!.name
        }else {
            textPunisment.text = "Вас уличили за неуплату налогов!"
        }
        penaltyCost.text = "${game.board.fields[game.data[game.presentId].position].penalty}"
    }

    private fun penaltyClose(){
        penaltyNotEnoughMoney.opacity = 0.0
        payPenalty.opacity = 0.0
        payPenalty.disableProperty().value = true
    }

    fun penaltyAccept(){
        if (game.playerPayPenalty()){
            penaltyClose()
            endMotion()
            return
        }
        penaltyNotEnoughMoney.opacity = 1.0
    }

    fun penaltySurrender(){
        penaltyClose()
        playerSurrender()
    }

    //Negative secret action block
    private val negativeAction : AnchorPane by fxid()
    private val negativeText : Text by fxid()
    private val negativeNotEnoughMoney : Label by fxid()

    private fun negativeInit(){
        negativeAction.opacity = 1.0
        negativeAction.disableProperty().value = false
        when(game.dice.secret.second){
            SecretAction.Action1 -> negativeText.text = "Вас обокрали на 300"
            SecretAction.Action2 -> negativeText.text = "Вы попали на распродажу и потратили там 500"
            SecretAction.Action3 -> negativeText.text = "Вы испортили свои любимые штаны за 40"
            SecretAction.Action4 -> negativeText.text = "В банке произошла ошибка, и с вас списали 750"
            else -> negativeText.text = "Вы простудились, и потратили 250 в аптеке"
        }
    }

    private fun negativeClose(){
        negativeAction.opacity = 0.0
        negativeAction.disableProperty().value = true
        negativeNotEnoughMoney.opacity = 0.0
    }

    fun negativePay(){
        if (game.negativePay()){
            negativeClose()
            endMotion()
            return
        }
        negativeNotEnoughMoney.opacity = 1.0
    }

    fun negativeSurrender(){
        negativeClose()
        playerSurrender()
    }

    //Prison block
    private val prison : AnchorPane by fxid()
    private val prisonCountMoves : Label by fxid()
    private val prisonNotEnoughMoney : Label by fxid()
    private val prisonMessage : Label by fxid()
    private val prisonTryButton : Button by fxid()
    private val prisonSurrenderButton : Button by fxid()

    private fun prisonInit(){
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
        prisonCountMoves.text = "${4 - game.data[game.presentId].prisonDays}"
    }

    private fun prisonClose(){
        prison.opacity = 0.0
        prison.disableProperty().value = true
        prisonSurrenderButton.opacity = 0.0
        prisonSurrenderButton.disableProperty().value = true
    }

    fun prisonPay(){
        if(game.prisonPay()){
            prisonClose()
            endMotion()
            return
        }
        prisonNotEnoughMoney.opacity = 1.0
    }

    fun prisonTry(){
        game.dice.roll()
        diceRoll(game.dice.first,game.dice.second)
        prisonClose()
        runAsync { Thread.sleep(1000) }ui{
            if (game.prisonTry()) playerMove()
            runAsync { Thread.sleep(250) }ui{endMotion()}
        }
    }

    fun prisonSurrender(){
        prisonClose()
        playerSurrender()
    }

    //Board functional
    private val idMotion : Label by fxid()

    val buttonRoll : Button by fxid()

    //models of players & moves
    private val model1 : Rectangle by fxid()
    private val model2 : Rectangle by fxid()
    private val model3 : Rectangle by fxid()
    private val model4 : Rectangle by fxid()
    private val model5 : Rectangle by fxid()
    //animation
    private fun movePlayer1(a : Int, prison : Boolean){
        timeline {
            keyframe(Duration.seconds(0.5)) {
                if (prison){
                    keyvalue(model1.layoutXProperty(), game.player1Default.first + game.prisonPosition.first)
                    keyvalue(model1.layoutYProperty(), game.player1Default.second + game.prisonPosition.second)
                }else {
                    keyvalue(model1.layoutXProperty(), game.player1Default.first + game.board.fields[a].layoutX)
                    keyvalue(model1.layoutYProperty(), game.player1Default.second + game.board.fields[a].layoutY)
                }
            }
        }
    }
    private fun movePlayer2(a : Int, prison : Boolean){
        timeline {
            keyframe(Duration.seconds(0.5)) {
                if (prison){
                    keyvalue(model2.layoutXProperty(), game.player2Default.first + game.prisonPosition.first)
                    keyvalue(model2.layoutYProperty(), game.player2Default.second + game.prisonPosition.second)
                }else {
                    keyvalue(model2.layoutXProperty(), game.player2Default.first + game.board.fields[a].layoutX)
                    keyvalue(model2.layoutYProperty(), game.player2Default.second + game.board.fields[a].layoutY)
                }
            }
        }
    }
    private fun movePlayer3(a : Int, prison : Boolean){
        timeline {
            keyframe(Duration.seconds(0.5)) {
                if (prison){
                    keyvalue(model3.layoutXProperty(), game.player3Default.first + game.prisonPosition.first)
                    keyvalue(model3.layoutYProperty(), game.player3Default.second + game.prisonPosition.second)
                }else {
                    keyvalue(model3.layoutXProperty(), game.player3Default.first + game.board.fields[a].layoutX)
                    keyvalue(model3.layoutYProperty(), game.player3Default.second + game.board.fields[a].layoutY)
                }
            }
        }
    }
    private fun movePlayer4(a : Int, prison : Boolean){
        timeline {
            keyframe(Duration.seconds(0.5)) {
                if (prison){
                    keyvalue(model4.layoutXProperty(), game.player4Default.first + game.prisonPosition.first)
                    keyvalue(model4.layoutYProperty(), game.player4Default.second + game.prisonPosition.second)
                }else {
                    keyvalue(model4.layoutXProperty(), game.player4Default.first + game.board.fields[a].layoutX)
                    keyvalue(model4.layoutYProperty(), game.player4Default.second + game.board.fields[a].layoutY)
                }
            }
        }
    }
    private fun movePlayer5(a : Int, prison : Boolean){
        timeline {
            keyframe(Duration.seconds(0.5)) {
                if (prison){
                    keyvalue(model5.layoutXProperty(), game.player5Default.first + game.prisonPosition.first)
                    keyvalue(model5.layoutYProperty(), game.player5Default.second + game.prisonPosition.second)
                }else {
                    keyvalue(model5.layoutXProperty(), game.player5Default.first + game.board.fields[a].layoutX)
                    keyvalue(model5.layoutYProperty(), game.player5Default.second + game.board.fields[a].layoutY)
                }
            }
        }
    }
    //start model position

    //player property description
    private val playerField1 : VBox by fxid()
    private val playerField2 : VBox by fxid()
    private val playerField3 : VBox by fxid()
    private val playerField4 : VBox by fxid()
    private val playerField5 : VBox by fxid()
    private val moneylbl1 : Label by fxid()
    private val moneylbl2 : Label by fxid()
    private val moneylbl3 : Label by fxid()
    private val moneylbl4 : Label by fxid()
    private val moneylbl5 : Label by fxid()
    private val pl1 : Label by fxid()
    private val pl2 : Label by fxid()
    private val pl3 : Label by fxid()
    private val pl4 : Label by fxid()
    private val pl5 : Label by fxid()

    //game realty fields
    private val field1 : VBox by fxid()
    private val field1Penalty : Label by fxid()
    val labelUpgrade1 : Label by fxid()
    fun field1Action(){ if (game.canControl(1)) find<Field>().openModal(resizable = false) }
    private val field2 : VBox by fxid()
    private val field2Penalty : Label by fxid()
    val labelUpgrade2 : Label by fxid()
    fun field2Action(){ if (game.canControl(2)) find<Field>().openModal(resizable = false) }
    private val field3 : VBox by fxid()
    private val field3Penalty : Label by fxid()
    val labelUpgrade3 : Label by fxid()
    fun field3Action(){ if (game.canControl(3)) find<Field>().openModal(resizable = false) }
    private val field5 : VBox by fxid()
    private val field5Penalty : Label by fxid()
    val labelUpgrade5 : Label by fxid()
    fun field5Action(){ if (game.canControl(5)) find<Field>().openModal(resizable = false) }
    private val field6 : VBox by fxid()
    private val field6Penalty : Label by fxid()
    val labelUpgrade6 : Label by fxid()
    fun field6Action(){ if (game.canControl(6)) find<Field>().openModal(resizable = false) }
    private val field8 : HBox by fxid()
    private val field8Penalty : Label by fxid()
    val labelUpgrade8 : Label by fxid()
    fun field8Action(){ if (game.canControl(8))  find<Field>().openModal(resizable = false) }
    private val field9 : HBox by fxid()
    private val field9Penalty : Label by fxid()
    val labelUpgrade9 : Label by fxid()
    fun field9Action(){ if (game.canControl(9)) find<Field>().openModal(resizable = false) }
    private val field10 : HBox by fxid()
    private val field10Penalty : Label by fxid()
    val labelUpgrade10 : Label by fxid()
    fun field10Action(){ if (game.canControl(10)) find<Field>().openModal(resizable = false) }
    private val field11 : HBox by fxid()
    private val field11Penalty : Label by fxid()
    val labelUpgrade11 : Label by fxid()
    fun field11Action(){ if (game.canControl(11)) find<Field>().openModal(resizable = false) }
    private val field13 : HBox by fxid()
    private val field13Penalty : Label by fxid()
    val labelUpgrade13 : Label by fxid()
    fun field13Action(){ if (game.canControl(13)) find<Field>().openModal(resizable = false) }
    private val field15 : VBox by fxid()
    private val field15Penalty : Label by fxid()
    val labelUpgrade15 : Label by fxid()
    fun field15Action(){ if (game.canControl(15)) find<Field>().openModal(resizable = false) }
    private val field16 : VBox by fxid()
    private val field16Penalty : Label by fxid()
    val labelUpgrade16 : Label by fxid()
    fun field16Action(){ if (game.canControl(16)) find<Field>().openModal(resizable = false) }
    private val field17 : VBox by fxid()
    private val field17Penalty : Label by fxid()
    val labelUpgrade17 : Label by fxid()
    fun field17Action(){ if (game.canControl(17)) find<Field>().openModal(resizable = false) }
    private val field19 : VBox by fxid()
    private val field19Penalty : Label by fxid()
    val labelUpgrade19 : Label by fxid()
    fun field19Action(){ if (game.canControl(19)) find<Field>().openModal(resizable = false) }
    private val field22 : HBox by fxid()
    private val field22Penalty : Label by fxid()
    val labelUpgrade22 : Label by fxid()
    fun field22Action(){ if (game.canControl(22)) find<Field>().openModal(resizable = false) }
    private val field24 : HBox by fxid()
    private val field24Penalty : Label by fxid()
    val labelUpgrade24 : Label by fxid()
    fun field24Action(){ if (game.canControl(24)) find<Field>().openModal(resizable = false) }
    private val field25 : HBox by fxid()
    private val field25Penalty : Label by fxid()
    val labelUpgrade25 : Label by fxid()
    fun field25Action(){ if (game.canControl(25)) find<Field>().openModal(resizable = false) }
    private val field26 : HBox by fxid()
    private val field26Penalty : Label by fxid()
    val labelUpgrade26 : Label by fxid()
    fun field26Action(){ if (game.canControl(26)) find<Field>().openModal(resizable = false) }
    private val field27 : HBox by fxid()
    private val field27Penalty : Label by fxid()
    val labelUpgrade27 : Label by fxid()
    fun field27Action(){ if (game.canControl(27)) find<Field>().openModal(resizable = false) }

    //paint field by owner
    fun paintField(number: Int, color : Color){
        when(number){
            1 -> {
                labelUpgrade1.text = ""
                field1.style(append = true){backgroundColor += color;}
            }
            2 -> {
                labelUpgrade2.text = ""
                field2.style(append = true){backgroundColor += color}
            }
            3 -> {
                labelUpgrade3.text = ""
                field3.style(append = true){backgroundColor += color}
            }
            5 -> {
                labelUpgrade5.text = ""
                field5.style(append = true){backgroundColor += color}
            }
            6 -> {
                labelUpgrade6.text = ""
                field6.style(append = true){backgroundColor += color}
            }
            8 -> {
                labelUpgrade8.text = ""
                field8.style(append = true){backgroundColor += color}
            }
            9 -> {
                labelUpgrade9.text = ""
                field9.style(append = true){backgroundColor += color}
            }
            10 -> {
                labelUpgrade10.text = ""
                field10.style(append = true){backgroundColor += color}
            }
            11 -> {
                labelUpgrade11.text = ""
                field11.style(append = true){backgroundColor += color}
            }
            13 -> {
                labelUpgrade13.text = ""
                field13.style(append = true){backgroundColor += color}
            }
            15 -> {
                labelUpgrade15.text = ""
                field15.style(append = true){backgroundColor += color}
            }
            16 -> {
                labelUpgrade16.text = ""
                field16.style(append = true){backgroundColor += color}
            }
            17 -> {
                labelUpgrade17.text = ""
                field17.style(append = true){backgroundColor += color}
            }
            19 -> {
                labelUpgrade19.text = ""
                field19.style(append = true){backgroundColor += color}
            }
            22 -> {
                labelUpgrade22.text = ""
                field22.style(append = true){backgroundColor += color}
            }
            24 -> {
                labelUpgrade24.text = ""
                field24.style(append = true){backgroundColor += color}
            }
            25 -> {
                labelUpgrade25.text = ""
                field25.style(append = true){backgroundColor += color}
            }
            26 -> {
                labelUpgrade26.text = ""
                field26.style(append = true){backgroundColor += color}
            }
            else -> {
                labelUpgrade27.text = ""
                field27.style(append = true){backgroundColor += color}
            }
        }
    }

    //game dice
    private val firstdice1 : ImageView by fxid()
    private val firstdice2 : ImageView by fxid()
    private val firstdice3 : ImageView by fxid()
    private val firstdice4 : ImageView by fxid()
    private val seconddice1 : ImageView by fxid()
    private val seconddice2 : ImageView by fxid()
    private val seconddice3 : ImageView by fxid()
    private val seconddice4 : ImageView by fxid()
    //animation
    private fun diceRoll(a : Int, b : Int){
        timeline {
            keyframe(Duration.seconds(0.01)) {
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
        timeline {
            keyframe(Duration.seconds(0.5)) {
                when(a){
                    1 -> keyvalue(firstdice1.opacityProperty(), 1.0)
                    2 -> keyvalue(firstdice2.opacityProperty(), 1.0)
                    3 -> keyvalue(firstdice3.opacityProperty(), 1.0)
                    else -> keyvalue(firstdice4.opacityProperty(), 1.0)
                }
                when(b){
                    1 -> keyvalue(seconddice1.opacityProperty(), 1.0)
                    2 -> keyvalue(seconddice2.opacityProperty(), 1.0)
                    3 -> keyvalue(seconddice3.opacityProperty(), 1.0)
                    else -> keyvalue(seconddice4.opacityProperty(), 1.0)
                }
            }
        }
    }

    init {
        primaryStage.width = 1024.0
        primaryStage.height = 1048.0
        primaryStage.centerOnScreen()

        game.cntPls = game.data.size

        idMotion.text = "Ход игрока ${game.data[0].name}"

        pl1.text += game.data[0].name
        moneylbl1.bind(game.data[0].moneyProperty)

        pl2.text += game.data[1].name
        moneylbl2.bind(game.data[1].moneyProperty)

        if (game.cntPls >= 3) {
            playerField3.opacity = 1.0
            model3.opacity = 1.0
            pl3.text += game.data[2].name
            moneylbl3.bind(game.data[2].moneyProperty)
        }
        if (game.cntPls >= 4) {
            playerField4.opacity = 1.0
            model4.opacity = 1.0
            pl4.text += game.data[3].name
            moneylbl4.bind(game.data[3].moneyProperty)
        }
        if (game.cntPls >= 5) {
            playerField5.opacity = 1.0
            model5.opacity = 1.0
            pl5.text += game.data[4].name
            moneylbl5.bind(game.data[4].moneyProperty)
        }
        game.setBalance()
        linkCostOfField()
    }

    private fun linkCostOfField(){
        field1Penalty.bind(game.board.fields[1].penaltyProperty)
        field2Penalty.bind(game.board.fields[2].penaltyProperty)
        field3Penalty.bind(game.board.fields[3].penaltyProperty)
        field5Penalty.bind(game.board.fields[5].penaltyProperty)
        field6Penalty.bind(game.board.fields[6].penaltyProperty)
        field8Penalty.bind(game.board.fields[8].penaltyProperty)
        field9Penalty.bind(game.board.fields[9].penaltyProperty)
        field10Penalty.bind(game.board.fields[10].penaltyProperty)
        field11Penalty.bind(game.board.fields[11].penaltyProperty)
        field13Penalty.bind(game.board.fields[13].penaltyProperty)
        field15Penalty.bind(game.board.fields[15].penaltyProperty)
        field16Penalty.bind(game.board.fields[16].penaltyProperty)
        field17Penalty.bind(game.board.fields[17].penaltyProperty)
        field19Penalty.bind(game.board.fields[19].penaltyProperty)
        field22Penalty.bind(game.board.fields[22].penaltyProperty)
        field24Penalty.bind(game.board.fields[24].penaltyProperty)
        field25Penalty.bind(game.board.fields[25].penaltyProperty)
        field26Penalty.bind(game.board.fields[26].penaltyProperty)
        field27Penalty.bind(game.board.fields[27].penaltyProperty)
    }

    private fun clearFieldLooser(current : Game.Player){
        for (i in current.realty){
            paintField(i.location, c("#d2edd7"))
            game.fieldClear(i)
        }
        when (current.id){
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

    private fun secretAction(){
        //positive
        if (game.secretIsPositive()){
            find<SomeActionAlert>().openModal(resizable = false)
            endMotion()
        }else{//negative
            negativeInit()
        }
    }

    private fun playerSurrender(){
        clearFieldLooser(game.data[game.presentId])
        game.playerLose()
        endMotion()
        checkEndGame()
    }

    private fun playerToPrison(current: Game.Player){
        game.motionIfPrison(current)
        when(current.id){
            1 -> movePlayer1(0, true)
            2 -> movePlayer2(0, true)
            3 -> movePlayer3(0, true)
            4 -> movePlayer4(0, true)
            else -> movePlayer5(0, true)
        }
        find<SomeActionAlert>().openModal(resizable = false)
        runAsync { Thread.sleep(50) }ui{endMotion()}
    }

    private fun diceDoubleAlert(){
        if (game.diceDoubleCheck()) find<DiceDouble>().openModal(resizable = false)
    }

    private fun fieldEvent(){
        //buy realty
        if (game.realtyCanBuy()){
            offerToBuy()
            diceDoubleAlert()
            return
        }
        //pay penalty
        if (game.punishmentOrPenalty()){
            payPenalty()
            diceDoubleAlert()
            return
        }
        //player to prison
        if (game.ifToPrison()){
            playerToPrison(game.data[game.presentId])
            return
        }
        //get stonks
        if (game.ifStonks()){
            find<SomeActionAlert>().openModal(resizable = false)
        }
        //secret action
        if (game.ifSecret()){
            secretAction()
            diceDoubleAlert()
            return
        }
        //start field
        if (game.ifStart()){
            find<SomeActionAlert>().openModal(resizable = false)
        }

        diceDoubleAlert()
        endMotion()
    }

    private fun playerMove(){
        runAsync {
            Thread.sleep(250)
        }ui{
            game.data[game.presentId].positionChange(game.dice.count)
            when(game.presentId){
                0 -> movePlayer1(game.data[0].position, false)
                1 -> movePlayer2(game.data[1].position, false)
                2 -> movePlayer3(game.data[2].position, false)
                3 -> movePlayer4(game.data[3].position, false)
                else -> movePlayer5(game.data[4].position, false)
            }
            //check cycle completed and reward according to the settings
            if (game.checkCircleComplete()){
                runAsync { Thread.sleep(300) }ui{find<CycleComplete>().openModal(resizable = false)}
            }

            runAsync {
                Thread.sleep(500)
            }ui{
                runAsync { Thread.sleep(50) }ui{fieldEvent()}
                game.motionNext()
            }
        }
    }

    fun motion(){
        buttonRoll.disableProperty().value = true
        if (game.data[game.presentId].playerInPrison()){
            prisonInit()
            return
        }
        game.dice.roll()
        diceRoll(game.dice.first, game.dice.second)
        runAsync { Thread.sleep(500) }ui {
            if (game.checkPrisonByDouble()) {
                playerToPrison(game.data[game.presentId])
            } else {
                playerMove()
            }
        }
    }

    private fun endMotion(){
        runAsync { Thread.sleep(300) }ui{
            game.endMotionLogic()
            when (game.motionPlayer){
                0 -> idMotion.text = "Ход игрока ${game.data[0].name}"
                1 -> idMotion.text = "Ход игрока ${game.data[1].name}"
                2 -> idMotion.text = "Ход игрока ${game.data[2].name}"
                3 -> idMotion.text = "Ход игрока ${game.data[3].name}"
                else -> idMotion.text = "Ход игрока ${game.data[4].name}"
            }
            buttonRoll.disableProperty().value = false
            if (game.data[game.motionPlayer].playerInPrison()) motion()
        }
    }

    private fun checkEndGame(){
        runAsync { Thread.sleep(350) }ui {
            if (game.gameIsEnd()) {
                find<FinishGame>().openModal(resizable = false)
            }
        }
    }

    fun howToPlay(){
        find<HowToPlay>().openModal(resizable = false)
    }

    fun newGame(){
        replaceWith(Begin(), ViewTransition.Implode(0.5.seconds))
    }

    fun exit(){
        primaryStage.close()
    }
}