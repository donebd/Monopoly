package monopoly.`interface`

import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.layout.AnchorPane
import monopoly.logic.Type
import tornadofx.Fragment
import tornadofx.c

class Field22 : Fragment(){

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
        costLabel.text = "${board.fields[22].cost}"
        costOfUpgrade.text = "${board.fields[22].upgradeCost}"
        changable()
    }

    private fun changable(){
        penaltyLabel.text = "${board.fields[22].penalty}"
        countUpgrade.text = "${board.fields[22].upgrade}"
        if (data[gamePlay.presentId].realty.filter { it.type == Type.FastFood }.size == 2){
            upgradeButton.disableProperty().value = (board.fields[22].upgrade > 4 ||
                    data[gamePlay.presentId].currentMotionUpgrade.contains(Type.FastFood))
            //check for count upgrade
            sellUpgradeButton.disableProperty().value = board.fields[22].upgrade == 0
        }
    }

    fun sellByHalf(){
        data[gamePlay.presentId].moneyChange(board.fields[22].cost/2)
        data[gamePlay.presentId].realty.remove( board.fields[22])
        board.fields[22].owner = null
        board.fields[22].upgrade = 0
        board.fields[22].penaltyUpdate()
        gamePlay.paintField(22, c("#d2edd7"))
        close()
    }

    fun buildUpgrade(){
        if (data[gamePlay.presentId].money >= board.fields[22].upgradeCost){
            data[gamePlay.presentId].moneyChange(-board.fields[22].upgradeCost)
            board.fields[22].upgrade++
            board.fields[22].penaltyUpdate()
            data[gamePlay.presentId].currentMotionUpgrade.add(Type.FastFood)
            changable()
            return
        }
        notEnoughMoney.opacity = 1.0
    }

    fun sellUpgrade(){
        data[gamePlay.presentId].moneyChange(board.fields[22].upgradeCost)
        board.fields[22].upgrade--
        board.fields[22].penaltyUpdate()
        changable()
    }
}