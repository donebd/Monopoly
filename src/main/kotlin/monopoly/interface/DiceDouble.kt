package monopoly.`interface`

import javafx.scene.layout.AnchorPane
import tornadofx.Fragment

class DiceDouble : Fragment() {
    override val root: AnchorPane by fxml()

    fun exit() {
        close()
    }
}