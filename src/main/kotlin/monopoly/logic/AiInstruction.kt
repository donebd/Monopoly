package monopoly.logic

class AiInstruction(val game: Game) {
    fun instructions(player: Player): List<Int> {
        val tmp = mutableListOf<Int>()
        if (player.monopolyRealty.isNotEmpty() && (player.aiDifficulty == Difficulty.Hard || (player.aiDifficulty == Difficulty.Medium && (1..100).random() in (1..85))
                    || (player.aiDifficulty == Difficulty.Easy && (0..1).random() == 1))
        ) {
            for (i in player.monopolyRealty) {
                game.board.fields.filter { it.type == i }.forEach {
                    if (i !in player.currentMotionUpgrade && player.money >= it.upgradeCost && it.upgrade < 5) {
                        player.moneyChange(-it.upgradeCost)
                        it.upgrade++
                        it.penaltyUpdate()
                        player.currentMotionUpgrade.add(i)
                        tmp.add(it.location)
                    }
                }
            }
        }//Блок апгрейда своих монополий by 
        return tmp
    }

    fun prisonInstructions(player: Player) {
        when (player.aiDifficulty) {
            Difficulty.Hard -> {
                if (!game.prisonPayDay(player)) {
                    if ((player.money >= 10000 || (player.monopolyRealty.isEmpty() && playerNearlyHasSomeMonopoly(player))) && onBoardHasBuyableFields() && player.money >= 500) {
                        game.prisonPay()
                        return//buyOut
                    }
                    game.prisonTryLogic()
                    return//prisonTry
                } else {
                    if (player.money >= 750) {
                        game.prisonPay()
                        return//buyOut
                    }
                    if (player.monopolyRealty.isNotEmpty() && sellSomeUpgrade(player, false) != -1) {
                        val idDegradeField = sellSomeUpgrade(player, true)
                        game.view.fieldDegradeProperty.value = idDegradeField
                        game.triggerProperty(game.view.sellUpgradeViewProperty)
                        prisonInstructions(player)
                        return//sellUpgrade
                    }
                    if (player.hasSomething()) {
                        val selledFieldId = sellSomeField(player)
                        game.fieldSellByHalf(player, game.board.fields[selledFieldId])
                        game.view.fieldSelledProperty.value = selledFieldId
                        prisonInstructions(player)
                        return//sellField
                    }
                }
            }
            else -> {
                if (!game.prisonPayDay(player)) {
                    if (player.aiDifficulty == Difficulty.Easy && player.money >= 500 && (0..1).random() == 1) {
                        game.prisonPay()
                        return//buyOut
                    }
                    if (player.aiDifficulty == Difficulty.Medium && player.money >= 500 && player.monopolyRealty.isEmpty() && onBoardHasBuyableFields()) {
                        game.prisonPay()
                        return//buyOut
                    }
                    game.prisonTryLogic()
                    return//prisonTry
                } else {
                    if (player.money >= 750) {
                        game.prisonPay()
                        return//buyOut
                    }
                    if (player.hasSomeNotMonopoly()) {
                        val selledFieldId = sellSomeNotMonopolyField(player)
                        game.fieldSellByHalf(player, game.board.fields[selledFieldId])
                        game.view.fieldSelledProperty.value = selledFieldId
                        prisonInstructions(player)
                        return//sellField
                    }
                    if (player.monopolyRealty.isNotEmpty() && sellSomeUpgrade(player, false) != -1) {
                        val idDegradeField = sellSomeUpgrade(player, true)
                        game.view.fieldDegradeProperty.value = idDegradeField
                        game.triggerProperty(game.view.sellUpgradeViewProperty)
                        prisonInstructions(player)
                        return//sellUpgrade
                    }
                    if (player.hasSomething()) {
                        val selledFieldId = sellSomeField(player)
                        game.fieldSellByHalf(player, game.board.fields[selledFieldId])
                        game.view.fieldSelledProperty.value = selledFieldId
                        prisonInstructions(player)
                        return//sellField
                    }
                }
            }
        }
        game.playerSurrender()
    }

     private fun onBoardHasBuyableFields(): Boolean {
        return game.board.fields.any { it.couldBuy && it.owner == null }
    }

