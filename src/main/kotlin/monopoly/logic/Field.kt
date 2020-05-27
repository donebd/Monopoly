package monopoly.logic

import javafx.beans.property.SimpleIntegerProperty

class Field(val location: Int, val type: Type){
    var name = "SomeField"
    var upgrade = 0
    var couldBuy = true
    var layoutX = 0.0
    var layoutY = 0.0
    var owner : Player? = null
    var cost =  2000 + location*100
    val upgradeCost = cost / 3
    var penalty = cost
    var particular = false
    val penaltyProperty = SimpleIntegerProperty()

    init {
        penaltyUpdate()
    }

    fun penaltyUpdate(){
        penalty = if (owner != null) {
            if (!particular) (cost / 10) + (cost / 10)*upgrade*3
            else (cost / 10) + (cost / 10)*upgrade*4
        }
        else cost
        penaltyProperty.value = penalty
    }

    fun costUpdate(a : Int){
        cost = a
        penaltyUpdate()
    }
}