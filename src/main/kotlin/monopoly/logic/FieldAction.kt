package monopoly.logic

class FieldAction() {

    lateinit var playerClicked : Player
    lateinit var fieldClicked : Field
    lateinit var type : Type

    constructor(owner : Player, field : Field) : this(){
        playerClicked = owner
        fieldClicked = field
        type = field.type
    }

    fun fieldCantBeUpgraded() = fieldClicked.upgrade > 4 || playerClicked.currentMotionUpgrade.contains(type)

    fun fieldCantBeSelled() = fieldClicked.upgrade == 0

    fun fieldSellByHalf() {
        playerClicked.moneyChange(fieldClicked.cost / 2)
        playerClicked.realty.remove(fieldClicked)
        fieldClicked.ownerUpdate(null)
        fieldClicked.upgrade = 0
        playerClicked.checkForMonopoly(fieldClicked)
        fieldClicked.penaltyUpdate()
    }

    fun fieldBuildUpgrade(): Boolean {
        if (playerClicked.money >= fieldClicked.upgradeCost) {
            playerClicked.moneyChange(-fieldClicked.upgradeCost)
            fieldClicked.upgrade++
            fieldClicked.penaltyUpdate()
            playerClicked.currentMotionUpgrade.add(type)
            return true
        }
        return false
    }

    fun fieldSellUpgrade() {
        playerClicked.moneyChange(fieldClicked.upgradeCost)
        fieldClicked.upgrade--
        fieldClicked.penaltyUpdate()
    }

}