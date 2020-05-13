package monopoly.logic

import javafx.beans.property.SimpleIntegerProperty
import monopoly.`interface`.GamePlay


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

        fun moneyChange(a : Int){
            money += a
            moneyProperty.value = money
        }

        fun positionChange(a : Int){
            numberOfMoves += a
            position = numberOfMoves % 28
        }

        private fun goToPrison(){
            if (position <= 10)
                positionChange(10-position)
            else positionChange(-(position - 10))
            prisonDays = 1
        }

        fun move(prisonOut : Int,board: GameBoard){
            val dice = Dice()
            do {
                if (prisonOut == 0) dice.roll()
                if (dice.double) doubleInARow ++
                else doubleInARow = 0

                if ( doubleInARow == 3) break

                if (numberOfMoves % 28 > (numberOfMoves + dice.count + prisonOut) % 28){
                    moneyChange(2000)
                    message(111)
                }

                positionChange(dice.count + prisonOut)
                report(board)
            }while (dice.double )

            if (doubleInARow == 3) {
                goToPrison()
                message(228)
                doubleInARow -= 3
            }
            return
        }

        private fun report(board: GameBoard){
            when(board.fields[position].type){
                Type.Start -> {
                    moneyChange(1000)
                    message(123)
                }
                Type.Secret -> secretAction(board)
                Type.ToPrison -> {
                    goToPrison()
                    message(229)
                }
                Type.Punisment -> {
                    moneyChange(-2000)
                    message(322)
                }
                Type.Free -> message(12)
                else -> realtyAction(board)
            }
        }

        private fun message(code: Int){
            when(code){
                12 -> println("You took a walk in the Park")
                111 -> println("For passing the circle you get $2000")
                228 -> println("You're going to jail for cheating with dice")
                229 -> println("You're going to jail for tax evasion")
                322 -> println("You were robbed of $2000")
                123 -> println("For getting to the START field you get $1000")
                545 -> println("You found $750 on the road")
                546 -> println("You get a chance to roll the dice again")
                547 -> println("You went to a sale and spent $500 there")
                548 -> println("You won $1000 in the lottery")
                else -> println("abc")
            }
            println("Balance : $$money")
            readLine()
        }

        private fun realtyAction(board: GameBoard){

            if (board.fields[position].owner == null) {
                if ( money >= board.fields[position].cost){
                    println("Do you want to buy a property of this type: ${board.fields[position].type}, for $${board.fields[position].cost}? You have $${money} in your account")
                if (readLine() == "y"){
                    board.fields[position].owner = Player(id)
                    moneyChange(-board.fields[position].cost)
                    realty.add(board.fields[position])
                    return
                    }
                }
                println("The property will be put up for auction")

                return
            }

            if (board.fields[position].owner!!.id != id ){
                println("You must pay to the Player - ${board.fields[position].owner!!.name }, $${board.fields[position].penalty}")
                readLine()
                moneyChange(-board.fields[position].penalty)
                board.fields[position].owner!!.moneyChange(board.fields[position].penalty)
                println("You have $${money} in your account")
                return
            }
            println("You are in your own field and don't pay anything")
        }

        private fun secretAction(board: GameBoard){
            val secret = (545..548).random()
            when(secret){
                545 -> moneyChange(700)
                546 -> {
                    message(secret)
                    move(0, board)
                    return
                }
                547 -> moneyChange(-500)
                548 -> moneyChange(1000)
            }
            message(secret)
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

        class Field(location: Int, val type: Type){
            var couldBuy = true
            var layoutX = 0.0
            var layoutY = 0.0
            var owner : Player? = null
            var cost =  1000 + location*100
            var penalty = cost / 10

            fun costUpdate(a : Int){
                cost = a
                penalty = cost / 10
            }
        }
        }

    val board = GameBoard()

    fun startGame(data : MutableList<Player>){
        println("Game starting...")
        while (data.size > 1){
            data.map {motion(it) }
        }
    }

    fun motion(player: Player){
        println()
        println("${player.name} your turn:")
        val dice = Dice()
        if (player.prisonDays == 0) {
            player.move(0, board)
            return
        }

        println("You have to pay $500, or beat out a double. After ${3 - player.prisonDays} moves, you will be required to pay $600 if you do not get out of prison")
        print("To pay?(y/n)")
        if (readLine() == "y"){
            player.moneyChange(-500)
            player.prisonDays = 0
            player.move(0, board)
            return
        }

        dice.roll()
        if (dice.double) {
            player.prisonDays = 0
            player.doubleInARow++
            player.move(dice.count, board)
            return
        }

        player.prisonDays++
        if(player.prisonDays == 4){
            println("You pay $600 and get out of jail")
            player.prisonDays = 0
            player.moneyChange(-600)
            player.move(0, board)
        }

    }

    }


