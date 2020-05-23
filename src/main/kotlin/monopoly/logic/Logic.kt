package monopoly.logic

import javafx.beans.property.SimpleIntegerProperty

class Game{

    val player1Default = Pair(10.0,30.0)
    val player2Default = Pair(10.0,40.0)
    val player3Default = Pair(30.0,30.0)
    val player4Default = Pair(30.0,40.0)
    val player5Default = Pair(10.0,50.0)
    val prisonPosition = Pair(810.0,50.0)

    val data = mutableListOf<Player>()

    val board = GameBoard()

    var motionPlayer = 0

    var presentId = 0 // number of the player who is currently walking in action

    var loosers = mutableListOf<Int>()

    var cntPls = 0 // count of players

    val dice = Dice()

    var playerClicked = 0 // for action with realty
    var click = 0

    fun canControl(number : Int) : Boolean{
        if (board.fields[number].owner == data[presentId]){
            click = number
            playerClicked = presentId
            return true
        }
        return false
    }

    init {
        data.add(Player(1))
        data.add(Player(2))
    }

    fun setBalance(){
        if (data.size == 2){
            for (i in 0..1)data[i].moneyChange(10000)
        }

        if (data.size == 3){
            for (i in 0..2)data[i].moneyChange(5000)
        }
    }

    //settings reward
    fun checkCircleComplete() : Boolean{
        if ((data[presentId].finishCircle && data[presentId].circlesCompleted < 5)||
            (data[presentId].finishCircle && data[presentId].circlesCompleted < 10 && cntPls < 4)){
            data[presentId].moneyChange(2000)
            return true
        }
        return false
    }

    fun checkPrisonByDouble() = (dice.double && data[presentId].doubleInARow == 2)

    fun motionIfPrison(current: Player){
        current.goToPrison()
        if (dice.double){
            motionPlayer++
            motionPlayer %= cntPls
            dice.double = false
        }
    }

    fun motionNext(){
        if (!dice.double) {
            motionPlayer ++
        motionPlayer %= cntPls
        }
    }

    fun endMotionLogic(){
        data[presentId].currentMotionUpgrade.clear()
        while (motionPlayer in loosers) {
            motionPlayer++
            motionPlayer %= cntPls
        }
        presentId = motionPlayer
    }

    fun diceDoubleCheck() : Boolean {
        if (dice.double) {
            data[presentId].doubleInARow ++
            return true
        }
        data[presentId].doubleInARow = 0
        return false
    }

    fun realtyCanBuy() = board.fields[data[presentId].position].couldBuy && board.fields[data[presentId].position].owner == null

    fun punishmentOrPenalty() = ifPunishment() ||
            (board.fields[data[presentId].position].owner != null && board.fields[data[presentId].position].owner!!.id != data[presentId].id)
    fun ifPunishment() = board.fields[data[presentId].position].type == Type.Punisment

    fun ifToPrison() = board.fields[data[presentId].position].type == Type.ToPrison

    fun ifStonks() : Boolean{
        if(board.fields[data[presentId].position].type == Type.Stonks){
            data[presentId].moneyChange(3000)
            return true
        }
        return false
    }

    fun ifSecret() = board.fields[data[presentId].position].type == Type.Secret

    fun ifStart() : Boolean{
        if(board.fields[data[presentId].position].type == Type.Start){
            data[presentId].moneyChange(1000)
            return true
        }
        return false
    }

    fun playerAcceptBuyRealty() : Boolean{
        if (data[presentId].money >= board.fields[data[presentId].position].cost){
            data[presentId].moneyChange(-board.fields[data[presentId].position].cost)
            data[presentId].realty.add(board.fields[data[presentId].position])
            board.fields[data[presentId].position].owner = data[presentId]
            board.fields[data[presentId].position].penaltyUpdate()
            return true
        }
        return false
    }

    fun playerPayPenalty() : Boolean{
        if (data[presentId].money >= board.fields[data[presentId].position].penalty) {
            data[presentId].moneyChange(-board.fields[data[presentId].position].penalty)
            if (board.fields[data[presentId].position].type != Type.Punisment) {
                board.fields[data[presentId].position].owner!!.moneyChange(board.fields[data[presentId].position].penalty)
            }
            return true
        }
        return false
    }

    fun playerLose(){
        loosers.add(presentId)
    }

    fun fieldClear(i : GameBoard.Field){
        board.fields[i.location].owner = null
        board.fields[i.location].upgrade = 0
        board.fields[i.location].penaltyUpdate()
    }

    fun gameIsEnd() = cntPls - loosers.size == 1

    fun secretIsPositive() : Boolean{
        dice.secretAction()
        if (dice.secret.first) return true
        return false
    }

    fun negativePay() : Boolean{
        when(dice.secret.second){
            SecretAction.Action1 -> if (data[presentId].money >= 300){
                data[presentId].moneyChange(-300)
                return true
            }
            SecretAction.Action2 -> if (data[presentId].money >= 500){
                data[presentId].moneyChange(-500)
                return true
            }
            SecretAction.Action3 -> if (data[presentId].money >= 40){
                data[presentId].moneyChange(-40)
                return true
            }
            SecretAction.Action4 -> if (data[presentId].money >= 750){
                data[presentId].moneyChange(-750)
                return true
            }
            else -> if (data[presentId].money >= 250){
                data[presentId].moneyChange(-250)
                return true
            }
        }
        return false
    }

    fun prisonPay() : Boolean{
        if(data[presentId].prisonDays == 4 && data[presentId].money >= 750){
            data[presentId].prisonDays = 0
            data[presentId].moneyChange(-750)
            return true
        }
        if (data[presentId].prisonDays != 4 && data[presentId].money >= 500){
            data[presentId].prisonDays = 0
            data[presentId].moneyChange(-500)
            return true
        }
        return false
    }

    fun prisonPayDay() = data[presentId].prisonDays == 4

    fun prisonTry() : Boolean{
        if (dice.double) {
            data[presentId].prisonDays = 0
            dice.double = false
            return true
        }
        motionPlayer++
        motionPlayer %= cntPls
        data[presentId].prisonDays ++
        return false
    }

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
            initBoard(this)
        }

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
        }

    }


