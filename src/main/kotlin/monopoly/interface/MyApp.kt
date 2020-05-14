package monopoly.`interface`


import com.sun.org.apache.xpath.internal.operations.Bool
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.control.TextField
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.layout.*
import javafx.scene.paint.Color
import javafx.scene.shape.Rectangle
import javafx.scene.text.Text
import javafx.stage.Stage
import javafx.util.Duration
import monopoly.logic.Game
import monopoly.logic.Type
import tornadofx.*

class MyApp: App(Begin::class){
    override fun start(stage: Stage) {
        with(stage) {
        isResizable = false
    }
        stage.icons.add(Image("file:src/main/resources/monopoly/dice.png"))
        super.start(stage)
    }
}

var data = mutableListOf<Game.Player>()

var gamePlay = GamePlay()

var board = Game.GameBoard()

var motionPlayer = 0

var loosers = mutableListOf<Int>()

class Begin : View("Monopoly"){

    override val root : BorderPane by fxml()

    private val playfield1 : TextField by fxid()

    private val playfield2 : TextField by fxid()

    private val playfield3 : TextField by fxid()

    private val playfield4 : TextField by fxid()

    private val playfield5 : TextField by fxid()

    fun active3(){
        if (playfield3.disableProperty().value == true) {
            playfield3.disableProperty().value = false
            playfield3.text = "Player 3"
        }
        else {
            playfield3.disableProperty().value = true
            playfield3.text = ""
        }
    }

    fun active4(){
        if (playfield4.disableProperty().value == true) {
            playfield4.disableProperty().value = false
            playfield4.text = "Player 4"
        }
        else {
            playfield4.disableProperty().value = true
            playfield4.text = ""
        }
    }

    fun active5(){
        if (playfield5.disableProperty().value == true) {
            playfield5.disableProperty().value = false
            playfield5.text = "Player 5"
        }
        else {
            playfield5.disableProperty().value = true
            playfield5.text = ""
        }
    }

    init {
        primaryStage.width = 600.0
        primaryStage.height = 420.0
        primaryStage.centerOnScreen()
    }

