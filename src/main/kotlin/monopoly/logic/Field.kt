package monopoly.logic

import javafx.beans.property.SimpleIntegerProperty
import javafx.beans.property.SimpleStringProperty

class Field(val location: Int, val type: Type) {
    var name = "SomeField"
    val nameProperty = SimpleStringProperty(name)
    var upgrade = 0
    var couldBuy = true
    var layoutX = 0.0
    var layoutY = 0.0
    var owner: Player? = null
    var cost = 2000 + location * 100
    val costProperty = SimpleIntegerProperty(cost)
    val upgradeCost = cost / 2
    var penalty = cost
    var particular = false
    val penaltyProperty = SimpleIntegerProperty()
    var hasMonopoly = false

    init {
        penaltyUpdate()
    }

    fun nameUpdate(name: String) {
        this.name = name
        nameProperty.value = name
    }

    fun penaltyUpdate() {
        penalty = if (hasMonopoly) {
            if (!particular) (cost / 3) + (cost / 6) * upgrade
            else (cost / 3) + (cost / 6) * upgrade * 2
        } else {
            if (owner != null) (cost / 6)
            else cost
        }
        penaltyProperty.value = penalty
    }

    fun monopolyChange() {
        hasMonopoly = !hasMonopoly
        penaltyUpdate()
    }

    fun costUpdate(a: Int) {
        cost = a
        penaltyUpdate()
    }
}