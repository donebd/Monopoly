package monopoly.`interface`

import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.layout.AnchorPane
import monopoly.logic.Type
import tornadofx.Fragment
import tornadofx.c
import tornadofx.imageview

class Field : Fragment() {

    override val root: AnchorPane by fxml()

    private val player: Label by fxid()
    private val penaltyLabel: Label by fxid()
    private val costLabel: Label by fxid()
    private val countUpgrade: Label by fxid()
    private val costOfUpgrade: Label by fxid()
    private val notEnoughMoney: Label by fxid()
    private val upgradeButton: Button by fxid()
    private val sellUpgradeButton: Button by fxid()
    private val sellByHalfButton: Button by fxid()
    private val companyName: Label by fxid()
    private val typeOfField: Label by fxid()

    private val act = game.currentAct

    init {
        companyName.text = act.fieldClicked.name
        typeOfField.text = act.fieldClicked.type.toString()
        val image = imageview("monopoly/fields/${act.fieldClicked.name}.png")
        when (act.fieldClicked.location) {
            in 1..6 -> {
                image.layoutX = 50.0
                image.rotate = -90.0
            }
            8,9,10,11,13,22,24,25,26,27 -> {
                image.layoutX = 25.0
                image.layoutY = 25.0
            }
            in 15..19 -> {
                image.layoutX = 50.0
                if (act.fieldClicked.location == 16) image.layoutX = 80.0
                image.rotate = 90.0
            }
        }
        with(root) {this.add(image)}
        player.text = act.playerClicked.name
        costLabel.text = "${act.fieldClicked.cost}"
        costOfUpgrade.text = "${act.fieldClicked.upgradeCost}"
        changable()
    }


    private fun changable() {
        penaltyLabel.text = "${act.fieldClicked.penalty}"
        countUpgrade.text = "${act.fieldClicked.upgrade}"
        if (act.playerClicked.hasMonopoly(act.fieldClicked)) {
            upgradeButton.disableProperty().value = act.fieldCantBeUpgraded()
            //check for count upgrade
            sellUpgradeButton.disableProperty().value = act.fieldCantBeSelled()
        }
        sellByHalfButton.disableProperty().value = !act.fieldCantBeSelled()
    }

    fun sellByHalf() {
        act.fieldSellByHalf()
        close()
    }

    fun buildUpgrade() {
        if (act.fieldBuildUpgrade()) {
            gamePlay.sendln(act.playerClicked.name + " строит филиал. Количество филиалов на поле " + act.fieldClicked.name + " - " + act.fieldClicked.upgrade)
            changable()
            return
        }
        notEnoughMoney.opacity = 1.0
    }

    fun sellUpgrade() {
        act.fieldSellUpgrade()
        gamePlay.sendln(act.playerClicked.name + " продает филиал. Количество филиалов на поле " + act.fieldClicked.name + " - " + act.fieldClicked.upgrade)
        changable()
    }
}