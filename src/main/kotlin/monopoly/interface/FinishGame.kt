package monopoly.`interface`

import javafx.scene.control.Label
import javafx.scene.layout.AnchorPane
import tornadofx.Fragment

class FinishGame : Fragment() {

    override val root: AnchorPane by fxml()
    private val winner: Label by fxid()

    init {
        winner.text = game.data.first { game.data.indexOf(it) !in game.loosers }.name
        runAsync { Thread.sleep(100) } ui {
            gamePlay.buttonRoll.disableProperty().value = true
        }
    }

    fun newGame() {
        gamePlay.newGame()
        close()
    }

    fun exit() {
        gamePlay.close()
        close()
    }
}