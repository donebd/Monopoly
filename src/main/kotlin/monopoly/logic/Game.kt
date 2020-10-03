package monopoly.logic


import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleIntegerProperty
import javafx.beans.property.SimpleStringProperty
import tornadofx.runAsync
import tornadofx.ui

class Game {

    val player1Default = Pair(10.0, 30.0)
    val player2Default = Pair(10.0, 40.0)
    val player3Default = Pair(30.0, 30.0)
    val player4Default = Pair(30.0, 40.0)
    val player5Default = Pair(10.0, 50.0)
    val prisonPosition = Pair(810.0, 50.0)

    var statsCntOfMotion = 0
    val statsVisitedField = IntArray(28)

    var delay = true

    val data = mutableListOf<Player>()

    var cntPls = 0 // count of players

    fun cntPlsUpdate() {
        cntPls = data.size
    }

    init {
        data.add(Player(1))
        data.add(Player(2))
        cntPls = data.size
    }

    //manipulate with view
    var gameIsEnd = false
    var gameWinner: Player? = null
    var endProperty = SimpleBooleanProperty(gameIsEnd)
    var prisonInitProperty = SimpleBooleanProperty()
    var penaltyInitProperty = SimpleBooleanProperty()
    var offerToBuyInitProperty = SimpleBooleanProperty()
    var negativeEventInitProperty = SimpleBooleanProperty()
    var positiveEventInitProperty = SimpleBooleanProperty()
    var diceDoubleProperty = SimpleBooleanProperty()
    var cycleCompleteProperty = SimpleBooleanProperty()
    var stonksActionProperty = SimpleBooleanProperty()
    var startActionProperty = SimpleBooleanProperty()
    var notifyInView = SimpleStringProperty()
    var updateUpgradeView = SimpleBooleanProperty()
    var viewEndMotionProperty = SimpleBooleanProperty()
    var toPrisonViewProperty = SimpleBooleanProperty()
    var surrenderViewProperty = SimpleBooleanProperty()
    var fieldBoughtProperty = SimpleBooleanProperty()
    var sellUpgradeViewProperty = SimpleBooleanProperty()
    var fieldDegradeProperty = SimpleIntegerProperty()
    var fieldSelledProperty = SimpleIntegerProperty()

    fun triggerProperty(property: SimpleBooleanProperty) {
        property.value = !property.value
    }

    private fun setGameStatus(status: Boolean) {
        gameIsEnd = !status
        gameWinner = data.filter { data.indexOf(it) !in loosers }.first()
        endProperty.value = gameIsEnd
    }

    val board = GameBoard()

    var motionPlayer = 0

    var currentPlayer = data.first() //the player who is currently walking in action

    var loosers = mutableListOf<Int>()

    val dice = Dice()

    var exchangeSender = Player(0)
    var exchangeReceiver = Player(0)
    var exchangeSenderList = mutableListOf<Field>()
    var exchangeReceiverList = mutableListOf<Field>()
    var exchangeMoneySender = 0
    var exchangeMoneyReceiver = 0
    var exchangePause = false

    var playerClicked = Player(228) // for action with realty
    var fieldClicked = Field(50, Type.Secret)

    var prisonByDouble = false

    fun canControl(number: Int): Boolean {
        if (board.fields[number].owner == currentPlayer && !currentPlayer.ai) {
            fieldClicked = board.fields[number]
            playerClicked = currentPlayer
            return true
        }
        return false
    }

    fun canExchange(sender: Player, receiver: Player): Boolean {
        if (sender.ai || currentPlayer.id != sender.id) return false
        exchangeSender = sender
        exchangeReceiver = receiver
        exchangePause = sender.id != receiver.id && receiver.id !in loosers
        return exchangePause
    }

