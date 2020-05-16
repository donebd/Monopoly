package monopoly.`interface`

import javafx.scene.control.Label
import javafx.scene.layout.AnchorPane
import tornadofx.Fragment

class CycleComplete : Fragment(){

    override val root : AnchorPane by fxml()

    private val player : Label by fxid()

    init {
        player.text = data[gamePlay.presentId].name
        data[gamePlay.presentId].moneyChange(2000)
    }

    fun exit(){
        close()
    }
}