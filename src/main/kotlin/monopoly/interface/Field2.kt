package monopoly.`interface`

import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.layout.AnchorPane
import monopoly.logic.Type
import tornadofx.Fragment
import tornadofx.c

class Field2 : Fragment(){

    override val root : AnchorPane by fxml()

    private val player : Label by fxid()
    private val penaltyLabel : Label by fxid()
    private val costLabel : Label by fxid()
    private val countUpgrade : Label by fxid()
    private val costOfUpgrade : Label by fxid()
    private val notEnoughMoney : Label by fxid()
    private val upgradeButton : Button by fxid()
    private val sellUpgradeButton : Button by fxid()

    init {
        player.text = data[gamePlay.presentId].name
        costLabel.text = "${board.fields[2].cost}"
        costOfUpgrade.text = "${board.fields[2].upgradeCost}"
        changable()
    }

    private fun changable(){
        penaltyLabel.text = "${board.fields[2].penalty}"
        countUpgrade.text = "${board.fields[2].upgrade}"
        if (data[gamePlay.presentId].realty.filter { it.type == Type.Perfume }.size == 2){
            upgradeButton.disableProperty().value = (board.fields[2].upgrade > 4 ||
                    data[gamePlay.presentId].currentMotionUpgrade.contains(Type.Perfume))
            //check for count upgrade
            sellUpgradeButton.disableProperty().value = board.fields[2].upgrade == 0
        }
    }

    fun sellByHalf(){
        data[gamePlay.presentId].moneyChange(board.fields[2].cost/2)
        data[gamePlay.presentId].realty.remove( board.fields[2])
        board.fields[2].owner = null
        board.fields[2].upgrade = 0
        board.fields[2].penaltyUpdate()
        gamePlay.paintField(2, c("#d2edd7"))
        close()
    }

    fun buildUpgrade(){
        if (data[gamePlay.presentId].money >= board.fields[2].upgradeCost){
            data[gamePlay.presentId].moneyChange(-board.fields[2].upgradeCost)
            board.fields[2].upgrade++
            board.fields[2].penaltyUpdate()
            data[gamePlay.presentId].currentMotionUpgrade.add(Type.Perfume)
            changable()
            return
        }
        notEnoughMoney.opacity = 1.0
    }

    fun sellUpgrade(){
        data[gamePlay.presentId].moneyChange(board.fields[2].upgradeCost)
        board.fields[2].upgrade--
        board.fields[2].penaltyUpdate()
        changable()
    }
}