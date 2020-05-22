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
import monopoly.logic.Type
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
        offerCostRealty.text = "${board.fields[data[presentId].position].cost}"
        offerTypeRealty.text = "${board.fields[data[presentId].position].type}"
    }

    fun offerAccept(){
        if ( data[presentId].money >= board.fields[data[presentId].position].cost){
            data[presentId].moneyChange(-board.fields[data[presentId].position].cost)
            data[presentId].realty.add(board.fields[data[presentId].position])
            board.fields[data[presentId].position].owner = data[presentId]
            board.fields[data[presentId].position].penaltyUpdate()
            when(presentId){
                0 -> paintField(data[presentId].position, c("#f13030"))
                1 -> paintField(data[presentId].position, c("#f27330"))
                2 -> paintField(data[presentId].position, c("green"))
                3 -> paintField(data[presentId].position, c("#03a3d1"))
                else -> paintField(data[presentId].position, c("#eb15dc"))
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
        if (board.fields[data[presentId].position].type != Type.Punisment) {
            penaltyOwner.text = board.fields[data[presentId].position].owner!!.name
        }else {
            textPunisment.text = "Вас уличили за неуплату налогов!"
        }
        penaltyCost.text = "${board.fields[data[presentId].position].penalty}"
    }

    private fun penaltyClose(){
        penaltyNotEnoughMoney.opacity = 0.0
        payPenalty.opacity = 0.0
        payPenalty.disableProperty().value = true
    }

    fun penaltyAccept(){
        if (data[presentId].money >= board.fields[data[presentId].position].penalty){
            data[presentId].moneyChange(-board.fields[data[presentId].position].penalty)
            if (board.fields[data[presentId].position].type != Type.Punisment) {
                board.fields[data[presentId].position].owner!!.moneyChange(board.fields[data[presentId].position].penalty)
            }
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
        when(dice.secret.second){
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
        when(dice.secret.second){
            SecretAction.Action1 -> if (data[presentId].money >= 300){
                data[presentId].moneyChange(-300)
                negativeClose()
                endMotion()
                return
            }
            SecretAction.Action2 -> if (data[presentId].money >= 500){
                data[presentId].moneyChange(-500)
                negativeClose()
                endMotion()
                return
            }
            SecretAction.Action3 -> if (data[presentId].money >= 40){
                data[presentId].moneyChange(-40)
                negativeClose()
                endMotion()
                return
            }
            SecretAction.Action4 -> if (data[presentId].money >= 750){
                data[presentId].moneyChange(-750)
                negativeClose()
                endMotion()
                return
            }
            else -> if (data[presentId].money >= 250){
                data[presentId].moneyChange(-250)
                negativeClose()
                endMotion()
                return
            }
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
        if (data[presentId].prisonDays == 4) {
            prisonMessage.text = "Заплатите 750"
            prisonTryButton.disableProperty().value = true
            prisonSurrenderButton.opacity = 1.0
            prisonSurrenderButton.disableProperty().value = false
        }
        prisonCountMoves.text = "${4 - data[presentId].prisonDays}"
    }

    private fun prisonClose(){
        prison.opacity = 0.0
        prison.disableProperty().value = true
        prisonSurrenderButton.opacity = 0.0
        prisonSurrenderButton.disableProperty().value = true
    }

    fun prisonPay(){
        if(data[presentId].prisonDays == 4 && data[presentId].money >= 750){
            data[presentId].prisonDays = 0
            data[presentId].moneyChange(-750)
            prisonClose()
            endMotion()
            return
        }
        if (data[presentId].prisonDays != 4 && data[presentId].money >= 500){
            data[presentId].prisonDays = 0
            data[presentId].moneyChange(-500)
            prisonClose()
            endMotion()
            return
        }
        prisonNotEnoughMoney.opacity = 1.0
    }

    fun prisonTry(){
        dice.roll()
        diceRoll(dice.first,dice.second)
        prison.opacity = 0.0
        prison.disableProperty().value = true
        runAsync { Thread.sleep(1000) }ui{
            if (dice.double) {
                data[presentId].prisonDays = 0
                dice.double = false
                playerMove()
            }else{
                motionPlayer++
                motionPlayer %= cntPls
                data[presentId].prisonDays ++
            }
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
                    keyvalue(model1.layoutXProperty(), player1Default.first + prisonPosition.first)
                    keyvalue(model1.layoutYProperty(), player1Default.second + prisonPosition.second)
                }else {
                    keyvalue(model1.layoutXProperty(), player1Default.first + board.fields[a].layoutX)
                    keyvalue(model1.layoutYProperty(), player1Default.second + board.fields[a].layoutY)
                }
            }
        }
    }
    private fun movePlayer2(a : Int, prison : Boolean){
        timeline {
            keyframe(Duration.seconds(0.5)) {
                if (prison){
                    keyvalue(model2.layoutXProperty(), player2Default.first + prisonPosition.first)
                    keyvalue(model2.layoutYProperty(), player2Default.second + prisonPosition.second)
                }else {
                    keyvalue(model2.layoutXProperty(), player2Default.first + board.fields[a].layoutX)
                    keyvalue(model2.layoutYProperty(), player2Default.second + board.fields[a].layoutY)
                }
            }
        }
    }
    private fun movePlayer3(a : Int, prison : Boolean){
        timeline {
            keyframe(Duration.seconds(0.5)) {
                if (prison){
                    keyvalue(model3.layoutXProperty(), player3Default.first + prisonPosition.first)
                    keyvalue(model3.layoutYProperty(), player3Default.second + prisonPosition.second)
                }else {
                    keyvalue(model3.layoutXProperty(), player3Default.first + board.fields[a].layoutX)
                    keyvalue(model3.layoutYProperty(), player3Default.second + board.fields[a].layoutY)
                }
            }
        }
    }
    private fun movePlayer4(a : Int, prison : Boolean){
        timeline {
            keyframe(Duration.seconds(0.5)) {
                if (prison){
                    keyvalue(model4.layoutXProperty(), player4Default.first + prisonPosition.first)
                    keyvalue(model4.layoutYProperty(), player4Default.second + prisonPosition.second)
                }else {
                    keyvalue(model4.layoutXProperty(), player4Default.first + board.fields[a].layoutX)
                    keyvalue(model4.layoutYProperty(), player4Default.second + board.fields[a].layoutY)
                }
            }
        }
    }
    private fun movePlayer5(a : Int, prison : Boolean){
        timeline {
            keyframe(Duration.seconds(0.5)) {
                if (prison){
                    keyvalue(model5.layoutXProperty(), player5Default.first + prisonPosition.first)
                    keyvalue(model5.layoutYProperty(), player5Default.second + prisonPosition.second)
                }else {
                    keyvalue(model5.layoutXProperty(), player5Default.first + board.fields[a].layoutX)
                    keyvalue(model5.layoutYProperty(), player5Default.second + board.fields[a].layoutY)
                }
            }
        }
    }
    //start model position
    private val player1Default = Pair(10.0,30.0)
    private val player2Default = Pair(10.0,40.0)
    private val player3Default = Pair(30.0,30.0)
    private val player4Default = Pair(30.0,40.0)
    private val player5Default = Pair(10.0,50.0)
    private val prisonPosition = Pair(810.0,50.0)

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
    fun field1Action(){ if (canControl(1)) {
        click = 1
        find<Field>().openModal(resizable = false)
    } }
    private val field2 : VBox by fxid()
    private val field2Penalty : Label by fxid()
    val labelUpgrade2 : Label by fxid()
    fun field2Action(){ if (canControl(2)) {
        click = 2
        find<Field>().openModal(resizable = false)
    } }
    private val field3 : VBox by fxid()
    private val field3Penalty : Label by fxid()
    val labelUpgrade3 : Label by fxid()
    fun field3Action(){ if (canControl(3)) {
        click = 3
        find<Field>().openModal(resizable = false)
    } }
    private val field5 : VBox by fxid()
    private val field5Penalty : Label by fxid()
    val labelUpgrade5 : Label by fxid()
    fun field5Action(){ if (canControl(5)) {
        click = 5
        find<Field>().openModal(resizable = false)
    } }
    private val field6 : VBox by fxid()
    private val field6Penalty : Label by fxid()
    val labelUpgrade6 : Label by fxid()
    fun field6Action(){ if (canControl(6)) {
        click = 6
        find<Field>().openModal(resizable = false)
    } }
    private val field8 : HBox by fxid()
    private val field8Penalty : Label by fxid()
    val labelUpgrade8 : Label by fxid()
    fun field8Action(){ if (canControl(8)) {
        click = 8
        find<Field>().openModal(resizable = false)
    } }
    private val field9 : HBox by fxid()
    private val field9Penalty : Label by fxid()
    val labelUpgrade9 : Label by fxid()
    fun field9Action(){ if (canControl(9)) {
        click = 9
        find<Field>().openModal(resizable = false)
    } }
    private val field10 : HBox by fxid()
    private val field10Penalty : Label by fxid()
    val labelUpgrade10 : Label by fxid()
    fun field10Action(){ if (canControl(10)) {
        click = 10
        find<Field>().openModal(resizable = false)
    } }
    private val field11 : HBox by fxid()
    private val field11Penalty : Label by fxid()
    val labelUpgrade11 : Label by fxid()
    fun field11Action(){ if (canControl(11)) {
        click = 11
        find<Field>().openModal(resizable = false)
    } }
    private val field13 : HBox by fxid()
    private val field13Penalty : Label by fxid()
    val labelUpgrade13 : Label by fxid()
    fun field13Action(){ if (canControl(13)) {
        click = 13
        find<Field>().openModal(resizable = false)
    } }
    private val field15 : VBox by fxid()
    private val field15Penalty : Label by fxid()
    val labelUpgrade15 : Label by fxid()
    fun field15Action(){ if (canControl(15)) {
        click = 15
        find<Field>().openModal(resizable = false)
    } }
    private val field16 : VBox by fxid()
    private val field16Penalty : Label by fxid()
    val labelUpgrade16 : Label by fxid()
    fun field16Action(){ if (canControl(16)) {
        click = 16
        find<Field>().openModal(resizable = false)
    } }
    private val field17 : VBox by fxid()
    private val field17Penalty : Label by fxid()
    val labelUpgrade17 : Label by fxid()
    fun field17Action(){ if (canControl(17)) {
        click = 17
        find<Field>().openModal(resizable = false)
    } }
    private val field19 : VBox by fxid()
    private val field19Penalty : Label by fxid()
    val labelUpgrade19 : Label by fxid()
    fun field19Action(){ if (canControl(19)) {
        click = 19
        find<Field>().openModal(resizable = false)
    } }
    private val field22 : HBox by fxid()
    private val field22Penalty : Label by fxid()
    val labelUpgrade22 : Label by fxid()
    fun field22Action(){ if (canControl(22)) {
        click = 22
        find<Field>().openModal(resizable = false)
    } }
    private val field24 : HBox by fxid()
    private val field24Penalty : Label by fxid()
    val labelUpgrade24 : Label by fxid()
    fun field24Action(){ if (canControl(24)) {
        click = 24
        find<Field>().openModal(resizable = false)
    } }
    private val field25 : HBox by fxid()
    private val field25Penalty : Label by fxid()
    val labelUpgrade25 : Label by fxid()
    fun field25Action(){ if (canControl(25)) {
        click = 25
        find<Field>().openModal(resizable = false)
    } }
    private val field26 : HBox by fxid()
    private val field26Penalty : Label by fxid()
    val labelUpgrade26 : Label by fxid()
    fun field26Action(){ if (canControl(26)) {
        click = 26
        find<Field>().openModal(resizable = false)
    } }
    private val field27 : HBox by fxid()
    private val field27Penalty : Label by fxid()
    val labelUpgrade27 : Label by fxid()
    fun field27Action(){ if (canControl(27)) {
        click = 27
        find<Field>().openModal(resizable = false)
    } }

    private fun canControl(number : Int) : Boolean{
        if (board.fields[number].owner == data[presentId]){
            playerClicked = presentId
            return true
        }
        return false
    }

    var playerClicked = 0

    var click = 0

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

    private val dice = Game.Dice()

    private var cntPls = 0 // count of players

    var presentId = 0 // number of the player who is currently walking

    init {
        primaryStage.width = 1024.0
        primaryStage.height = 1048.0
        primaryStage.centerOnScreen()

        cntPls = data.size

        //some balance in ingame value
        if (cntPls == 2){
            for (i in 0..1)data[i].moneyChange(10000)
        }

        if (cntPls == 3){
            for (i in 0..2)data[i].moneyChange(5000)
        }

        if (cntPls >= 1) {
            pl1.text += data[0].name
            moneylbl1.bind(data[0].moneyProperty)
            idMotion.text = "Ход игрока ${data[0].name}"
        }

        if (cntPls >= 2) {
            pl2.text += data[1].name
            moneylbl2.bind(data[1].moneyProperty)
        }

        if (cntPls >= 3) {
            playerField3.opacity = 1.0
            model3.opacity = 1.0
            pl3.text += data[2].name
            moneylbl3.bind(data[2].moneyProperty)
        }
        if (cntPls >= 4) {
            playerField4.opacity = 1.0
            model4.opacity = 1.0
            pl4.text += data[3].name
            moneylbl4.bind(data[3].moneyProperty)
        }
        if (cntPls >= 5) {
            playerField5.opacity = 1.0
            model5.opacity = 1.0
            pl5.text += data[4].name
            moneylbl5.bind(data[4].moneyProperty)
        }
        linkCostOfField()
    }

    private fun linkCostOfField(){
        field1Penalty.bind(board.fields[1].penaltyProperty)
        field2Penalty.bind(board.fields[2].penaltyProperty)
        field3Penalty.bind(board.fields[3].penaltyProperty)
        field5Penalty.bind(board.fields[5].penaltyProperty)
        field6Penalty.bind(board.fields[6].penaltyProperty)
        field8Penalty.bind(board.fields[8].penaltyProperty)
        field9Penalty.bind(board.fields[9].penaltyProperty)
        field10Penalty.bind(board.fields[10].penaltyProperty)
        field11Penalty.bind(board.fields[11].penaltyProperty)
        field13Penalty.bind(board.fields[13].penaltyProperty)
        field15Penalty.bind(board.fields[15].penaltyProperty)
        field16Penalty.bind(board.fields[16].penaltyProperty)
        field17Penalty.bind(board.fields[17].penaltyProperty)
        field19Penalty.bind(board.fields[19].penaltyProperty)
        field22Penalty.bind(board.fields[22].penaltyProperty)
        field24Penalty.bind(board.fields[24].penaltyProperty)
        field25Penalty.bind(board.fields[25].penaltyProperty)
        field26Penalty.bind(board.fields[26].penaltyProperty)
        field27Penalty.bind(board.fields[27].penaltyProperty)
    }

    private fun clearFieldLooser(current : Game.Player){
        for (i in current.realty){
            paintField(i.location, c("#d2edd7"))
            board.fields[i.location].owner = null
            board.fields[i.location].upgrade = 0
            board.fields[i.location].penaltyUpdate()
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
        dice.secretAction()
        //positive
        if (dice.secret.first){
            find<SomeActionAlert>().openModal(resizable = false)
            endMotion()
        }else{//negative
            negativeInit()
        }
    }

    private fun playerSurrender(){
        clearFieldLooser(data[presentId])
        loosers.add(presentId)
        endMotion()
        checkEndGame()
    }

    private fun playerToPrison(current: Game.Player){
        current.goToPrison()
        when(current.id){
            1 -> movePlayer1(0, true)
            2 -> movePlayer2(0, true)
            3 -> movePlayer3(0, true)
            4 -> movePlayer4(0, true)
            else -> movePlayer5(0, true)
        }
        find<SomeActionAlert>().openModal(resizable = false)
        if (dice.double){
            motionPlayer++
            motionPlayer %= cntPls
        }
        data[presentId].doubleInARow = 0
        runAsync { Thread.sleep(50) }ui{endMotion()}
    }

    private fun diceDoubleAlert(){
        if (dice.double) {
            find<DiceDouble>().openModal(resizable = false)
            data[presentId].doubleInARow ++
        }else {
            data[presentId].doubleInARow = 0
        }
    }

    private fun fieldEvent(){
        //buy realty
        if (board.fields[data[presentId].position].couldBuy && board.fields[data[presentId].position].owner == null){
            offerToBuy()
            diceDoubleAlert()
            return
        }
        //pay penalty
        if (board.fields[data[presentId].position].type == Type.Punisment ||
            (board.fields[data[presentId].position].owner != null && board.fields[data[presentId].position].owner!!.id != data[presentId].id)){
            payPenalty()
            diceDoubleAlert()
            return
        }
        //player to prison
        if (board.fields[data[presentId].position].type == Type.ToPrison){
            playerToPrison(data[presentId])
            return
        }
        //get stonks
        if (board.fields[data[presentId].position].type == Type.Stonks){
            find<SomeActionAlert>().openModal(resizable = false)
            data[presentId].moneyChange(3000)
        }
        //secret action
        if (board.fields[data[presentId].position].type == Type.Secret){
            secretAction()
            diceDoubleAlert()
            return
        }
        //start field
        if (board.fields[data[presentId].position].type == Type.Start){
            find<SomeActionAlert>().openModal(resizable = false)
            data[presentId].moneyChange(1000)
        }

        diceDoubleAlert()
        endMotion()
    }

    private fun playerMove(){
        runAsync {
            Thread.sleep(250)
        }ui{
            when(presentId){
                0 -> {
                    data[0].positionChange(dice.count)
                    movePlayer1(data[0].position, false)
                }
                1 -> {
                    data[1].positionChange(dice.count)
                    movePlayer2(data[1].position, false)
                }
                2 -> {
                    data[2].positionChange(dice.count)
                    movePlayer3(data[2].position, false)
                }
                3 -> {
                    data[3].positionChange(dice.count)
                    movePlayer4(data[3].position, false)
                }
                else -> {
                    data[4].positionChange(dice.count)
                    movePlayer5(data[4].position, false)
                }
            }
            //check cycle completed and reward according to the settings
            if ((data[presentId].finishCircle && data[presentId].circlesCompleted < 5)||
                (data[presentId].finishCircle && data[presentId].circlesCompleted < 10 && cntPls < 4)){
                runAsync { Thread.sleep(300) }ui{find<CycleComplete>().openModal(resizable = false)}
            }
            runAsync {
                Thread.sleep(500)
            }ui{
                runAsync { Thread.sleep(50) }ui{fieldEvent()}

                if (!dice.double) motionPlayer ++
                motionPlayer %= cntPls
            }
        }
    }

    fun motion(){
        buttonRoll.disableProperty().value = true
        if (data[presentId].playerInPrison()){
            prisonInit()
            return
        }
        dice.roll()
        diceRoll(dice.first, dice.second)
        runAsync { Thread.sleep(500) }ui {
            if (dice.double && data[presentId].doubleInARow == 2) {
                playerToPrison(data[presentId])
                dice.double = false
            } else {
                playerMove()
            }
        }
    }

    private fun endMotion(){
        data[presentId].currentMotionUpgrade.clear()
        runAsync { Thread.sleep(300) }ui{
            while (motionPlayer in loosers) {
                motionPlayer++
                motionPlayer %= cntPls
            }
            presentId = motionPlayer
            when (motionPlayer){
                0 -> idMotion.text = "Ход игрока ${data[0].name}"
                1 -> idMotion.text = "Ход игрока ${data[1].name}"
                2 -> idMotion.text = "Ход игрока ${data[2].name}"
                3 -> idMotion.text = "Ход игрока ${data[3].name}"
                else -> idMotion.text = "Ход игрока ${data[4].name}"
            }
            buttonRoll.disableProperty().value = false
            if (data[motionPlayer].playerInPrison())motion()
        }
    }

    private fun checkEndGame(){
        runAsync { Thread.sleep(350) }ui {
            if (cntPls - loosers.size == 1) {
                find<FinishGame>().openModal(resizable = false)
            }
        }
    }

    fun howToPlay(){
        find<HowToPlay>().openModal(resizable = false)
    }

    fun newGame(){
        motionPlayer = 0
        board = Game.GameBoard()
        data.clear()
        replaceWith(Begin(), ViewTransition.Implode(0.5.seconds))
        loosers.clear()
    }

    fun exit(){
        primaryStage.close()
    }
}