     fun buyInstructions(player: Player) {
        when (player.aiDifficulty) {
            Difficulty.Hard -> {
                if (player.money >= game.board.fields[player.position].cost + 500 && (player.monopolyRealty.isEmpty() || player.money >= 10000 || someOnBoardNearlyHasMonopoly())) {
                    game.playerAcceptBuyRealty()
                    game.triggerProperty(game.view.fieldBoughtProperty)
                    return//buy
                }
                if (playerNearlyHasMonopoly(player, game.board.fields[player.position])
                    && calculateRealtyCost(player) + player.money >= game.board.fields[player.position].cost + 500
                    && player.monopolyRealty.isNotEmpty() && sellSomeUpgrade(player, false) != -1
                ) {
                    val idDegradeField = sellSomeUpgrade(player, true)
                    game.view.fieldDegradeProperty.value = idDegradeField
                    game.triggerProperty(game.view.sellUpgradeViewProperty)
                    buyInstructions(player)
                    return//sellUpgrade
                }
                if (playerNearlyHasMonopoly(player, game.board.fields[player.position])
                    && calculateRealtyCost(player) + player.money >= game.board.fields[player.position].cost + 500
                    && player.realty.any { it.type != game.board.fields[player.position].type }
                ) {
                    val selledFieldId = sellSomeOtherTypeField(player)
                    game.fieldSellByHalf(player, game.board.fields[selledFieldId])
                    game.view.fieldSelledProperty.value = selledFieldId
                    buyInstructions(player)
                    return//sellField
                }
            }
            Difficulty.Medium -> {
                if (player.money >= game.board.fields[player.position].cost && (player.monopolyRealty.isEmpty() || someOnBoardNearlyHasMonopoly())) {
                    game.playerAcceptBuyRealty()
                    game.triggerProperty(game.view.fieldBoughtProperty)
                    return//buy
                }

            }
            else -> if (player.money >= game.board.fields[player.position].cost) {
                game.playerAcceptBuyRealty()
                game.triggerProperty(game.view.fieldBoughtProperty)
                return//buy
            }
        }
        return//nothing
    }

     private fun calculateRealtyCost(player: Player): Int {// возвращает кол-во денег с продажи всех немонопольных полей и апгрейдов
        var sum = 0
        for (i in player.monopolyRealty) {
            for (j in game.board.fields.filter { it.type == i })
                if (j.upgrade > 0) {
                    sum += j.upgrade * j.upgradeCost
                }
        }//стоимость всех апгрейдов

        for (i in player.realty.filter { it.type != game.board.fields[player.position].type && it.type !in player.monopolyRealty }) {
            if (player.realty.filter { it.type == i.type }.size == 1)
                sum += i.cost / 2
        }
        for (i in player.realty.filter { it.type != game.board.fields[player.position].type && it.type !in player.monopolyRealty }) {
            if (player.realty.filter { it.type == i.type }.size == 2)
                sum += i.cost / 2
        }
        return sum //стоимость всех немонопольных полей
    }

