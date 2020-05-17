package monopoly.logic

import javafx.beans.property.SimpleIntegerProperty

class Game{

    class Player(val id: Int){
        var name = "Player$id"
        var numberOfMoves = 0
        var position = 0
        var money = 15000
        val moneyProperty = SimpleIntegerProperty()
        var prisonDays = 0
        var doubleInARow = 0
        val realty = mutableListOf<GameBoard.Field>()
        val currentMotionUpgrade = mutableListOf<Type>()//monitoring 1 upgrade of one of type realty in motion

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

    }

    class Dice{
        var count = 0
        var first = 0
        var second = 0
        var double = false
        var secret = Pair(true,SecretAction.Action1)

        init {
            secretAction()
        }

        fun roll() {
            first = (1..4).random()
            second = (1..4).random()
            count = second + first
            double = first - second == 0
            print("On the dice fell $count ")
            if (double) print(", double!")
            println()
        }

        fun secretAction() {
            val answer1 = (1..3).random() == 1
            val answer2 = when((1..5).random()){
                1 -> SecretAction.Action1
                2 -> SecretAction.Action2
                3 -> SecretAction.Action3
                4 -> SecretAction.Action4
                else -> SecretAction.Action5
            }
            secret = Pair(answer1, answer2)
        }
    }

    class GameBoard{
        val fields = mutableListOf<Field>()

        init {
            fields.addAll(initBoard(this).fields)
        }

        class Field(val location: Int, val type: Type){
            var upgrade = 0
            var couldBuy = true
            var layoutX = 0.0
            var layoutY = 0.0
            var owner : Player? = null
            var cost =  2000 + location*100
            val upgradeCost = cost / 3
            var penalty = cost
            val penaltyProperty = SimpleIntegerProperty()

            init {
                penaltyUpdate()
            }

            fun penaltyUpdate(){
                penalty = if (owner != null) (cost / 10) + (cost / 10)*upgrade*3
                else cost
                penaltyProperty.value = penalty
            }

            fun costUpdate(a : Int){
                cost = a
                penaltyUpdate()
            }
        }
        }

    }


