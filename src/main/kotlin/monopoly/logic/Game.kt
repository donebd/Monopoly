package monopoly.logic

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

    private var loosers = mutableListOf<Int>()

    var cntPls = 0 // count of players

    val dice = Dice()

    var playerClicked = 0 // for action with realty
    var click = 0

    var prisonByDouble = false

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
        cntPls = data.size
    }

    fun aiInstructions(){
        if (data[motionPlayer].monopolyRealty.isNotEmpty()){
            for (i in data[motionPlayer].monopolyRealty){
                board.fields.filter { it.type == i }.forEach{ if ( i !in data[motionPlayer].currentMotionUpgrade && data[motionPlayer].money >= it.upgradeCost && it.upgrade < 5 ){
                    data[motionPlayer].moneyChange(-it.upgradeCost)
                    it.upgrade++
                    it.penaltyUpdate()
                    data[motionPlayer].currentMotionUpgrade.add(i)
                } }
            }
        }//Блок апгрейда своих монополий by ai
    }

    fun aiBuyInstructions() : Boolean{
        if ((data[presentId].monopolyRealty.isEmpty() || data[presentId].money >= 10000) && data[presentId].money + 500 >= board.fields[data[presentId].position].cost)
            return true
        return false
    }

    fun aiPunisment() : Int{
        if (data[presentId].money >= board.fields[data[presentId].position].penalty) return 0
            if (data[presentId].monopolyRealty.isNotEmpty()){
                return 1
            }else{
                if (data[presentId].hasSomething()) return  2
            }
        return 3
    }

    fun sellSomeUpgrade(player : Player){
        for (i in player.monopolyRealty){
            for (j in board.fields.filter { it.type == i })
                if (j.upgrade > 0){
                    player.moneyChange(j.upgradeCost)
                    j.upgrade--
                    j.penaltyUpdate()
                    return
                }
        }
    }

    fun sellSomeField(player: Player) : Int{
        for (i in player.realty){
            if (player.realty.filter { it.type == i.type }.size == 1)
                return i.location
        }
        for (i in player.realty){
            if (player.realty.filter { it.type == i.type }.size == 2)
                return i.location
        }
        return 0
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

    fun checkPrisonByDouble() : Boolean{
        if ((dice.double && data[presentId].doubleInARow == 2)){
            prisonByDouble = true
            return true
        }
        prisonByDouble = false
        return false
    }

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

    fun stonksAction() : Boolean{
        if (ifStonks()){
            data[presentId].moneyChange(3000)
            return true
        }
        return false
    }

    fun ifStonks() = board.fields[data[presentId].position].type == Type.Stonks

    fun ifSecret() = board.fields[data[presentId].position].type == Type.Secret

    fun startAction() : Boolean{
        if(ifStart()){
            data[presentId].moneyChange(1000)
            return true
        }
        return false
    }

    fun ifStart() : Boolean = board.fields[data[presentId].position].type == Type.Start

    fun playerAcceptBuyRealty() : Boolean{
        if (data[presentId].money >= board.fields[data[presentId].position].cost){
            data[presentId].moneyChange(-board.fields[data[presentId].position].cost)
            data[presentId].realty.add(board.fields[data[presentId].position])
            board.fields[data[presentId].position].owner = data[presentId]
            data[presentId].checkForMonopoly(board.fields[data[presentId].position])
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

    fun positiveSecret(){
        when(dice.secret.second){
            SecretAction.Action1 -> data[presentId].moneyChange(250)
            SecretAction.Action2 -> data[presentId].moneyChange(500)
            SecretAction.Action3 -> data[presentId].moneyChange(300)
            SecretAction.Action4 -> data[presentId].moneyChange(750)
            else -> data[presentId].moneyChange(100)
        }
    }

    fun fieldClear(i : Field){
        board.fields[i.location].owner = null
        board.fields[i.location].upgrade = 0
        if(board.fields[i.location].hasMonopoly) board.fields[i.location].monopolyChange()
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

    //fieldAction
    var position = 1

    private var monopolySize = 2

    private var type = Type.Perfume

    fun fieldActionInit(){
        position = click
        monopolySize = when(position){
            1,2,8,9,11,13,22,24,26,27 -> 2
            else -> 3
        }
        type = when(position){
            1,2 -> Type.Perfume
            3,5,6 -> Type.Clothes
            8,9 -> Type.SocialNetwork
            11,13 -> Type.Soda
            15,16,19 -> Type.Airlanes
            22,24 -> Type.FastFood
            26,27 -> Type.Software
            else -> Type.Car
        }
    }

    fun playerHasMonopoly() = data[playerClicked].realty.filter { it.type == type}.size == monopolySize

    fun fieldCantBeUpgraded() = board.fields[position].upgrade > 4 || data[playerClicked].currentMotionUpgrade.contains(type)

    fun fieldCantBeSelled() = board.fields[position].upgrade == 0

    fun fieldSellByHalf(){
        data[playerClicked].moneyChange(board.fields[position].cost/2)
        data[playerClicked].realty.remove(board.fields[position])
        board.fields[position].owner = null
        board.fields[position].upgrade = 0
        data[playerClicked].checkForMonopoly(board.fields[position])
        board.fields[position].penaltyUpdate()
    }

    fun fieldSellByHalf(player: Player, field: Field){
        player.moneyChange(field.cost/2)
        player.realty.remove(field)
        field.owner = null
        field.upgrade = 0
        player.checkForMonopoly(field)
        field.penaltyUpdate()
    }

    fun fieldBuildUpgrade() : Boolean{
        if (data[playerClicked].money >= board.fields[position].upgradeCost){
            data[playerClicked].moneyChange(-board.fields[position].upgradeCost)
            board.fields[position].upgrade++
            board.fields[position].penaltyUpdate()
            data[playerClicked].currentMotionUpgrade.add(type)
            return true
        }
        return false
    }

    fun fieldSellUpgrade(){
        data[playerClicked].moneyChange(board.fields[position].upgradeCost)
        board.fields[position].upgrade--
        board.fields[position].penaltyUpdate()
    }

    }


