package monopoly.`interface`

import javafx.scene.control.TextField
import javafx.scene.layout.BorderPane
import monopoly.logic.Game
import tornadofx.View
import tornadofx.ViewTransition
import tornadofx.clear
import tornadofx.seconds

class Begin : View("Monopoly"){

    override val root : BorderPane by fxml()

    private val playfield1 : TextField by fxid()

    private val playfield2 : TextField by fxid()

    private val playfield3 : TextField by fxid()

    private val playfield4 : TextField by fxid()

    private val playfield5 : TextField by fxid()

    fun howToPlay(){
        find<HowToPlay>().openModal(resizable =  false)
    }

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
        primaryStage.width = 610.0
        primaryStage.height = 420.0
        primaryStage.centerOnScreen()
    }

    fun startGame(){
        game = Game()

        game.data[0].name = playfield1.text
        game.data[1].name = playfield2.text

        if (playfield3.disableProperty().value == false){
            game.data.add(Game.Player(game.data.size + 1))
            game.data.last().name = playfield3.text
        }

        if (playfield4.disableProperty().value == false){
            game.data.add(Game.Player(game.data.size + 1))
            game.data.last().name = playfield4.text
        }

        if (playfield5.disableProperty().value == false){
            game.data.add(Game.Player(game.data.size + 1))
            game.data.last().name = playfield5.text
        }
        gamePlay.root.clear()
        gamePlay = GamePlay()
        replaceWith(gamePlay, ViewTransition.Explode(0.5.seconds))
    }

}