    fun acceptExchange() {
        for (field in exchangeSenderList) {
            exchangeSender.realty.remove(field)
            field.owner = null
            field.upgrade = 0
            exchangeSender.checkForMonopoly(field)
            field.penaltyUpdate()
            exchangeReceiver.realty.add(field)
            field.owner = exchangeReceiver
            exchangeReceiver.checkForMonopoly(field)
            field.penaltyUpdate()
        }// fields of sender to receiver

        for (field in exchangeReceiverList) {
            exchangeReceiver.realty.remove(field)
            field.owner = null
            field.upgrade = 0
            exchangeReceiver.checkForMonopoly(field)
            field.penaltyUpdate()
            exchangeSender.realty.add(field)
            field.owner = exchangeSender
            exchangeSender.checkForMonopoly(field)
            field.penaltyUpdate()
        }

        exchangeSender.moneyChange(exchangeMoneyReceiver)
        exchangeSender.moneyChange(-exchangeMoneySender)
        exchangeReceiver.moneyChange(exchangeMoneySender)
        exchangeReceiver.moneyChange(-exchangeMoneyReceiver)
    }

    fun correctExchange(
        sendingFields: List<Field>,
        receiveringFields: List<Field>,
        moneySend: Int,
        moneyGet: Int
    ): Boolean {
        if (sendingFields.isEmpty() && receiveringFields.isEmpty()) return false
        var costSend = moneySend
        var costReceive = moneyGet
        for (field in sendingFields) {
            costSend += field.cost
        }
        for (field in receiveringFields) {
            costReceive += field.cost
        }
        if (costSend > costReceive * 2 || 2 * costSend < costReceive) return false
        return true
    }

    private fun aiInstructions(player: Player): List<Int> {
        val tmp = mutableListOf<Int>()
        if (player.monopolyRealty.isNotEmpty() && (player.aiDifficulty == Difficulty.Hard || (player.aiDifficulty == Difficulty.Medium && (1..100).random() in (1..85))
                    || (player.aiDifficulty == Difficulty.Easy && (0..1).random() == 1))
        ) {
            for (i in player.monopolyRealty) {
                board.fields.filter { it.type == i }.forEach {
                    if (i !in player.currentMotionUpgrade && player.money >= it.upgradeCost && it.upgrade < 5) {
                        player.moneyChange(-it.upgradeCost)
                        it.upgrade++
                        it.penaltyUpdate()
                        player.currentMotionUpgrade.add(i)
                        tmp.add(it.location)
                    }
                }
            }
        }//Блок апгрейда своих монополий by ai
        return tmp
    }

    private fun aiPrisonInstructions(player: Player) {
        when (player.aiDifficulty) {
            Difficulty.Hard -> {
                if (!prisonPayDay(player)) {
                    if ((player.money >= 10000 || (player.monopolyRealty.isEmpty() && playerNearlyHasSomeMonopoly(player))) && onBoardHasBuyableFields() && player.money >= 500) {
                        prisonPay()
                        return//buyOut
                    }
                    prisonTryLogic()
                    return//prisonTry
                } else {
                    if (player.money >= 750) {
                        prisonPay()
                        return//buyOut
                    }
                    if (player.monopolyRealty.isNotEmpty() && sellSomeUpgrade(player, false) != -1) {
                        val idDegradeField = sellSomeUpgrade(player, true)
                        fieldDegradeProperty.value = idDegradeField
                        triggerProperty(sellUpgradeViewProperty)
                        aiPrisonInstructions(player)
                        return//sellUpgrade
                    }
                    if (player.hasSomething()) {
                        val selledFieldId = sellSomeField(player)
                        fieldSellByHalf(player, board.fields[selledFieldId])
                        fieldSelledProperty.value = selledFieldId
                        aiPrisonInstructions(player)
                        return//sellField
                    }
                }
            }
            else -> {
                if (!prisonPayDay(player)) {
                    if (player.aiDifficulty == Difficulty.Easy && player.money >= 500 && (0..1).random() == 1) {
                        prisonPay()
                        return//buyOut
                    }
                    if (player.aiDifficulty == Difficulty.Medium && player.money >= 500 && player.monopolyRealty.isEmpty() && onBoardHasBuyableFields()) {
                        prisonPay()
                        return//buyOut
                    }
                    prisonTryLogic()
                    return//prisonTry
                } else {
                    if (player.money >= 750) {
                        prisonPay()
                        return//buyOut
                    }
                    if (player.hasSomeNotMonopoly()) {
                        val selledFieldId = sellSomeNotMonopolyField(player)
                        fieldSellByHalf(player, board.fields[selledFieldId])
                        fieldSelledProperty.value = selledFieldId
                        aiPrisonInstructions(player)
                        return//sellField
                    }
                    if (player.monopolyRealty.isNotEmpty() && sellSomeUpgrade(player, false) != -1) {
                        val idDegradeField = sellSomeUpgrade(player, true)
                        fieldDegradeProperty.value = idDegradeField
                        triggerProperty(sellUpgradeViewProperty)
                        aiPrisonInstructions(player)
                        return//sellUpgrade
                    }
                    if (player.hasSomething()) {
                        val selledFieldId = sellSomeField(player)
                        fieldSellByHalf(player, board.fields[selledFieldId])
                        fieldSelledProperty.value = selledFieldId
                        aiPrisonInstructions(player)
                        return//sellField
                    }
                }
            }
        }
        playerSurrender()
    }

