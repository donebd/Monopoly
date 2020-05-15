package monopoly.logic

import javafx.beans.property.SimpleIntegerProperty

class Game{

    class Player(val id: Int){
        var name = "Player$id"
        private var numberOfMoves = 0
        var position = 0
        var money = 15000
        val moneyProperty = SimpleIntegerProperty()
        var prisonDays = 0
        var doubleInARow = 0
        val realty = mutableListOf<GameBoard.Field>()

        init {
            moneyChange(0)
        }

        fun playerInPrison() : Boolean = prisonDays != 0

        fun moneyChange(a : Int){
            money += a
            moneyProperty.value = money
        }

        fun positionChange(a : Int){
            numberOfMoves += a
            position = numberOfMoves % 28
        }

        fun goToPrison(){
            if (position <= 7)
                positionChange(7-position)
            else positionChange(-(position - 7))
            prisonDays = 1
        }

        fun secretAction() : Pair<Boolean, SecretAction>{
            val answer1 = (1..2).random() == 1
            val answer2 = when((1..5).random()){
                1 -> SecretAction.Action1
                2 -> SecretAction.Action2
                3 -> SecretAction.Action3
                4 -> SecretAction.Action4
                else -> SecretAction.Action5
            }
            return Pair(answer1, answer2)
        }

    }

    class Dice{
        var count = 0
        var first = 0
        var second = 0
        var double = false

        fun roll() {
            first = (1..4).random()
            second = (1..4).random()
            count = second + first
            double = first - second == 0
            print("On the dice fell $count ")
            if (double) print(", double!")
            println()
        }
    }

    class GameBoard{
        val fields = mutableListOf<Field>()

        init {
            fields.addAll(initBoard(this).fields)
        }

        class Field(val location: Int, val type: Type){
            var couldBuy = true
            var layoutX = 0.0
            var layoutY = 0.0
            var owner : Player? = null
            var cost =  2000 + location*100
            var penalty = cost / 10

            fun costUpdate(a : Int){
                cost = a
                penalty = cost / 10
            }
        }
        }

    }


