package monopoly.`interface`

import javafx.scene.image.Image
import javafx.stage.Stage
import monopoly.logic.Game
import tornadofx.*

var data = mutableListOf<Game.Player>()

var board = Game.GameBoard()

var gamePlay = GamePlay()

var motionPlayer = 0

var loosers = mutableListOf<Int>()

class MyApp: App(Begin::class){
    override fun start(stage: Stage) {
        with(stage) {
        isResizable = false
    }
        stage.icons.add(Image("file:src/main/resources/monopoly/dice.png"))
        super.start(stage)
    }
}