    private fun onBoardHasBuyableFields(): Boolean {
        return board.fields.any { it.couldBuy && it.owner == null }
    }

    private fun aiBuyInstructions(player: Player) {
        when (player.aiDifficulty) {
            Difficulty.Hard -> {
                if (player.money >= board.fields[player.position].cost + 500 && (player.monopolyRealty.isEmpty() || player.money >= 10000 || someOnBoardNearlyHasMonopoly())) {
                    playerAcceptBuyRealty()
                    triggerProperty(fieldBoughtProperty)
                    return//buy
                }
                if (playerNearlyHasMonopoly(player, board.fields[player.position])
                    && calculateRealtyCost(player) + player.money >= board.fields[player.position].cost + 500
                    && player.monopolyRealty.isNotEmpty() && sellSomeUpgrade(player, false) != -1
                ) {
                    val idDegradeField = sellSomeUpgrade(player, true)
                    fieldDegradeProperty.value = idDegradeField
                    triggerProperty(sellUpgradeViewProperty)
                    aiBuyInstructions(player)
                    return//sellUpgrade
                }
                if (playerNearlyHasMonopoly(player, board.fields[player.position])
                    && calculateRealtyCost(player) + player.money >= board.fields[player.position].cost + 500
                    && player.realty.any { it.type != board.fields[player.position].type }
                ) {
                    val selledFieldId = sellSomeOtherTypeField(player)
                    fieldSellByHalf(player, board.fields[selledFieldId])
                    fieldSelledProperty.value = selledFieldId
                    aiBuyInstructions(player)
                    return//sellField
                }
            }
            Difficulty.Medium -> {
                if (player.money >= board.fields[player.position].cost && (player.monopolyRealty.isEmpty() || someOnBoardNearlyHasMonopoly())) {
                    playerAcceptBuyRealty()
                    triggerProperty(fieldBoughtProperty)
                    return//buy
                }

            }
            else -> if (player.money >= board.fields[player.position].cost) {
                playerAcceptBuyRealty()
                triggerProperty(fieldBoughtProperty)
                return//buy
            }
        }
        return//nothing
    }

    private fun calculateRealtyCost(player: Player): Int {// возвращает кол-во денег с продажи всех немонопольных полей и апгрейдов
        var sum = 0
        for (i in player.monopolyRealty) {
            for (j in board.fields.filter { it.type == i })
                if (j.upgrade > 0) {
                    sum += j.upgrade * j.upgradeCost
                }
        }//стоимость всех апгрейдов

        for (i in player.realty.filter { it.type != board.fields[player.position].type && it.type !in player.monopolyRealty }) {
            if (player.realty.filter { it.type == i.type }.size == 1)
                sum += i.cost / 2
        }
        for (i in player.realty.filter { it.type != board.fields[player.position].type && it.type !in player.monopolyRealty }) {
            if (player.realty.filter { it.type == i.type }.size == 2)
                sum += i.cost / 2
        }
        return sum //стоимость всех немонопольных полей
    }

    private fun someOnBoardNearlyHasMonopoly(): Boolean { // функция для проверки необходимости покупки поля для руина монополии другому игроку
        for (player in data)
            if (playerNearlyHasMonopoly(player, board.fields[currentPlayer.position])) return true
        return false
    }