    fun startGame(){
        data.add(Game.Player(1))
        data[0].name = playfield1.text
        data.add(Game.Player(2))
        data[1].name = playfield2.text

        if (playfield3.disableProperty().value == false){
            data.add(Game.Player(data.size + 1))
            data.last().name = playfield3.text
        }

        if (playfield4.disableProperty().value == false){
            data.add(Game.Player(data.size + 1))
            data.last().name = playfield4.text
        }

        if (playfield5.disableProperty().value == false){
            data.add(Game.Player(data.size + 1))
            data.last().name = playfield5.text
        }

        gamePlay.root.clear()
        gamePlay = GamePlay()
        replaceWith(gamePlay, ViewTransition.Explode(0.5.seconds))
    }

}

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
        offerCostRealty.text = "${board.fields[data[gamePlay.presentId].position].cost}"
        offerTypeRealty.text = "${board.fields[data[gamePlay.presentId].position].type}"
    }

    fun offerAccept(){
        if ( data[gamePlay.presentId].money >= board.fields[data[gamePlay.presentId].position].cost){
            data[gamePlay.presentId].moneyChange(-board.fields[data[gamePlay.presentId].position].cost)
            data[gamePlay.presentId].realty.add(board.fields[data[gamePlay.presentId].position])
            board.fields[data[gamePlay.presentId].position].owner = data[gamePlay.presentId]
            when(gamePlay.presentId){
                0 -> gamePlay.paintField(data[gamePlay.presentId].position,c("#f13030"))
                1 -> gamePlay.paintField(data[gamePlay.presentId].position,c("#f27330"))
                2 -> gamePlay.paintField(data[gamePlay.presentId].position,c("green"))
                3 -> gamePlay.paintField(data[gamePlay.presentId].position,c("#03a3d1"))
                else -> gamePlay.paintField(data[gamePlay.presentId].position,c("#eb15dc"))
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
        if (board.fields[data[gamePlay.presentId].position].type != Type.Punisment) {
            penaltyOwner.text = board.fields[data[gamePlay.presentId].position].owner!!.name
        }else {
            textPunisment.text = "Вас уличили за неуплату налогов!"
        }
        penaltyCost.text = "${board.fields[data[gamePlay.presentId].position].penalty}"
    }

    fun penaltyAccept(){
        if (data[gamePlay.presentId].money >= board.fields[data[gamePlay.presentId].position].penalty){
            data[gamePlay.presentId].moneyChange(-board.fields[data[gamePlay.presentId].position].penalty)
            if (board.fields[data[gamePlay.presentId].position].type != Type.Punisment) {
                board.fields[data[gamePlay.presentId].position].owner!!.moneyChange(board.fields[data[gamePlay.presentId].position].penalty)
            }
            penaltyNotEnoughMoney.opacity = 0.0
            payPenalty.opacity = 0.0
            payPenalty.disableProperty().value = true
            endMotion()
            return
        }
        penaltyNotEnoughMoney.opacity = 1.0
    }

    fun penaltySurrender(){
        penaltyNotEnoughMoney.opacity = 0.0
        payPenalty.opacity = 0.0
        payPenalty.disableProperty().value = true
        clearFieldLooser(data[presentId])
        loosers.add(presentId)
        endMotion()
        checkEndGame()
    }

    //Prison block
    private val prison : AnchorPane by fxid()
    private val prisonCountMoves : Label by fxid()
    private val prisonNotEnoughMoney : Label by fxid()
    private val prisonMessage : Label by fxid()
    private val prisonTryButton : Button by fxid()
    private val prisonSurrenderButton : Button by fxid()

    private fun prisonInit(){
        prisonMessage.text = "Заплатите 500К, или выбейте дубль"
        prisonNotEnoughMoney.opacity = 0.0
        prison.opacity = 1.0
        prison.disableProperty().value = false
        if (data[presentId].prisonDays == 4) {
            prisonMessage.text = "Заплатите 750К"
            prisonTryButton.disableProperty().value = true
            prisonSurrenderButton.opacity = 1.0
            prisonSurrenderButton.disableProperty().value = false
        }
        prisonCountMoves.text = "${4 - data[presentId].prisonDays}"
    }

    fun prisonPay(){
        if(data[presentId].prisonDays == 4 && data[presentId].money >= 750){
            data[presentId].prisonDays = 0
            data[presentId].moneyChange(-750)
            prison.opacity = 0.0
            prison.disableProperty().value = true
            prisonSurrenderButton.opacity = 0.0
            prisonSurrenderButton.disableProperty().value = true
            endMotion()
            return
        }
        if (data[presentId].prisonDays != 4 && data[presentId].money >= 500){
            data[presentId].prisonDays = 0
            data[presentId].moneyChange(-500)
            prison.opacity = 0.0
            prison.disableProperty().value = true
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
                find<DiceDouble>().openModal()
            }else{
                motionPlayer++
                motionPlayer %= cntPls
                data[presentId].prisonDays ++
            }
            endMotion()
        }
    }

    fun prisonSurrender(){
        prison.opacity = 0.0
        prison.disableProperty().value = true
        prisonSurrenderButton.opacity = 0.0
        prisonSurrenderButton.disableProperty().value = true
        clearFieldLooser(data[presentId])
        loosers.add(presentId)
        endMotion()
        checkEndGame()
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
    private val field2 : VBox by fxid()
    private val field3 : VBox by fxid()
    private val field5 : VBox by fxid()
    private val field6 : VBox by fxid()
    private val field8 : HBox by fxid()
    private val field9 : HBox by fxid()
    private val field10 : HBox by fxid()
    private val field11 : HBox by fxid()
    private val field13 : HBox by fxid()
    private val field15 : VBox by fxid()
    private val field16 : VBox by fxid()
    private val field17 : VBox by fxid()
    private val field19 : VBox by fxid()
    private val field22 : HBox by fxid()
    private val field24 : HBox by fxid()
    private val field25 : HBox by fxid()
    private val field26 : HBox by fxid()
    private val field27 : HBox by fxid()
    //paint field by owner
    private fun paintField(number: Int, color : Color){
        when(number){
            1 -> field1.style(append = true){backgroundColor += color;}
            2 -> field2.style(append = true){backgroundColor += color}
            3 -> field3.style(append = true){backgroundColor += color}
            5 -> field5.style(append = true){backgroundColor += color}
            6 -> field6.style(append = true){backgroundColor += color}
            8 -> field8.style(append = true){backgroundColor += color}
            9 -> field9.style(append = true){backgroundColor += color}
            10 -> field10.style(append = true){backgroundColor += color}
            11 -> field11.style(append = true){backgroundColor += color}
            13 -> field13.style(append = true){backgroundColor += color}
            15 -> field15.style(append = true){backgroundColor += color}
            16 -> field16.style(append = true){backgroundColor += color}
            17 -> field17.style(append = true){backgroundColor += color}
            19 -> field19.style(append = true){backgroundColor += color}
            22 -> field22.style(append = true){backgroundColor += color}
            24 -> field24.style(append = true){backgroundColor += color}
            25 -> field25.style(append = true){backgroundColor += color}
            26 -> field26.style(append = true){backgroundColor += color}
            else -> field27.style(append = true){backgroundColor += color}
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
            keyframe(Duration.seconds(1.0)) {
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

        println(cntPls)

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
    }

    private fun clearFieldLooser(current : Game.Player){
        for (i in current.realty){
            paintField(i.location,c("#d2edd7"))
            board.fields[i.location].owner = null
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

    private fun playerToPrison(current: Game.Player){
        current.goToPrison()
        when(current.id){
            1 -> movePlayer1(0, true)
            2 -> movePlayer2(0, true)
            3 -> movePlayer3(0, true)
            4 -> movePlayer4(0, true)
            else -> movePlayer5(0, true)
        }
        find<SomeActionAlert>().openModal()
        runAsync { Thread.sleep(50) }ui{endMotion()}
    }

    private fun fieldEvent(){
        println("Event start")
        if (board.fields[data[gamePlay.presentId].position].couldBuy && board.fields[data[gamePlay.presentId].position].owner == null){
            offerToBuy()
            if (dice.double) find<DiceDouble>().openModal()
            return
        }
        if (board.fields[data[gamePlay.presentId].position].type == Type.Punisment ||
            (board.fields[data[gamePlay.presentId].position].owner != null && board.fields[data[gamePlay.presentId].position].owner!!.id != data[gamePlay.presentId].id)){
            payPenalty()
            if (dice.double) find<DiceDouble>().openModal()
            return
        }
        if (board.fields[data[gamePlay.presentId].position].type == Type.ToPrison){
            playerToPrison(data[presentId])
            if (dice.double){
                motionPlayer++
                motionPlayer %= cntPls
            }
            return
        }
        if (dice.double) find<DiceDouble>().openModal()
        endMotion()
    }

    fun motion(){
        if (data[motionPlayer].playerInPrison()){
            presentId = motionPlayer
            prisonInit()
            return
        }
        buttonRoll.disableProperty().value = true
        dice.roll()
        diceRoll(dice.first, dice.second)
        runAsync {
            Thread.sleep(500)
        }ui{
            when(motionPlayer){
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
            runAsync {
                Thread.sleep(500)
            }ui{
                presentId = motionPlayer
                runAsync { Thread.sleep(50) }ui{fieldEvent()}

                if (!dice.double) motionPlayer ++
                motionPlayer %= cntPls
            }
        }
    }

    private fun endMotion(){
        runAsync { Thread.sleep(300) }ui{
            while (motionPlayer in loosers) {
                motionPlayer++
                motionPlayer %= cntPls
            }
            when (motionPlayer){
                0 -> idMotion.text = "Ход игрока ${data[0].name}"
                1 -> idMotion.text = "Ход игрока ${data[1].name}"
                2 -> idMotion.text = "Ход игрока ${data[2].name}"
                3 -> idMotion.text = "Ход игрока ${data[3].name}"
                else -> idMotion.text = "Ход игрока ${data[4].name}"
            }
            buttonRoll.disableProperty().value = false
        }
    }

    private fun checkEndGame(){
        if (cntPls - loosers.size == 1){
            find<FinishGame>().openModal()
        }
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

class DiceDouble : Fragment(){
    override val root : AnchorPane by fxml()

    fun exit(){
        close()
    }
}

class SomeActionAlert : Fragment(){
    override val root : AnchorPane by fxml()

    private val message : Label by fxid()

    private val player : Label by fxid()

    private val prisonText : Text by fxid()

    init {
        player.text = data[gamePlay.presentId].name
        if(data[gamePlay.presentId].playerInPrison()){
            prisonText.opacity = 1.0
            message.opacity = 0.0
        }
    }

    fun exit(){
        close()
    }
}

class FinishGame : Fragment(){

    override val root : AnchorPane by fxml()
    private val winner : Label by fxid()

    init {
        winner.text = data[motionPlayer].name
        runAsync { Thread.sleep(500) }ui{
            gamePlay.buttonRoll.disableProperty().value = true}

    }

    fun newGame(){
        gamePlay.newGame()
        close()
    }

    fun exit(){
        gamePlay.close()
        close()
    }
}
