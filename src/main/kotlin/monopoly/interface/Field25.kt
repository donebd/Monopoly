package monopoly.`interface`

import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.layout.AnchorPane
import monopoly.logic.Type
import tornadofx.Fragment
import tornadofx.c

class Field25 : Fragment(){

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
        costLabel.text = "${board.fields[25].cost}"
        costOfUpgrade.text = "${board.fields[25].upgradeCost}"
        changable()
    }

    private fun changable(){
        penaltyLabel.text = "${board.fields[25].penalty}"
        countUpgrade.text = "${board.fields[25].upgrade}"
        if (data[gamePlay.presentId].realty.filter { it.type == Type.Car }.size == 3){
            upgradeButton.disableProperty().value = (board.fields[25].upgrade > 4 ||
                    data[gamePlay.presentId].currentMotionUpgrade.contains(Type.Car))
            //check for count upgrade
            sellUpgradeButton.disableProperty().value = board.fields[25].upgrade == 0
        }
        sellByHalfButton.disableProperty().value = board.fields[25].upgrade != 0
    }

    fun sellByHalf(){
        data[gamePlay.presentId].moneyChange(board.fields[25].cost/2)
        data[gamePlay.presentId].realty.remove( board.fields[25])
        board.fields[25].owner = null
        board.fields[25].upgrade = 0
        board.fields[25].penaltyUpdate()
        gamePlay.paintField(25, c("#d2edd7"))
        close()
    }

    fun buildUpgrade(){
        if (data[gamePlay.presentId].money >= board.fields[17].upgradeCost){
            data[gamePlay.presentId].moneyChange(-board.fields[17].upgradeCost)
            board.fields[25].upgrade++
            board.fields[25].penaltyUpdate()
            data[gamePlay.presentId].currentMotionUpgrade.add(Type.Car)
            changable()
            return
        }
        notEnoughMoney.opacity = 1.0
    }

    fun sellUpgrade(){
        data[gamePlay.presentId].moneyChange(board.fields[25].upgradeCost)
        board.fields[25].upgrade--
        board.fields[25].penaltyUpdate()
        changable()
    }
}