    private fun playerNearlyHasMonopoly(player: Player, field: Field): Boolean {
        when (player.realty.filter { it.type == field.type }.size) {
            1 -> if (field.type == Type.Perfume || field.type == Type.Software || field.type == Type.Soda || field.type == Type.FastFood || field.type == Type.SocialNetwork) return true
            2 -> if (field.type == Type.Clothes || field.type == Type.Car || field.type == Type.Airlanes) return true
            else -> return false
        }
        return false
    }

    private fun playerNearlyHasSomeMonopoly(player: Player): Boolean {
        for (i in player.realty) if (playerNearlyHasMonopoly(player, i)) return true
        return false
    }

    private fun aiNegativeEvent(player: Player) {
        when (dice.secret.second) {
            SecretAction.Action1 -> if (player.money >= 300) {
                negativePay()
                endMotion()
                return//pay
            }
            SecretAction.Action2 -> if (player.money >= 500) {
                negativePay()
                endMotion()
                return//pay
            }
            SecretAction.Action3 -> if (player.money >= 40) {
                negativePay()
                endMotion()
                return//pay
            }
            SecretAction.Action4 -> if (player.money >= 750) {
                negativePay()
                endMotion()
                return//pay
            }
            else -> if (player.money >= 250) {
                negativePay()
                endMotion()
                return//pay
            }
        }
        when (player.aiDifficulty) {
            Difficulty.Hard, Difficulty.Medium -> {
                if (player.monopolyRealty.isNotEmpty() && sellSomeUpgrade(player, false) != -1) {
                    val idDegradeField = sellSomeUpgrade(player, true)
                    fieldDegradeProperty.value = idDegradeField
                    triggerProperty(sellUpgradeViewProperty)
                    aiNegativeEvent(player)
                    return//sellUpgrade
                } else {
                    if (player.hasSomething()) {
                        val selledFieldId = sellSomeField(player)
                        fieldSellByHalf(player, board.fields[selledFieldId])
                        fieldSelledProperty.value = selledFieldId
                        aiNegativeEvent(player)
                        return//sellField
                    }
                }
            }
            else -> {
                if (player.hasSomeNotMonopoly()) {
                    val selledFieldId = sellSomeNotMonopolyField(player)
                    fieldSellByHalf(player, board.fields[selledFieldId])
                    fieldSelledProperty.value = selledFieldId
                    aiNegativeEvent(player)
                    return//sellNotMonopolyField
                }
                if (player.monopolyRealty.isNotEmpty() && sellSomeUpgrade(player, false) != -1) {
                    val idDegradeField = sellSomeUpgrade(player, true)
                    fieldDegradeProperty.value = idDegradeField
                    triggerProperty(sellUpgradeViewProperty)
                    aiNegativeEvent(player)
                    return//sellUpgrade
                } else {
                    if (player.hasSomething()) {
                        val selledFieldId = sellSomeField(player)
                        fieldSellByHalf(player, board.fields[selledFieldId])
                        fieldSelledProperty.value = selledFieldId
                        aiNegativeEvent(player)
                        return//sellField
                    }
                }
            }
        }

        playerSurrender()
    }

    private fun aiPunisment(player: Player) {
        if (player.money >= board.fields[player.position].penalty) {
            playerPayPenalty()
            endMotion()
            return
        }
        when (player.aiDifficulty) {
            Difficulty.Hard, Difficulty.Medium -> {
                if (player.monopolyRealty.isNotEmpty() && sellSomeUpgrade(player, false) != -1) {
                    val idDegradeField = sellSomeUpgrade(player, true)
                    fieldDegradeProperty.value = idDegradeField
                    triggerProperty(sellUpgradeViewProperty)
                    aiPunisment(player)
                    return//sellUpgrade
                } else {
                    if (player.hasSomething()) {
                        val selledFieldId = sellSomeField(player)
                        fieldSellByHalf(player, board.fields[selledFieldId])
                        fieldSelledProperty.value = selledFieldId
                        aiPunisment(player)
                        return//sellField
                    }
                }
            }
            else -> {
                if (player.hasSomeNotMonopoly()) {
                    val selledFieldId = sellSomeNotMonopolyField(player)
                    fieldSellByHalf(player, board.fields[selledFieldId])
                    fieldSelledProperty.value = selledFieldId
                    aiPunisment(player)
                    return//sellNotMonopolyField
                }
                if (player.monopolyRealty.isNotEmpty() && sellSomeUpgrade(player, false) != -1) {
                    val idDegradeField = sellSomeUpgrade(player, true)
                    fieldDegradeProperty.value = idDegradeField
                    triggerProperty(sellUpgradeViewProperty)
                    aiPunisment(player)
                    return//sellUpgrade
                } else {
                    if (player.hasSomething()) {
                        val selledFieldId = sellSomeField(player)
                        fieldSellByHalf(player, board.fields[selledFieldId])
                        fieldSelledProperty.value = selledFieldId
                        aiPunisment(player)
                        return//sellField
                    }
                }
            }
        }

        playerSurrender()
    }

