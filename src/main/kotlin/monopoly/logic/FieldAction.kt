package monopoly.logic

class FieldAction() {

    constructor(owner : Player, field : Field) : this(){
        playerClicked = owner
        fieldClicked = field
    }

    var playerClicked = Player(228) // for action with realty
    var fieldClicked = Field(50, Type.Secret)
    var type = fieldClicked.type

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