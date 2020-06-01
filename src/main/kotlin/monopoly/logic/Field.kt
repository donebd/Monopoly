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
    val upgradeCost = cost / 2
    var penalty = cost
    var particular = false
    val penaltyProperty = SimpleIntegerProperty()
    var hasMonopoly = false

    init {
        penaltyUpdate()
    }

    fun penaltyUpdate(){
        penalty = if (hasMonopoly){
            if (!particular) (cost / 5) + (cost / 5)*upgrade*2
            else (cost / 5) + (cost / 5)*upgrade*3
        }else {
            if (owner != null) (cost / 10)
            else cost
        }
        penaltyProperty.value = penalty
    }

    fun monopolyChange(){
        hasMonopoly = !hasMonopoly
        penaltyUpdate()
    }

    fun costUpdate(a : Int){
        cost = a
        penaltyUpdate()
    }
}