    private fun sellSomeUpgrade(player: Player, needSell: Boolean): Int {
        for (i in player.monopolyRealty) {
            for (j in board.fields.filter { it.type == i })
                if (j.upgrade > 0) {
                    if (needSell) {
                        player.moneyChange(j.upgradeCost)
                        j.upgrade--
                        j.penaltyUpdate()
                    }
                    return j.location
                }
        }
        return -1
    }

    private fun sellSomeField(player: Player): Int {
        for (i in player.realty) {
            if (player.realty.filter { it.type == i.type }.size == 1)
                return i.location
        }
        for (i in player.realty) {
            if (player.realty.filter { it.type == i.type }.size == 2)
                return i.location
        }
        for (i in player.realty) {
            if (player.realty.filter { it.type == i.type }.size == 3)
                return i.location
        }
        return 0 // никогда не выполняется, т.к. идет проверка до
    }

    private fun sellSomeNotMonopolyField(player: Player): Int {
        for (i in player.realty.filter { it.type !in player.monopolyRealty }) {
            if (player.realty.filter { it.type == i.type }.size == 2)
                return i.location
        }
        for (i in player.realty.filter { it.type !in player.monopolyRealty }) {
            if (player.realty.filter { it.type == i.type }.size == 1)
                return i.location
        }
        return 0 // никогда не выполняется, т.к. идет проверка до
    }

    private fun sellSomeOtherTypeField(player: Player): Int {
        for (i in player.realty.filter { it.type != board.fields[player.position].type && it.type !in player.monopolyRealty }) {
            if (player.realty.filter { it.type == i.type }.size == 1)
                return i.location
        }
        for (i in player.realty.filter { it.type != board.fields[player.position].type && it.type !in player.monopolyRealty }) {
            if (player.realty.filter { it.type == i.type }.size == 2)
                return i.location
        }
        return 0 // никогда не выполняется
    }

    fun setBalance() {
        if (data.size == 2) {
            for (i in 0..1) data[i].moneyChange(10000)
        }

        if (data.size == 3) {
            for (i in 0..2) data[i].moneyChange(1000)
        }

        if (data.size == 4) {
            for (i in 0..2) data[i].moneyChange(-2000)
        }

        if (data.size == 5) {
            for (i in 0..4) data[i].moneyChange(-5000)
        }
    }

    //settings reward
    fun checkCircleComplete(): Boolean {
        if ((currentPlayer.finishCircle && currentPlayer.circlesCompleted < 5) ||
            (currentPlayer.finishCircle && currentPlayer.circlesCompleted < 10 && cntPls < 4)
        ) {
            currentPlayer.moneyChange(2000)
            return true
        }
        return false
    }

    private fun checkPrisonByDouble(): Boolean {
        if ((dice.double && currentPlayer.doubleInARow == 2)) {
            prisonByDouble = true
            return true
        }
        prisonByDouble = false
        return false
    }

    fun endMotionLogic() {
        currentPlayer.currentMotionUpgrade.clear()
        if (!dice.double && !currentPlayer.justOutJail) {
            motionPlayer++
            motionPlayer %= cntPls
            statsCntOfMotion++
        }
        while (motionPlayer in loosers) {
            motionPlayer++
            motionPlayer %= cntPls
        }
        currentPlayer = data[motionPlayer]
    }

