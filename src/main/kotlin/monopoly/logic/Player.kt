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
    val monopolyRealty = mutableListOf<Type>()
    var finishCircle = false
    var circlesCompleted = 0
    var ai = false
    var aiDifficulty : Difficulty? = null

    init {
        moneyChange(0)
    }

    override fun toString(): String {
        return "[Id = $id] [Name = $name] [Money = $money] [Ai = $ai] [Difficulty = $aiDifficulty]"
    }

    fun hasSomething() : Boolean{
        return realty.isNotEmpty()
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

    fun checkForMonopoly(field: Field) : Boolean {
        val monopolySize = when (field.location) {
            1, 2, 8, 9, 11, 13, 22, 24, 26, 27 -> 2
            else -> 3
        }
        if (realty.filter { it.type == field.type}.size == monopolySize){
            if (field.type !in monopolyRealty) {
                monopolyRealty.add(field.type)
                for (current in realty.filter { it.type == field.type})current.monopolyChange()
                }
                return true
        }
        if (field.type in monopolyRealty) {
            for (current in realty.filter { it.type == field.type && field.hasMonopoly})current.monopolyChange()
            field.monopolyChange()
            monopolyRealty.remove(field.type)
        }
        return false
    }
}