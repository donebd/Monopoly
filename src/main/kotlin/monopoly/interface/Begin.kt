package monopoly.`interface`

import javafx.scene.control.CheckBox
import javafx.scene.control.TextField
import javafx.scene.layout.BorderPane
import monopoly.logic.Game
import monopoly.logic.Player
import tornadofx.*

class Begin : View("Monopoly"){

    override val root : BorderPane by fxml()

    private val playfield1 : TextField by fxid()

    private val playfield2 : TextField by fxid()

    private val playfield3 : TextField by fxid()

    private val playfield4 : TextField by fxid()

    private val playfield5 : TextField by fxid()

    private val botCheck1 : CheckBox by fxid()

    private val botCheck2 : CheckBox by fxid()

    private val botCheck3 : CheckBox by fxid()

    private val botCheck4 : CheckBox by fxid()

    private val botCheck5 : CheckBox by fxid()

    private val player3Check : CheckBox by fxid()

    private val player4Check : CheckBox by fxid()

    private val player5Check : CheckBox by fxid()

    fun howToPlay(){
        find<HowToPlay>().openModal(resizable =  false)
    }

    fun active3(){
        if (player3Check.isSelected) {
            playfield3.disableProperty().value = false
            playfield3.text = "Player 3"
            botCheck3.disableProperty().value = false
        }
        else {
            playfield3.disableProperty().value = true
            playfield3.text = ""
            botCheck3.disableProperty().value = true
            botCheck3.isSelected = false
        }
    }

    fun active4(){
        if (player4Check.isSelected) {
            playfield4.disableProperty().value = false
            playfield4.text = "Player 4"
            botCheck4.disableProperty().value = false
        }
        else {
            playfield4.disableProperty().value = true
            playfield4.text = ""
            botCheck4.disableProperty().value = true
            botCheck4.isSelected = false
        }
    }

    fun active5(){
        if (player5Check.isSelected) {
            playfield5.disableProperty().value = false
            playfield5.text = "Player 5"
            botCheck5.disableProperty().value = false
        }
        else {
            playfield5.disableProperty().value = true
            playfield5.text = ""
            botCheck5.disableProperty().value = true
            botCheck5.isSelected = false
        }
    }

    fun botAction1(){
        if (botCheck1.isSelected){
            playfield1.disableProperty().value = true
            playfield1.text = "AI 1"
        } else {
            playfield1.disableProperty().value = false
            playfield1.text = "Player 1"
        }
    }
    fun botAction2(){
        if (botCheck2.isSelected){
            playfield2.disableProperty().value = true
            playfield2.text = "AI 2"
        } else {
            playfield2.disableProperty().value = false
            playfield2.text = "Player 2"
        }
    }

    fun botAction3(){
        if (botCheck3.isSelected){
            playfield3.disableProperty().value = true
            playfield3.text = "AI 3"
        } else {
            playfield3.disableProperty().value = false
            playfield3.text = "Player 3"
        }
    }

    fun botAction4(){
        if (botCheck4.isSelected){
            playfield4.disableProperty().value = true
            playfield4.text = "AI 4"
        } else {
            playfield4.disableProperty().value = false
            playfield4.text = "Player 4"
        }
    }

    fun botAction5(){
        if (botCheck5.isSelected){
            playfield5.disableProperty().value = true
            playfield5.text = "AI 5"
        } else {
            playfield5.disableProperty().value = false
            playfield5.text = "Player 5"
        }
    }

    init {
        primaryStage.width = 610.0
        primaryStage.height = 420.0
        primaryStage.centerOnScreen()
    }

    fun startGame(){
        game = Game()

        game.data[0].name = playfield1.text
        game.data[1].name = playfield2.text

        if (botCheck1.isSelected) game.data[0].ai = true

        if (botCheck2.isSelected) game.data[1].ai = true

        if (player3Check.isSelected){
            game.data.add(Player(game.data.size + 1))
            game.data.last().name = playfield3.text
            if (botCheck3.isSelected) game.data.last().ai = true
        }

        if (player4Check.isSelected){
            game.data.add(Player(game.data.size + 1))
            game.data.last().name = playfield4.text
            if (botCheck4.isSelected) game.data.last().ai = true
        }

        if (player5Check.isSelected){
            game.data.add(Player(game.data.size + 1))
            game.data.last().name = playfield5.text
            if (botCheck5.isSelected) game.data.last().ai = true
        }
        gamePlay.root.clear()
        gamePlay = GamePlay()
        replaceWith(gamePlay, ViewTransition.Explode(0.5.seconds))
    }

}