    private fun diceDoubleCheck(): Boolean {
        if (dice.double) {
            currentPlayer.doubleInARow++
            return true
        }
        currentPlayer.doubleInARow = 0
        return false
    }

    private fun realtyCanBuy() =
        board.fields[currentPlayer.position].couldBuy && board.fields[currentPlayer.position].owner == null

    fun punishmentOrPenalty() = ifPunishment() ||
            (board.fields[currentPlayer.position].owner != null && board.fields[currentPlayer.position].owner!!.id != currentPlayer.id)

    fun ifPunishment() = board.fields[currentPlayer.position].type == Type.Punisment

    private fun ifToPrison() = board.fields[currentPlayer.position].type == Type.ToPrison

    private fun stonksAction(): Boolean {
        if (ifStonks()) {
            currentPlayer.moneyChange(3000)
            return true
        }
        return false
    }

    fun ifStonks() = board.fields[currentPlayer.position].type == Type.Stonks

    fun ifSecret() = board.fields[currentPlayer.position].type == Type.Secret

    fun startAction(): Boolean {
        if (ifStart()) {
            currentPlayer.moneyChange(1000)
            return true
        }
        return false
    }

    fun ifStart(): Boolean = board.fields[currentPlayer.position].type == Type.Start

    fun playerAcceptBuyRealty(): Boolean {
        if (currentPlayer.money >= board.fields[currentPlayer.position].cost) {
            currentPlayer.moneyChange(-board.fields[currentPlayer.position].cost)
            currentPlayer.realty.add(board.fields[currentPlayer.position])
            board.fields[currentPlayer.position].owner = currentPlayer
            currentPlayer.checkForMonopoly(board.fields[currentPlayer.position])
            board.fields[currentPlayer.position].penaltyUpdate()
            return true
        }
        return false
    }

    fun playerPayPenalty(): Boolean {
        if (currentPlayer.money >= board.fields[currentPlayer.position].penalty) {
            currentPlayer.moneyChange(-board.fields[currentPlayer.position].penalty)
            if (board.fields[currentPlayer.position].type != Type.Punisment) {
                board.fields[currentPlayer.position].owner!!.moneyChange(board.fields[currentPlayer.position].penalty)
            }
            return true
        }
        return false
    }

    fun playerLose() {
        loosers.add(data.indexOf(currentPlayer))
    }

    private fun positiveSecret() {
        when (dice.secret.second) {
            SecretAction.Action1 -> currentPlayer.moneyChange(250)
            SecretAction.Action2 -> currentPlayer.moneyChange(500)
            SecretAction.Action3 -> currentPlayer.moneyChange(300)
            SecretAction.Action4 -> currentPlayer.moneyChange(750)
            else -> currentPlayer.moneyChange(100)
        }
    }

    fun fieldClear(i: Field) {
        board.fields[i.location].owner = null
        board.fields[i.location].upgrade = 0
        if (board.fields[i.location].hasMonopoly) board.fields[i.location].monopolyChange()
        board.fields[i.location].penaltyUpdate()
    }

    fun gameIsEnd() = cntPls - loosers.size == 1

    private fun secretIsPositive(): Boolean {
        dice.secretAction()
        if (dice.secret.first) return true
        return false
    }

    fun negativePay(): Boolean {
        when (dice.secret.second) {
            SecretAction.Action1 -> if (currentPlayer.money >= 300) {
                currentPlayer.moneyChange(-300)
                return true
            }
            SecretAction.Action2 -> if (currentPlayer.money >= 500) {
                currentPlayer.moneyChange(-500)
                return true
            }
            SecretAction.Action3 -> if (currentPlayer.money >= 40) {
                currentPlayer.moneyChange(-40)
                return true
            }
            SecretAction.Action4 -> if (currentPlayer.money >= 750) {
                currentPlayer.moneyChange(-750)
                return true
            }
            else -> if (currentPlayer.money >= 250) {
                currentPlayer.moneyChange(-250)
                return true
            }
        }
        return false
    }

