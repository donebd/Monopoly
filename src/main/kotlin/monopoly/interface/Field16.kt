package monopoly.`interface`

import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.layout.AnchorPane
import monopoly.logic.Type
import tornadofx.Fragment
import tornadofx.c

class Field16 : Fragment(){

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
        costLabel.text = "${board.fields[16].cost}"
        costOfUpgrade.text = "${board.fields[16].upgradeCost}"
        changable()
    }

    private fun changable(){
        penaltyLabel.text = "${board.fields[16].penalty}"
        countUpgrade.text = "${board.fields[16].upgrade}"
        if (data[gamePlay.presentId].realty.filter { it.type == Type.Airlanes }.size == 3){
            upgradeButton.disableProperty().value = (board.fields[16].upgrade > 4 ||
                    data[gamePlay.presentId].currentMotionUpgrade.contains(Type.Airlanes))
            //check for count upgrade
            sellUpgradeButton.disableProperty().value = board.fields[16].upgrade == 0
        }
    }

    fun sellByHalf(){
        data[gamePlay.presentId].moneyChange(board.fields[16].cost/2)
        data[gamePlay.presentId].realty.remove( board.fields[16])
        board.fields[16].owner = null
        board.fields[16].upgrade = 0
        board.fields[16].penaltyUpdate()
        gamePlay.paintField(16, c("#d2edd7"))
        close()
    }

    fun buildUpgrade(){
        if (data[gamePlay.presentId].money >= board.fields[16].upgradeCost){
            data[gamePlay.presentId].moneyChange(-board.fields[16].upgradeCost)
            board.fields[16].upgrade++
            board.fields[16].penaltyUpdate()
            data[gamePlay.presentId].currentMotionUpgrade.add(Type.Airlanes)
            changable()
            return
        }
        notEnoughMoney.opacity = 1.0
    }

    fun sellUpgrade(){
        data[gamePlay.presentId].moneyChange(board.fields[16].upgradeCost)
        board.fields[16].upgrade--
        board.fields[16].penaltyUpdate()
        changable()
    }
}