     private fun someOnBoardNearlyHasMonopoly(): Boolean { // функция для проверки необходимости покупки поля для руина монополии другому игроку
        for (player in game.data)
            if (playerNearlyHasMonopoly(player, game.board.fields[game.currentPlayer.position])) return true
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

     fun negativeEvent(player: Player) {
        when (game.dice.secret.second) {
            SecretAction.Action1 -> if (player.money >= 300) {
                game.negativePay()
                game.endMotion()
                return//pay
            }
            SecretAction.Action2 -> if (player.money >= 500) {
                game.negativePay()
                game.endMotion()
                return//pay
            }
            SecretAction.Action3 -> if (player.money >= 40) {
                game.negativePay()
                game.endMotion()
                return//pay
            }
            SecretAction.Action4 -> if (player.money >= 750) {
                game.negativePay()
                game.endMotion()
                return//pay
            }
            else -> if (player.money >= 250) {
                game.negativePay()
                game.endMotion()
                return//pay
            }
        }
        when (player.aiDifficulty) {
            Difficulty.Hard, Difficulty.Medium -> {
                if (player.monopolyRealty.isNotEmpty() && sellSomeUpgrade(player, false) != -1) {
                    val idDegradeField = sellSomeUpgrade(player, true)
                    game.view.fieldDegradeProperty.value = idDegradeField
                    game.triggerProperty(game.view.sellUpgradeViewProperty)
                    negativeEvent(player)
                    return//sellUpgrade
                } else {
                    if (player.hasSomething()) {
                        val selledFieldId = sellSomeField(player)
                        game.fieldSellByHalf(player, game.board.fields[selledFieldId])
                        game.view.fieldSelledProperty.value = selledFieldId
                        negativeEvent(player)
                        return//sellField
                    }
                }
            }
            else -> {
                if (player.hasSomeNotMonopoly()) {
                    val selledFieldId = sellSomeNotMonopolyField(player)
                    game.fieldSellByHalf(player, game.board.fields[selledFieldId])
                    game.view.fieldSelledProperty.value = selledFieldId
                    negativeEvent(player)
                    return//sellNotMonopolyField
                }
                if (player.monopolyRealty.isNotEmpty() && sellSomeUpgrade(player, false) != -1) {
                    val idDegradeField = sellSomeUpgrade(player, true)
                    game.view.fieldDegradeProperty.value = idDegradeField
                    game.triggerProperty(game.view.sellUpgradeViewProperty)
                    negativeEvent(player)
                    return//sellUpgrade
                } else {
                    if (player.hasSomething()) {
                        val selledFieldId = sellSomeField(player)
                        game.fieldSellByHalf(player, game.board.fields[selledFieldId])
                        game.view.fieldSelledProperty.value = selledFieldId
                        negativeEvent(player)
                        return//sellField
                    }
                }
            }
        }

        game.playerSurrender()
    }

     fun punisment(player: Player) {
        if (player.money >= game.board.fields[player.position].penalty) {
            game.playerPayPenalty()
            game.endMotion()
            return
        }
        when (player.aiDifficulty) {
            Difficulty.Hard, Difficulty.Medium -> {
                if (player.monopolyRealty.isNotEmpty() && sellSomeUpgrade(player, false) != -1) {
                    val idDegradeField = sellSomeUpgrade(player, true)
                    game.view.fieldDegradeProperty.value = idDegradeField
                    game.triggerProperty(game.view.sellUpgradeViewProperty)
                    punisment(player)
                    return//sellUpgrade
                } else {
                    if (player.hasSomething()) {
                        val selledFieldId = sellSomeField(player)
                        game.fieldSellByHalf(player, game.board.fields[selledFieldId])
                        game.view.fieldSelledProperty.value = selledFieldId
                        punisment(player)
                        return//sellField
                    }
                }
            }
            else -> {
                if (player.hasSomeNotMonopoly()) {
                    val selledFieldId = sellSomeNotMonopolyField(player)
                    game.fieldSellByHalf(player, game.board.fields[selledFieldId])
                    game.view.fieldSelledProperty.value = selledFieldId
                    punisment(player)
                    return//sellNotMonopolyField
                }
                if (player.monopolyRealty.isNotEmpty() && sellSomeUpgrade(player, false) != -1) {
                    val idDegradeField = sellSomeUpgrade(player, true)
                    game.view.fieldDegradeProperty.value = idDegradeField
                    game.triggerProperty(game.view.sellUpgradeViewProperty)
                    punisment(player)
                    return//sellUpgrade
                } else {
                    if (player.hasSomething()) {
                        val selledFieldId = sellSomeField(player)
                        game.fieldSellByHalf(player, game.board.fields[selledFieldId])
                        game.view.fieldSelledProperty.value = selledFieldId
                        punisment(player)
                        return//sellField
                    }
                }
            }
        }

        game.playerSurrender()
    }

     private fun sellSomeUpgrade(player: Player, needSell: Boolean): Int {
        for (i in player.monopolyRealty) {
            for (j in game.board.fields.filter { it.type == i })
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
        for (i in player.realty.filter { it.type != game.board.fields[player.position].type && it.type !in player.monopolyRealty }) {
            if (player.realty.filter { it.type == i.type }.size == 1)
                return i.location
        }
        for (i in player.realty.filter { it.type != game.board.fields[player.position].type && it.type !in player.monopolyRealty }) {
            if (player.realty.filter { it.type == i.type }.size == 2)
                return i.location
        }
        return 0 // никогда не выполняется
    }
}