    fun prisonPay(): Int {
        if (currentPlayer.prisonDays == 4 && currentPlayer.money >= 750) {
            currentPlayer.prisonDays = 0
            currentPlayer.moneyChange(-750)
            currentPlayer.justOutJail = true
            notifyInView.value = ("${currentPlayer.name} выходит из тюрьмы, заплатив 750$.")
            endMotion()
            return 1
        }
        if (currentPlayer.prisonDays != 4 && currentPlayer.money >= 500) {
            currentPlayer.prisonDays = 0
            currentPlayer.moneyChange(-500)
            currentPlayer.justOutJail = true
            notifyInView.value = ("${currentPlayer.name} выходит из тюрьмы, заплатив 500$.")
            endMotion()
            return 2
        }
        return -1
    }

    fun prisonTryLogic() {
        dice.roll()
        runAsync { if (delay) Thread.sleep(600) } ui {
            if (prisonTry()) {
                notifyInView.value = ("${currentPlayer.name} выходит из тюрьмы, выбив дубль!")
                playerMove(currentPlayer)
            } else {
                if (4 - currentPlayer.prisonDays + 1 != 1)
                    notifyInView.value = ("Игрок остается в тюрьме еще на ${4 - currentPlayer.prisonDays + 1} хода.")
                else
                    notifyInView.value = ("Всего один ход отлучает ${currentPlayer.name}, от свободы!")
                endMotion()
            }
        }
    }

    fun prisonPayDay() = currentPlayer.prisonDays == 4
    private fun prisonPayDay(player: Player) = player.prisonDays == 4

    private fun prisonTry(): Boolean {
        if (dice.double) {
            currentPlayer.prisonDays = 0
            dice.double = false
            return true
        }
        currentPlayer.prisonDays++
        return false
    }


    private var monopolySize = 2

    private var type = Type.Perfume

    fun fieldActionInit() {
        monopolySize = when (fieldClicked.location) {
            1, 2, 8, 9, 11, 13, 22, 24, 26, 27 -> 2
            else -> 3
        }
        type = when (fieldClicked.location) {
            1, 2 -> Type.Perfume
            3, 5, 6 -> Type.Clothes
            8, 9 -> Type.SocialNetwork
            11, 13 -> Type.Soda
            15, 16, 19 -> Type.Airlanes
            22, 24 -> Type.FastFood
            26, 27 -> Type.Software
            else -> Type.Car
        }
    }

    fun playerHasMonopoly() = playerClicked.realty.filter { it.type == type }.size == monopolySize

    fun fieldCantBeUpgraded() = fieldClicked.upgrade > 4 || playerClicked.currentMotionUpgrade.contains(type)

    fun fieldCantBeSelled() = fieldClicked.upgrade == 0

    fun fieldSellByHalf() {
        playerClicked.moneyChange(fieldClicked.cost / 2)
        playerClicked.realty.remove(fieldClicked)
        fieldClicked.owner = null
        fieldClicked.upgrade = 0
        playerClicked.checkForMonopoly(fieldClicked)
        fieldClicked.penaltyUpdate()
    }

    private fun fieldSellByHalf(player: Player, field: Field) {
        player.moneyChange(field.cost / 2)
        player.realty.remove(field)
        field.owner = null
        field.upgrade = 0
        player.checkForMonopoly(field)
        field.penaltyUpdate()
    }

    fun fieldBuildUpgrade(): Boolean {
        if (playerClicked.money >= fieldClicked.upgradeCost) {
            playerClicked.moneyChange(-fieldClicked.upgradeCost)
            fieldClicked.upgrade++
            fieldClicked.penaltyUpdate()
            playerClicked.currentMotionUpgrade.add(type)
            return true
        }
        return false
    }

    fun fieldSellUpgrade() {
        playerClicked.moneyChange(fieldClicked.upgradeCost)
        fieldClicked.upgrade--
        fieldClicked.penaltyUpdate()
    }
    //---------------------------------------------------------------
    //---------------------------------------------------------------
    //-------------------The logic of one move-----------------------
    //---------------------------------------------------------------
    //---------------------------------------------------------------

    fun motion() {//function start
        val player = currentPlayer
        if (player.playerInPrison()) {
            triggerProperty(prisonInitProperty)
            if (player.ai) {
                aiPrisonInstructions(player)
            }
            return
        }
        dice.roll()
        runAsync { if (delay) Thread.sleep(500) } ui {
            if (checkPrisonByDouble()) {
                playerToPrison(player)
            } else {
                playerMove(player)
            }
        }
    }

