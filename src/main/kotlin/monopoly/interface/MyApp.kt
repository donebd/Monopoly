package monopoly.`interface`


import javafx.animation.KeyFrame
import javafx.animation.Timeline
import javafx.beans.property.SimpleIntegerProperty
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.control.TextField
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.layout.*
import javafx.scene.paint.Color
import javafx.scene.shape.Rectangle
import javafx.stage.Stage
import javafx.util.Duration
import monopoly.logic.Game
import tornadofx.*
import kotlin.concurrent.timer

class MyApp: App(Begin::class){
    override fun start(stage: Stage) {
        with(stage) {
        isResizable = false
    }
        stage.icons.add(Image("file:src/main/resources/monopoly/dice.png"))
        super.start(stage)
    }
}

val data = mutableListOf<Game.Player>()

var gamePlay = GamePlay()

val board = Game.GameBoard()

var motionPlayer = 0

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
            playfield5.text = "Player 3"
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

        for (i in data) println(i.name)
        gamePlay.root.clear()
        gamePlay = GamePlay()
            replaceWith(gamePlay, ViewTransition.Explode(0.5.seconds))
    }

}

class GamePlay: View("Monopoly"){

    override val root : AnchorPane by fxml()

    private val idMotion : Label by fxid()

    private val buttonRoll : Button by fxid()

    private val model1 : Rectangle by fxid()
    private val model2 : Rectangle by fxid()
    private val model3 : Rectangle by fxid()
    private val model4 : Rectangle by fxid()
    private val model5 : Rectangle by fxid()

    private val pl1 : Label by fxid()
    private val pl2 : Label by fxid()
    private val pl3 : Label by fxid()
    private val pl4 : Label by fxid()
    private val pl5 : Label by fxid()

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

    private val playerField3 : VBox by fxid()
    private val playerField4 : VBox by fxid()
    private val playerField5 : VBox by fxid()

    private val moneylbl1 : Label by fxid()
    private val moneylbl2 : Label by fxid()
    private val moneylbl3 : Label by fxid()
    private val moneylbl4 : Label by fxid()
    private val moneylbl5 : Label by fxid()

    private val player1Default = Pair(10.0,30.0)
    private val player2Default = Pair(10.0,40.0)
    private val player3Default = Pair(30.0,30.0)
    private val player4Default = Pair(30.0,40.0)
    private val player5Default = Pair(10.0,50.0)

    private val firstdice1 : ImageView by fxid()
    private val firstdice2 : ImageView by fxid()
    private val firstdice3 : ImageView by fxid()
    private val firstdice4 : ImageView by fxid()

    private val seconddice1 : ImageView by fxid()
    private val seconddice2 : ImageView by fxid()
    private val seconddice3 : ImageView by fxid()
    private val seconddice4 : ImageView by fxid()

    private val dice = Game.Dice()

    var presentId = 0

    private var cntPls = 0

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

    private fun diceRoll(a : Int, b : Int){
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

    fun paintField(number: Int,color : Color){
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

    private fun fieldEvent(a: Int){
        println("Event запущен")
        if (board.fields[data[a].position].couldBuy && data[a].money >= board.fields[data[a].position].cost
            && board.fields[data[a].position].owner == null){
            find<OfferToBuy>().openWindow()
        }
    }

    fun motion(){
        buttonRoll.disableProperty().value = true
        dice.roll()
        diceRoll(dice.first, dice.second)
            when(motionPlayer){
                0 -> {
                    data[0].positionChange(dice.count)
                    movePlayer1(data[0].position)
                }
                1 -> {
                    data[1].positionChange(dice.count)
                    movePlayer2(data[1].position)
                }
                2 -> {
                    data[2].positionChange(dice.count)
                    movePlayer3(data[2].position)
                }
                3 -> {
                    data[3].positionChange(dice.count)
                    movePlayer4(data[3].position)
                }
                else -> {
                    data[4].positionChange(dice.count)
                    movePlayer5(data[4].position)
                }
            }
        presentId = motionPlayer
        fieldEvent(motionPlayer)
        println("when пройден")
            if (!dice.double) motionPlayer ++
            motionPlayer %= cntPls
        println("motionPlayer = $motionPlayer")
            if (dice.double) find<DiceDouble>().openModal()
                when (motionPlayer){
                    0 -> idMotion.text = "Ход игрока ${data[0].name}"
                    1 -> idMotion.text = "Ход игрока ${data[1].name}"
                    2 -> idMotion.text = "Ход игрока ${data[2].name}"
                    3 -> idMotion.text = "Ход игрока ${data[3].name}"
                    else -> idMotion.text = "Ход игрока ${data[4].name}"
                }
                buttonRoll.disableProperty().value = false
    }

    fun movePlayer1(a : Int){
        timeline {
            keyframe(Duration.seconds(0.5)) {
                keyvalue(model1.layoutXProperty(),player1Default.first + board.fields[a].layoutX)
                keyvalue(model1.layoutYProperty(),player1Default.second + board.fields[a].layoutY)
            }
        }
    }

    fun movePlayer2(a : Int){
        timeline {
            keyframe(Duration.seconds(0.5)) {
                keyvalue(model2.layoutXProperty(),player2Default.first + board.fields[a].layoutX)
                keyvalue(model2.layoutYProperty(),player2Default.second + board.fields[a].layoutY)
            }
        }
    }

    fun movePlayer3(a : Int){
        timeline {
            keyframe(Duration.seconds(0.5)) {
                keyvalue(model3.layoutXProperty(),player3Default.first + board.fields[a].layoutX)
                keyvalue(model3.layoutYProperty(),player3Default.second + board.fields[a].layoutY)
            }
        }
    }

    fun movePlayer4(a : Int){
        timeline {
            keyframe(Duration.seconds(0.5)) {
                keyvalue(model4.layoutXProperty(),player4Default.first + board.fields[a].layoutX)
                keyvalue(model4.layoutYProperty(),player4Default.second + board.fields[a].layoutY)
            }
        }
    }

    fun movePlayer5(a : Int){
        timeline {
            keyframe(Duration.seconds(0.5)) {
                keyvalue(model5.layoutXProperty(),player5Default.first + board.fields[a].layoutX)
                keyvalue(model5.layoutYProperty(),player5Default.second + board.fields[a].layoutY)
            }
        }
    }

    fun newGame(){
        data.clear()
        replaceWith(Begin(), ViewTransition.Implode(0.5.seconds))
    }

    fun exit(){
        primaryStage.close()
    }
}

class OfferToBuy : Fragment(){
    override val root : AnchorPane by fxml()

    private val moneyPlayer : Label by fxid()
    private val costRealty : Label by fxid()
    private val typeRealty : Label by fxid()

    init {
        moneyPlayer.text = "${data[gamePlay.presentId].money}"
        costRealty.text = "${board.fields[data[gamePlay.presentId].position].cost}"
        typeRealty.text = "${board.fields[data[gamePlay.presentId].position].type}"
    }

    fun accept(){
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
        close()
    }

    fun reject(){

        close()
    }
}

class DiceDouble : Fragment(){
    override val root : AnchorPane by fxml()

    fun exit(){
        close()
    }
}
