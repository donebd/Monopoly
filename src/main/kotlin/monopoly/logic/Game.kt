package monopoly.logic

import monopoly.logic.CodeInstruction.*

class Game{

    val player1Default = Pair(10.0,30.0)
    val player2Default = Pair(10.0,40.0)
    val player3Default = Pair(30.0,30.0)
    val player4Default = Pair(30.0,40.0)
    val player5Default = Pair(10.0,50.0)
    val prisonPosition = Pair(810.0,50.0)

    val data = mutableListOf<Player>()

    var gameIsEnd = false

    val board = GameBoard()

    var motionPlayer = 0

    var presentId = 0 // number of the player who is currently walking in action

    var loosers = mutableListOf<Int>()

    var cntPls = 0 // count of players

    val dice = Dice()

    var offerSender = Player(0)
    var offerReceiver = Player(0)
    var offerSenderList = mutableListOf<Field>()
    var offerReceiverList = mutableListOf<Field>()
    var offerMoneySender = 0
    var offerMoneyReceiver = 0
    var offerPause = false

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

    fun canOffer(sender: Player, receiver: Player) : Boolean {
        offerSender = sender
        offerReceiver = receiver
        offerPause = sender.id != receiver.id && receiver.id !in loosers
        return offerPause
    }

    fun acceptOffer(){
        for (field in offerSenderList) {
            offerSender.realty.remove(field)
            field.owner = null
            field.upgrade = 0
            offerSender.checkForMonopoly(field)
            field.penaltyUpdate()
            offerReceiver.realty.add(field)
            field.owner = offerReceiver
            offerReceiver.checkForMonopoly(field)
        }// fields of sender to receiver

        for (field in offerReceiverList) {
            offerReceiver.realty.remove(field)
            field.owner = null
            field.upgrade = 0
            offerReceiver.checkForMonopoly(field)
            field.penaltyUpdate()
            offerSender.realty.add(field)
            field.owner = offerSender
            offerSender.checkForMonopoly(field)
        }

        offerSender.moneyChange(offerMoneyReceiver)
        offerSender.moneyChange(-offerMoneySender)
        offerReceiver.moneyChange(offerMoneySender)
        offerReceiver.moneyChange(-offerMoneyReceiver)
    }

    fun correctExchange(sendingFields : List<Field>, receiveringFields : List<Field>, moneySend : Int, moneyGet : Int) : Boolean {
        if (sendingFields.isEmpty() && receiveringFields.isEmpty()) return false
        var costSend = moneySend
        var costReceive = moneyGet
        for (field in sendingFields) {
            costSend += field.cost
        }
        for (field in receiveringFields) {
            costReceive += field.cost
        }
        if (costSend > costReceive*2 || 2*costSend < costReceive) return false
        return true
    }

    init {
        data.add(Player(1))
        data.add(Player(2))
        cntPls = data.size
    }

    fun aiInstructions(player: Player) : List<Int>{
        val tmp  =  mutableListOf<Int>()
        if (player.monopolyRealty.isNotEmpty() && (player.aiDifficulty == Difficulty.Hard || (player.aiDifficulty == Difficulty.Medium && (1..100).random() in (1..85))
                    || (player.aiDifficulty == Difficulty.Easy && (0..1).random() == 1))){
            for (i in player.monopolyRealty){
                board.fields.filter { it.type == i }.forEach{ if ( i !in player.currentMotionUpgrade && player.money >= it.upgradeCost && it.upgrade < 5 ){
                    player.moneyChange(-it.upgradeCost)
                    it.upgrade++
                    it.penaltyUpdate()
                    player.currentMotionUpgrade.add(i)
                    tmp.add(it.location)
                } }
            }
        }//Блок апгрейда своих монополий by ai
        return tmp
    }
    fun aiPrisonInstructions(player: Player) : CodeInstruction{
        when(player.aiDifficulty){
            Difficulty.Hard -> {
                if (!prisonPayDay(player)){
                    if ((player.money >= 10000 || (player.monopolyRealty.isEmpty() && playerNearlyHasSomeMonopoly(player))) && onBoardHasBuyableFields() && player.money >= 500) return Buy
                    return PrisonTry
                }else{
                    if (player.money >= 750) return Buy
                    if (player.monopolyRealty.isNotEmpty() && sellSomeUpgrade(player,false) != -1) return SellUpgrade
                    if (player.hasSomething()) return SellField
                }
            }
            else -> {
                if (!prisonPayDay(player)){
                    if (player.aiDifficulty == Difficulty.Easy && player.money >= 500 && (0..1).random() == 1) return Buy
                    if (player.aiDifficulty == Difficulty.Medium && player.money >= 500 && player.monopolyRealty.isEmpty() && onBoardHasBuyableFields()) return Buy
                    return PrisonTry
                }else{
                    if (player.money >= 750) return Buy
                    if (player.hasSomeNotMonopoly()) return SellNotMonopoly
                    if (player.monopolyRealty.isNotEmpty() && sellSomeUpgrade(player,false) != -1) return SellUpgrade
                    if (player.hasSomething()) return SellField
                }
            }
        }
        return Surrender
    }