    private fun playerMove(player: Player) {
        runAsync {
            if (delay) Thread.sleep(250)
        } ui {
            player.positionChange(dice.count)
            statsVisitedField[player.position]++

            runAsync {
                if (delay) Thread.sleep(500)
            } ui {
                fieldEvent(player)
            }
        }
    }

    private fun fieldEvent(player: Player) {
        //check cycle completed and reward according to the settings
        if (checkCircleComplete()) {
            triggerProperty(cycleCompleteProperty)
        }
        //buy realty
        if (realtyCanBuy()) {
            triggerProperty(offerToBuyInitProperty)
            if (player.ai) {
                aiBuyInstructions(player)
                endMotion()
            }
            if (diceDoubleCheck()) triggerProperty(diceDoubleProperty)
            return
        }
        //pay penalty
        if (punishmentOrPenalty()) {
            triggerProperty(penaltyInitProperty)
            if (player.ai) {
                aiPunisment(player)
            }
            if (diceDoubleCheck()) triggerProperty(diceDoubleProperty)
            return
        }
        //player to prison
        if (ifToPrison()) {
            playerToPrison(player)
            return
        }
        //get stonks
        if (stonksAction()) {
            triggerProperty(stonksActionProperty)
        }
        //secret action
        if (ifSecret()) {
            secretAction()
            if (diceDoubleCheck()) triggerProperty(diceDoubleProperty)
            return
        }
        //start field
        if (startAction()) {
            triggerProperty(startActionProperty)
        }
        //player owner field
        if (board.fields[player.position].owner != null && board.fields[player.position].owner!!.id == player.id) {
            notifyInView.value = "Игрок пришел с проверкой на свое поле"
        }

        if (player.position == 7) notifyInView.value = (player.name + " решил прогуляться в парке, рядом с темницей.")

        if (player.position == 14) notifyInView.value = (player.name + " наслаждается отдыхом на природе.")

        if (diceDoubleCheck()) triggerProperty(diceDoubleProperty)
        endMotion()
    }

    fun endMotion() {
        runAsync { if (delay) Thread.sleep(300) } ui {
            endMotionLogic()
            triggerProperty(viewEndMotionProperty)
            val player = data[motionPlayer]
            if (player.ai) {
                for (i in aiInstructions(player)) {
                    notifyInView.value =
                        (player.name + " строит филиал. Количество филиалов на поле " + board.fields[i].name + " - " + board.fields[i].upgrade)
                }
                triggerProperty(updateUpgradeView)
            }
            if (!dice.double && !player.justOutJail) {
                notifyInView.value = ""
                notifyInView.value = ("Ваш ход, ${data[motionPlayer].name} !")
            }
            player.justOutJail = false
            runAsync {
                while (exchangePause) {
                    Thread.sleep(100)
                }
                runAsync { if (delay) Thread.sleep(500) } ui {
                    if ((player.playerInPrison() || player.ai) && !gameIsEnd) {
                        motion()
                    }
                }
            }
        }
    }

    private fun playerToPrison(current: Player) {
        current.goToPrison()
        statsVisitedField[current.position]++
        dice.double = false
        triggerProperty(toPrisonViewProperty)
        runAsync { if (delay) Thread.sleep(200) } ui { endMotion() }
    }

    private fun secretAction() {
        //positive
        if (secretIsPositive()) {
            positiveSecret()
            triggerProperty(positiveEventInitProperty)
            endMotion()
        } else {//negative
            triggerProperty(negativeEventInitProperty)
            if (currentPlayer.ai) {
                aiNegativeEvent(currentPlayer)
            }
        }
    }

    fun playerSurrender() {
        playerLose()
        triggerProperty(surrenderViewProperty)
        dice.double = false
        endMotion()
        checkEndGame()
    }

    private fun checkEndGame() {
        runAsync { if (delay) Thread.sleep(350) } ui {
            if (gameIsEnd()) {
                setGameStatus(false)
            }
        }
    }

}


