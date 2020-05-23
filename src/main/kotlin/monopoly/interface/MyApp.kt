package monopoly.`interface`

import javafx.scene.image.Image
import javafx.stage.Stage
import monopoly.logic.Game
import tornadofx.*

var game = Game()

var gamePlay = GamePlay()

class MyApp: App(Begin::class){
    override fun start(stage: Stage) {
        with(stage) {
        isResizable = false
    }
        stage.icons.add(Image("monopoly/dice.png"))
        super.start(stage)
    }
}