    private fun onBoardHasBuyableFields() : Boolean{
        return board.fields.any { it.couldBuy && it.owner == null }
    }

    fun aiBuyInstructions(player: Player) : CodeInstruction{
        when (player.aiDifficulty){
            Difficulty.Hard -> {
                if (player.money >= board.fields[player.position].cost + 500 && (player.monopolyRealty.isEmpty() || player.money >= 10000 || someOnBoardNearlyHasMonopoly()))
                    return Buy
                if (playerNearlyHasMonopoly(player, board.fields[player.position])
                    && calculateRealtyCost(player) + player.money >= board.fields[player.position].cost + 500
                    && player.monopolyRealty.isNotEmpty() && sellSomeUpgrade(player,false) != -1)
                    return SellUpgrade
                if (playerNearlyHasMonopoly(player, board.fields[player.position])
                    && calculateRealtyCost(player) + player.money >= board.fields[player.position].cost + 500
                    && player.realty.any { it.type != board.fields[player.position].type })
                    return SellField
            }
            Difficulty.Medium -> {
                if (player.money >= board.fields[player.position].cost && (player.monopolyRealty.isEmpty() || someOnBoardNearlyHasMonopoly()))
                    return  Buy

            }
            else -> if (player.money >= board.fields[player.position].cost) return Buy
        }

        return NotBuy// не купить
    }

    private fun calculateRealtyCost(player : Player) : Int{// возвращает кол-во денег с продажи всех немонопольных полей и апгрейдов
        var sum = 0
        for (i in player.monopolyRealty){
            for (j in board.fields.filter { it.type == i })
                if (j.upgrade > 0){
                   sum += j.upgrade * j.upgradeCost
                }
        }//стоимость всех апгрейдов

        for (i in player.realty.filter { it.type != board.fields[player.position].type && it.type !in player.monopolyRealty}){
            if (player.realty.filter { it.type == i.type }.size == 1)
                sum += i.cost/2
        }
        for (i in player.realty.filter { it.type != board.fields[player.position].type && it.type !in player.monopolyRealty}){
            if (player.realty.filter { it.type == i.type }.size == 2)
                sum += i.cost/2
        }
        return sum //стоимость всех немонопольных полей
    }

    private fun someOnBoardNearlyHasMonopoly() : Boolean{ // функция для проверки необходимости покупки поля для руина монополии другому игроку
        for (player in data)
            if (playerNearlyHasMonopoly(player, board.fields[data[presentId].position])) return true
        return false
    }

    private fun playerNearlyHasMonopoly(player: Player, field: Field) : Boolean{
        when(player.realty.filter { it.type == field.type }.size){
            1 -> if (field.type == Type.Perfume || field.type == Type.Software || field.type == Type.Soda || field.type == Type.FastFood || field.type == Type.SocialNetwork) return true
            2 -> if (field.type == Type.Clothes || field.type == Type.Car || field.type == Type.Airlanes) return true
            else -> return false
        }
        return false
    }

    private fun playerNearlyHasSomeMonopoly(player: Player) : Boolean{
        for (i in player.realty) if (playerNearlyHasMonopoly(player, i)) return true
        return false
    }

