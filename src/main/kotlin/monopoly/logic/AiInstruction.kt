package monopoly.logic

class AiInstruction(val game: Game) {
    private val answer = FeedBackPlayer(game)

    fun instructions(player: Player): List<Int> {
        val tmp = mutableListOf<Int>()
        if (player.monopolyRealty.isNotEmpty() && player.aiDifficulty == Difficulty.HARDEST) {
            val benefits =
                calculateCurrentBenefitOfFields(player, null, monopoly = true, default = false)
            var maxValuable: Field
            while (benefits.isNotEmpty()) {
                maxValuable = game.board.fields[benefits.maxBy { it.value }?.key ?: -1]
                benefits.remove(maxValuable.location)
                if (maxValuable.type !in player.currentMotionUpgrade && player.money >= maxValuable.upgradeCost && maxValuable.upgrade < 5) {
                    player.moneyChange(-maxValuable.upgradeCost)
                    maxValuable.upgrade++
                    maxValuable.penaltyUpdate()
                    player.currentMotionUpgrade.add(maxValuable.type)
                    tmp.add(maxValuable.location)
                }
            }
        }
        if (
            player.monopolyRealty.isNotEmpty() && (
                    player.aiDifficulty == Difficulty.HARD
                            || (player.aiDifficulty == Difficulty.MEDIUM && (1..100).random() in (1..85))
                            || (player.aiDifficulty == Difficulty.EASY && (0..1).random() == 1)
                    )
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
            Difficulty.HARDEST -> {
                if (!player.isPrisonPayDay()) {
                    if ((player.money >= 10000 || (player.monopolyRealty.isEmpty() && playerNearlyHasSomeMonopoly(player))) && onBoardHasBuyableFields() && player.money >= 500) {
                        answer.prisonPay(player)
                        return//buyOut
                    }
                    answer.prisonTryLogic(player)
                    return//prisonTry
                } else {
                    if (player.money >= 750) {
                        answer.prisonPay(player)
                        return//buyOut
                    }
                    if (player.realty.isNotEmpty()) {
                        if (player.monopolyRealty.isNotEmpty() && sellSomeUpgrade(player, false) != -1) {
                            val benefits =
                                calculateCurrentBenefitOfFields(player, null, monopoly = true, default = false)
                            var minValuableField: Field
                            while (benefits.isNotEmpty()) {
                                minValuableField = game.board.fields[benefits.minBy { it.value }?.key ?: -1]
                                benefits.remove(minValuableField.location)
                                if (minValuableField.upgrade != 0) {
                                    sellUpgrade(player, minValuableField)
                                    game.view.fieldDegradeProperty.value = minValuableField.location
                                    game.triggerProperty(game.view.sellUpgradeViewProperty)
                                    prisonInstructions(player)
                                    return//sellUpgrade
                                }
                            }
                        } else {
                            var benefits =
                                calculateCurrentBenefitOfFields(player, null, monopoly = false, default = true)
                            if (benefits.isEmpty()) benefits =
                                calculateCurrentBenefitOfFields(player, null, monopoly = true, default = true)
                            val minValuableField = game.board.fields[benefits.minBy { it.value }?.key ?: -1]
                            answer.fieldSellByHalf(player, minValuableField)
                            game.view.fieldSelledProperty.value = minValuableField.location
                            prisonInstructions(player)
                            return//sellField
                        }
                    }
                }
            }
            Difficulty.HARD -> {
                if (!player.isPrisonPayDay()) {
                    if ((player.money >= 10000 || (player.monopolyRealty.isEmpty() && playerNearlyHasSomeMonopoly(player))) && onBoardHasBuyableFields() && player.money >= 500) {
                        answer.prisonPay(player)
                        return//buyOut
                    }
                    answer.prisonTryLogic(player)
                    return//prisonTry
                } else {
                    if (player.money >= 750) {
                        answer.prisonPay(player)
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
                        answer.fieldSellByHalf(player, game.board.fields[selledFieldId])
                        game.view.fieldSelledProperty.value = selledFieldId
                        prisonInstructions(player)
                        return//sellField
                    }
                }
            }
            else -> {
                if (!player.isPrisonPayDay()) {
                    if (player.aiDifficulty == Difficulty.EASY && player.money >= 500 && (0..1).random() == 1) {
                        answer.prisonPay(player)
                        return//buyOut
                    }
                    if (player.aiDifficulty == Difficulty.MEDIUM && player.money >= 500 && player.monopolyRealty.isEmpty() && onBoardHasBuyableFields()) {
                        answer.prisonPay(player)
                        return//buyOut
                    }
                    answer.prisonTryLogic(player)
                    return//prisonTry
                } else {
                    if (player.money >= 750) {
                        answer.prisonPay(player)
                        return//buyOut
                    }
                    if (player.hasSomeNotMonopoly()) {
                        val selledFieldId = sellSomeNotMonopolyField(player)
                        answer.fieldSellByHalf(player, game.board.fields[selledFieldId])
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
                        answer.fieldSellByHalf(player, game.board.fields[selledFieldId])
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
            Difficulty.HARDEST -> {
                if (
                    player.money >= game.board.fields[player.position].cost &&
                    ((calculateBenefitOfField(game.board.fields[player.position], player) + calculateUpgradeCost(player) + player.money >= 0)
                            || playerNearlyHasMonopoly(player, game.board.fields[player.position])) &&
                    (player.monopolyRealty.isEmpty() || playerNotNeedUpgrade(player) || someOnBoardNearlyHasMonopoly())
                ) {
                    answer.playerAcceptBuyRealty(player)
                    game.triggerProperty(game.view.fieldBoughtProperty)
                    return//buy
                }
                if (playerNearlyHasMonopoly(player, game.board.fields[player.position])
                    && calculateRealtyCost(player) + player.money >= game.board.fields[player.position].cost
                ) {
                    if (player.monopolyRealty.isNotEmpty() && sellSomeUpgrade(player, false) != -1) {
                        val benefits = calculateCurrentBenefitOfFields(
                            player, game.board.fields[player.position].type, monopoly = true, default = false
                        )
                        var minValuableField: Field
                        while (benefits.isNotEmpty()) {
                            minValuableField = game.board.fields[benefits.minBy { it.value }?.key ?: -1]
                            benefits.remove(minValuableField.location)
                            if (minValuableField.upgrade != 0) {
                                sellUpgrade(player, minValuableField)
                                game.view.fieldDegradeProperty.value = minValuableField.location
                                game.triggerProperty(game.view.sellUpgradeViewProperty)
                                buyInstructions(player)
                                return//sellUpgrade
                            }
                        }
                    }
                    if (player.realty.any { it.type != game.board.fields[player.position].type }) {
                        val benefits = calculateCurrentBenefitOfFields(
                            player, game.board.fields[player.position].type, monopoly = false, default = true
                        )
                        val minValuableField = game.board.fields[benefits.minBy { it.value }?.key ?: -1]
                        answer.fieldSellByHalf(player, minValuableField)
                        game.view.fieldSelledProperty.value = minValuableField.location
                        buyInstructions(player)
                        return//sellField
                    }
                }
            }
            Difficulty.HARD -> {
                if (player.money >= game.board.fields[player.position].cost + 500 && (player.monopolyRealty.isEmpty() || player.money >= 10000 || someOnBoardNearlyHasMonopoly())) {
                    answer.playerAcceptBuyRealty(player)
                    game.triggerProperty(game.view.fieldBoughtProperty)
                    return//buy
                }
                if (playerNearlyHasMonopoly(player, game.board.fields[player.position])
                    && calculateRealtyCost(player) + player.money >= game.board.fields[player.position].cost + 500
                ) {
                    if (player.monopolyRealty.isNotEmpty() && sellSomeUpgrade(player, false) != -1) {
                        val idDegradeField = sellSomeUpgrade(player, true)
                        game.view.fieldDegradeProperty.value = idDegradeField
                        game.triggerProperty(game.view.sellUpgradeViewProperty)
                        buyInstructions(player)
                        return//sellUpgrade
                    } else if (player.realty.any { it.type != game.board.fields[player.position].type }) {
                        val selledFieldId = sellSomeOtherTypeField(player)
                        answer.fieldSellByHalf(player, game.board.fields[selledFieldId])
                        game.view.fieldSelledProperty.value = selledFieldId
                        buyInstructions(player)
                        return//sellField
                    }
                }
            }
            Difficulty.MEDIUM -> {
                if (player.money >= game.board.fields[player.position].cost && (player.monopolyRealty.isEmpty() || someOnBoardNearlyHasMonopoly())) {
                    answer.playerAcceptBuyRealty(player)
                    game.triggerProperty(game.view.fieldBoughtProperty)
                    return//buy
                }

            }
            else -> if (player.money >= game.board.fields[player.position].cost) {
                answer.playerAcceptBuyRealty(player)
                game.triggerProperty(game.view.fieldBoughtProperty)
                return//buy
            }
        }
        return//nothing
    }

    private fun playerNotNeedUpgrade(player: Player): Boolean {
        val monopoly = player.realty.filter { it.type in player.monopolyRealty }
        for (field in monopoly) {
            if (field.upgrade != 5) return false
        }
        return true
    }

    private fun calculateUpgradeCost(player: Player): Int {
        var sum = 0
        for (i in player.monopolyRealty) {
            for (j in game.board.fields.filter { it.type == i })
                if (j.upgrade > 0) {
                    sum += j.upgrade * j.upgradeCost
                }
        }
        return sum
    }

    private fun calculateRealtyCost(player: Player): Int {// возвращает кол-во денег с продажи всех немонопольных полей и апгрейдов
        var sum = calculateUpgradeCost(player)

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
            1 -> if (field.type == Type.PERFUME || field.type == Type.SOFTWARE || field.type == Type.SODA || field.type == Type.FASTFOOD || field.type == Type.NETWORK) return true
            2 -> if (field.type == Type.CLOTHES || field.type == Type.CAR || field.type == Type.AIRLINES) return true
            else -> return false
        }
        return false
    }

    private fun somePlayerNearlyHasMonopoly(exception: Player?, field: Field): Boolean {
        val players =
            if (exception != null) game.data.filter { game.data.indexOf(it) !in game.loosers && it.id != exception.id }
            else game.data.filter { game.data.indexOf(it) !in game.loosers }
        for (player in players) {
            if (playerNearlyHasMonopoly(player, field)) {
                return true
            }
        }
        return false
    }

    private fun playerNearlyHasSomeMonopoly(player: Player): Boolean {
        for (i in player.realty) if (playerNearlyHasMonopoly(player, i)) return true
        return false
    }

    fun negativeEvent(player: Player) {
        when (game.dice.secret.second) {
            SecretAction.ACTION1 -> if (player.money >= 300) {
                answer.negativePay(player)
                game.endMotion()
                return//pay
            }
            SecretAction.ACTION2 -> if (player.money >= 500) {
                answer.negativePay(player)
                game.endMotion()
                return//pay
            }
            SecretAction.ACTION3 -> if (player.money >= 40) {
                answer.negativePay(player)
                game.endMotion()
                return//pay
            }
            SecretAction.ACTION4 -> if (player.money >= 750) {
                answer.negativePay(player)
                game.endMotion()
                return//pay
            }
            else -> if (player.money >= 250) {
                answer.negativePay(player)
                game.endMotion()
                return//pay
            }
        }
        when (player.aiDifficulty) {
            Difficulty.HARDEST -> {
                if (player.realty.isNotEmpty()) {
                    if (player.monopolyRealty.isNotEmpty() && sellSomeUpgrade(player, false) != -1) {
                        val benefits = calculateCurrentBenefitOfFields(player, null, monopoly = true, default = false)
                        var minValuableField: Field
                        while (benefits.isNotEmpty()) {
                            minValuableField = game.board.fields[benefits.minBy { it.value }?.key ?: -1]
                            benefits.remove(minValuableField.location)
                            if (minValuableField.upgrade != 0) {
                                sellUpgrade(player, minValuableField)
                                game.view.fieldDegradeProperty.value = minValuableField.location
                                game.triggerProperty(game.view.sellUpgradeViewProperty)
                                negativeEvent(player)
                                return//sellUpgrade
                            }
                        }
                    } else {
                        var benefits =
                            calculateCurrentBenefitOfFields(player, null, monopoly = false, default = true)
                        if (benefits.isEmpty()) benefits =
                            calculateCurrentBenefitOfFields(player, null, monopoly = true, default = true)
                        val minValuableField = game.board.fields[benefits.minBy { it.value }?.key ?: -1]
                        answer.fieldSellByHalf(player, minValuableField)
                        game.view.fieldSelledProperty.value = minValuableField.location
                        negativeEvent(player)
                        return//sellField
                    }
                }
            }
            Difficulty.HARD, Difficulty.MEDIUM -> {
                if (player.monopolyRealty.isNotEmpty() && sellSomeUpgrade(player, false) != -1) {
                    val idDegradeField = sellSomeUpgrade(player, true)
                    game.view.fieldDegradeProperty.value = idDegradeField
                    game.triggerProperty(game.view.sellUpgradeViewProperty)
                    negativeEvent(player)
                    return//sellUpgrade
                } else {
                    if (player.hasSomething()) {
                        val selledFieldId = sellSomeField(player)
                        answer.fieldSellByHalf(player, game.board.fields[selledFieldId])
                        game.view.fieldSelledProperty.value = selledFieldId
                        negativeEvent(player)
                        return//sellField
                    }
                }
            }
            else -> {
                if (player.hasSomeNotMonopoly()) {
                    val selledFieldId = sellSomeNotMonopolyField(player)
                    answer.fieldSellByHalf(player, game.board.fields[selledFieldId])
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
                        answer.fieldSellByHalf(player, game.board.fields[selledFieldId])
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
            answer.playerPayPenalty(player)
            game.endMotion()
            return
        }
        when (player.aiDifficulty) {
            Difficulty.HARDEST -> {
                if (player.realty.isNotEmpty()) {
                    if (player.monopolyRealty.isNotEmpty() && sellSomeUpgrade(player, false) != -1) {
                        val benefits = calculateCurrentBenefitOfFields(player, null, monopoly = true, default = false)
                        var minValuableField: Field
                        while (benefits.isNotEmpty()) {
                            minValuableField = game.board.fields[benefits.minBy { it.value }?.key ?: -1]
                            benefits.remove(minValuableField.location)
                            if (minValuableField.upgrade != 0) {
                                sellUpgrade(player, minValuableField)
                                game.view.fieldDegradeProperty.value = minValuableField.location
                                game.triggerProperty(game.view.sellUpgradeViewProperty)
                                punisment(player)
                                return//sellUpgrade
                            }
                        }
                    } else {
                        var benefits =
                            calculateCurrentBenefitOfFields(player, null, monopoly = false, default = true)
                        if (benefits.isEmpty()) benefits =
                            calculateCurrentBenefitOfFields(player, null, monopoly = true, default = true)
                        val minValuableField = game.board.fields[benefits.minBy { it.value }?.key ?: -1]
                        answer.fieldSellByHalf(player, minValuableField)
                        game.view.fieldSelledProperty.value = minValuableField.location
                        punisment(player)
                        return//sellField
                    }
                }
            }
            Difficulty.MEDIUM, Difficulty.HARD -> {
                if (player.monopolyRealty.isNotEmpty() && sellSomeUpgrade(player, false) != -1) {
                    val idDegradeField = sellSomeUpgrade(player, true)
                    game.view.fieldDegradeProperty.value = idDegradeField
                    game.triggerProperty(game.view.sellUpgradeViewProperty)
                    punisment(player)
                    return//sellUpgrade
                } else {
                    if (player.hasSomething()) {
                        val selledFieldId = sellSomeField(player)
                        answer.fieldSellByHalf(player, game.board.fields[selledFieldId])
                        game.view.fieldSelledProperty.value = selledFieldId
                        punisment(player)
                        return//sellField
                    }
                }
            }
            else -> {
                if (player.hasSomeNotMonopoly()) {
                    val selledFieldId = sellSomeNotMonopolyField(player)
                    answer.fieldSellByHalf(player, game.board.fields[selledFieldId])
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
                        answer.fieldSellByHalf(player, game.board.fields[selledFieldId])
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

    private fun sellUpgrade(player: Player, field: Field) {
        player.moneyChange(field.upgradeCost)
        field.upgrade--
        field.penaltyUpdate()
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

    private fun calculateBenefitOfField(field: Field, owner: Player): Double {
        val players = game.data.filter { game.data.indexOf(it) !in game.loosers && it.id != owner.id }
        val analysis = AnalysisGame()
        val fieldEffectiveness = analysis.fieldEffectiveness
        val chanceDiceDrop = analysis.chanceDiceDrop
        var benefit = -field.cost.toDouble()

        for (other in players) {//earnings
            val newPosition = other.position
            for (first in 2..8) {//first mov
                val firstMovePos = (newPosition + first) % 28
                val firstField = game.board.fields[firstMovePos]
                if (firstField in owner.realty) benefit += chanceDiceDrop[first] * firstField.penalty

                for (second in 2..8) {
                    val secondMovePos = (firstMovePos + second) % 28
                    val secondField = game.board.fields[secondMovePos]
                    if (secondField in owner.realty) benefit += chanceDiceDrop[first] * secondField.penalty *
                            chanceDiceDrop[second]
                }
            }
        }
        for (first in 2..8) {//loss
            val firstDouble =
                first == 2 || (first == 4 && (1..2).random() == 1) || (first == 6 && (1..2).random() == 1) || first == 8
            val firstMovePos = (owner.position + first) % 28
            val firstField = game.board.fields[firstMovePos]
            if (firstField.type == Type.STONKS) benefit += 3000 * chanceDiceDrop[first]
            if (firstField.type == Type.TOPRISON) benefit -= 500 * chanceDiceDrop[first]
            if ((firstField.owner != null && firstField.owner!!.id != owner.id) || firstField.type == Type.PUNISMENT) {
                benefit -= firstField.penalty * chanceDiceDrop[first]
            }

            for (second in 2..8) {
                val secondDouble =
                    second == 2 || (second == 4 && (1..2).random() == 1) || (second == 6 && (1..2).random() == 1) || second == 8
                val secondMovePos = (firstMovePos + second) % 28
                val secondField = game.board.fields[secondMovePos]
                if (secondField.type == Type.STONKS) benefit += 3000 * chanceDiceDrop[first] * chanceDiceDrop[second]
                if (secondField.type == Type.TOPRISON) benefit -= 500 * chanceDiceDrop[first] * chanceDiceDrop[second]
                if ((secondField.owner != null && secondField.owner!!.id != owner.id) || secondField.type == Type.PUNISMENT) {
                    benefit -= secondField.penalty * chanceDiceDrop[first] * chanceDiceDrop[second]
                }

                for (third in 2..8) {
                    val thirdDouble =
                        third == 2 || (third == 4 && (1..2).random() == 1) || (third == 6 && (1..2).random() == 1) || third == 8
                    if (firstDouble && secondDouble && thirdDouble) {
                        benefit -= 500 * chanceDiceDrop[first] * chanceDiceDrop[second] * chanceDiceDrop[third]
                    }
                }
            }
        }

        return benefit
    }

    private fun calculateCurrentBenefitOfFields(
        player: Player,
        exception: Type?,
        monopoly: Boolean,
        default: Boolean
    ): MutableMap<Int, Double> {
        val analysis = AnalysisGame()
        val fieldEffectiveness = analysis.fieldEffectiveness
        val chanceDiceDrop = analysis.chanceDiceDrop
        val benefits = mutableMapOf<Int, Double>()
        val otherPlayers = game.data.filter { game.data.indexOf(it) !in game.loosers && it.id != player.id }
        if (monopoly) {
            player.realty.filter { it.type in player.monopolyRealty }.map {
                benefits[it.location] = 0.0
            }
        }
        if (default) {
            player.realty.filter { it.type !in player.monopolyRealty }.map {
                benefits[it.location] = 0.0
                if (playerNearlyHasMonopoly(player, it)) benefits[it.location] = 1250.0
                else if (somePlayerNearlyHasMonopoly(null, it)) benefits[it.location] = 750.0
            }
        }


        for (other in otherPlayers) {
            val newPosition = other.position
            for (first in 2..8) {//first mov
                val firstMovePos = (newPosition + first) % 28
                val field = game.board.fields[firstMovePos]
                if (field.location in benefits.keys) benefits[firstMovePos] =
                    benefits[firstMovePos]!! + chanceDiceDrop[first] * field.penalty * fieldEffectiveness[firstMovePos]

                for (second in 2..8) {
                    val secondMovePos = (firstMovePos + second) % 28
                    val secondField = game.board.fields[secondMovePos]
                    if (secondField.location in benefits.keys) benefits[secondMovePos] =
                        benefits[secondMovePos]!! + chanceDiceDrop[first] * secondField.penalty * fieldEffectiveness[secondMovePos] *
                                chanceDiceDrop[second]
                }
            }
        }
        if (exception != null) {
            val tmp = player.realty.filter { it.type == exception }
            tmp.map { if (it.location in benefits.keys) benefits.remove(it.location) }
        }
        return benefits
    }


}