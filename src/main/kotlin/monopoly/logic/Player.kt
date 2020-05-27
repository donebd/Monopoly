package monopoly.logic

import javafx.beans.property.SimpleIntegerProperty

class Player(val id: Int){
    var name = "Player$id"
    var numberOfMoves = 0
    var position = 0
    var money = 15000
    val moneyProperty = SimpleIntegerProperty()
    var prisonDays = 0
    var doubleInARow = 0
    val realty = mutableListOf<Field>()
    val currentMotionUpgrade = mutableListOf<Type>()//monitoring 1 upgrade of one of type realty in motion
    var finishCircle = false
    var circlesCompleted = 0

    init {
        moneyChange(0)
    }

    fun playerInPrison() : Boolean = prisonDays != 0

    fun moneyChange(a : Int){
        money += a
        moneyProperty.value = money
    }

    fun positionChange(a : Int){
        finishCircle = position + a > 27
        if (finishCircle) circlesCompleted ++
        numberOfMoves += a
        position = numberOfMoves % 28
    }

    fun goToPrison(){
        if (position <= 7)
            positionChange(7-position)
        else positionChange(-(position - 7))
        prisonDays = 1
        doubleInARow = 0
    }

}