package monopoly.`interface`

import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.layout.AnchorPane
import monopoly.logic.Type
import tornadofx.Fragment
import tornadofx.c

class Field27 : Fragment(){

    override val root : AnchorPane by fxml()

    private val player : Label by fxid()
    private val penaltyLabel : Label by fxid()
    private val costLabel : Label by fxid()
    private val countUpgrade : Label by fxid()
    private val costOfUpgrade : Label by fxid()
    private val notEnoughMoney : Label by fxid()
    private val upgradeButton : Button by fxid()
    private val sellUpgradeButton : Button by fxid()
    private val sellByHalfButton : Button by fxid()

    init {
        player.text = data[gamePlay.presentId].name
        costLabel.text = "${board.fields[27].cost}"
        costOfUpgrade.text = "${board.fields[27].upgradeCost}"
        changable()
    }

    private fun changable(){
        penaltyLabel.text = "${board.fields[27].penalty}"
        countUpgrade.text = "${board.fields[27].upgrade}"
        if (data[gamePlay.presentId].realty.filter { it.type == Type.Software }.size == 2){
            upgradeButton.disableProperty().value = (board.fields[27].upgrade > 4 ||
                    data[gamePlay.presentId].currentMotionUpgrade.contains(Type.Software))
            //check for count upgrade
            sellUpgradeButton.disableProperty().value = board.fields[27].upgrade == 0
        }
        sellByHalfButton.disableProperty().value = board.fields[27].upgrade != 0
    }

    fun sellByHalf(){
        data[gamePlay.presentId].moneyChange(board.fields[27].cost/2)
        data[gamePlay.presentId].realty.remove( board.fields[27])
        board.fields[27].owner = null
        board.fields[27].upgrade = 0
        board.fields[27].penaltyUpdate()
        gamePlay.paintField(27, c("#d2edd7"))
        close()
    }

    fun buildUpgrade(){
        if (data[gamePlay.presentId].money >= board.fields[27].upgradeCost){
            data[gamePlay.presentId].moneyChange(-board.fields[27].upgradeCost)
            board.fields[27].upgrade++
            board.fields[27].penaltyUpdate()
            data[gamePlay.presentId].currentMotionUpgrade.add(Type.Software)
            changable()
            return
        }
        notEnoughMoney.opacity = 1.0
    }

    fun sellUpgrade(){
        data[gamePlay.presentId].moneyChange(board.fields[27].upgradeCost)
        board.fields[27].upgrade--
        board.fields[27].penaltyUpdate()
        changable()
    }
}