    fun aiNegativeEvent(player: Player) : CodeInstruction{
        when(dice.secret.second) {
            SecretAction.Action1 -> if (player.money >= 300) {
                return Buy
            }
            SecretAction.Action2 -> if (player.money >= 500) {
                return Buy
            }
            SecretAction.Action3 -> if (player.money >= 40) {
                return Buy
            }
            SecretAction.Action4 -> if (player.money >= 750) {
                return Buy
            }
            else -> if (player.money >= 250) {
                return Buy
            }
        }
        when(player.aiDifficulty){
            Difficulty.Hard, Difficulty.Medium -> {
                if (player.monopolyRealty.isNotEmpty() && sellSomeUpgrade(player,false) != -1){
                    return SellUpgrade
                }else{
                    if (player.hasSomething()) return  SellField
                }
            }
            else -> {
                if (player.hasSomeNotMonopoly()) return SellNotMonopoly
                if (player.monopolyRealty.isNotEmpty() && sellSomeUpgrade(player,false) != -1){
                    return SellUpgrade
                }else{
                    if (player.hasSomething()) return  SellField
                }
            }
        }

        return Surrender// сдаться
    }

    fun aiPunisment(player: Player) : CodeInstruction{
        if (player.money >= board.fields[player.position].penalty) return Buy
        when(player.aiDifficulty){
            Difficulty.Hard, Difficulty.Medium -> {
                if (player.monopolyRealty.isNotEmpty() && sellSomeUpgrade(player, false) != -1){
                    return SellUpgrade
                }else{
                    if (player.hasSomething()) return  SellField
                }
            }
            else -> {
                if (player.hasSomeNotMonopoly()) return SellNotMonopoly
                if (player.monopolyRealty.isNotEmpty() && sellSomeUpgrade(player,false) != -1){
                    return SellUpgrade
                }else{
                    if (player.hasSomething()) return  SellField
                }
            }
        }

        return Surrender
    }

    fun sellSomeUpgrade(player : Player, needSell : Boolean) : Int{
        for (i in player.monopolyRealty){
            for (j in board.fields.filter { it.type == i })
                if (j.upgrade > 0){
                    if (needSell){
                        player.moneyChange(j.upgradeCost)
                        j.upgrade--
                        j.penaltyUpdate()
                    }
                    return j.location
                }
        }
        return -1
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
        for (i in player.realty){
            if (player.realty.filter { it.type == i.type }.size == 3)
                return i.location
        }
        return 0 // никогда не выполняется, т.к. идет проверка до
    }

    fun sellSomeNotMonopolyField(player: Player) : Int{
        for (i in player.realty.filter { it.type !in player.monopolyRealty }){
            if (player.realty.filter { it.type == i.type }.size == 2)
                return i.location
        }
        for (i in player.realty.filter { it.type !in player.monopolyRealty }){
            if (player.realty.filter { it.type == i.type }.size == 1)
                return i.location
        }
        return 0 // никогда не выполняется, т.к. идет проверка до
    }

    fun sellSomeOtherTypeField(player: Player) : Int{
        for (i in player.realty.filter { it.type != board.fields[player.position].type && it.type !in player.monopolyRealty}){
            if (player.realty.filter { it.type == i.type }.size == 1)
                return i.location
        }
        for (i in player.realty.filter { it.type != board.fields[player.position].type && it.type !in player.monopolyRealty}){
            if (player.realty.filter { it.type == i.type }.size == 2)
                return i.location
        }
        return 0 // никогда не выполняется
    }

    fun setBalance(){
        if (data.size == 2){
            for (i in 0..1)data[i].moneyChange(10000)
        }

        if (data.size == 3){
            for (i in 0..2)data[i].moneyChange(1000)
        }

        if (data.size == 4){
            for (i in 0..2)data[i].moneyChange(-2000)
        }

        if (data.size == 5){
            for (i in 0..4)data[i].moneyChange(-5000)
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

    fun prisonPay() : Int{
        if(data[presentId].prisonDays == 4 && data[presentId].money >= 750){
            data[presentId].prisonDays = 0
            data[presentId].moneyChange(-750)
            data[presentId].justOutJail = true
            return 1
        }
        if (data[presentId].prisonDays != 4 && data[presentId].money >= 500){
            data[presentId].prisonDays = 0
            data[presentId].moneyChange(-500)
            data[presentId].justOutJail = true
            return 2
        }
        return -1
    }

    fun prisonPayDay() = data[presentId].prisonDays == 4
    private fun prisonPayDay(player: Player) = player.prisonDays == 4

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


