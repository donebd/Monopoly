package monopoly.logic

class AIHardest(game: Game, player: Player) : AI(game, player) {

    override fun instructions(): List<Int> {
        val tmp = mutableListOf<Int>()
        if (player.monopolyRealty.isNotEmpty()) {
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
        return tmp
    }

    override fun prisonInstructions() {
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
                if (player.monopolyRealty.isNotEmpty() && sellSomeUpgrade(false) != -1) {
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
                            prisonInstructions()
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
                    prisonInstructions()
                    return//sellField
                }
            }
        }
        game.playerSurrender()
    }

    override fun buyInstructions() {
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
            if (player.monopolyRealty.isNotEmpty() && sellSomeUpgrade(false) != -1) {
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
                        buyInstructions()
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
                buyInstructions()
                return//sellField
            }
        }
    }

    override fun negativeEvent() {
        if (payNegative()) return
        if (player.realty.isNotEmpty()) {
            if (player.monopolyRealty.isNotEmpty() && sellSomeUpgrade(false) != -1) {
                val benefits = calculateCurrentBenefitOfFields(player, null, monopoly = true, default = false)
                var minValuableField: Field
                while (benefits.isNotEmpty()) {
                    minValuableField = game.board.fields[benefits.minBy { it.value }?.key ?: -1]
                    benefits.remove(minValuableField.location)
                    if (minValuableField.upgrade != 0) {
                        sellUpgrade(player, minValuableField)
                        game.view.fieldDegradeProperty.value = minValuableField.location
                        game.triggerProperty(game.view.sellUpgradeViewProperty)
                        negativeEvent()
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
                negativeEvent()
                return//sellField
            }
        }
        game.playerSurrender()
    }

    override fun punisment() {
        if (payPunishment()) return
        if (player.realty.isNotEmpty()) {
            if (player.monopolyRealty.isNotEmpty() && sellSomeUpgrade(false) != -1) {
                val benefits = calculateCurrentBenefitOfFields(player, null, monopoly = true, default = false)
                var minValuableField: Field
                while (benefits.isNotEmpty()) {
                    minValuableField = game.board.fields[benefits.minBy { it.value }?.key ?: -1]
                    benefits.remove(minValuableField.location)
                    if (minValuableField.upgrade != 0) {
                        sellUpgrade(player, minValuableField)
                        game.view.fieldDegradeProperty.value = minValuableField.location
                        game.triggerProperty(game.view.sellUpgradeViewProperty)
                        punisment()
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
                punisment()
                return//sellField
            }
        }
        game.playerSurrender()
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

    private fun sellUpgrade(player: Player, field: Field) {
        player.moneyChange(field.upgradeCost)
        field.upgrade--
        field.penaltyUpdate()
    }

    private fun calculateBenefitOfField(field: Field, owner: Player): Double {
        val players = game.data.filter { game.data.indexOf(it) !in game.loosers && it.id != owner.id }
        val analysis = AnalysisGame()
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
            if ((firstField.owner != null && firstField.owner!!.id != owner.id) || firstField.type == Type.PUNISHMENT) {
                benefit -= firstField.penalty * chanceDiceDrop[first]
            }

            for (second in 2..8) {
                val secondDouble =
                    second == 2 || (second == 4 && (1..2).random() == 1) || (second == 6 && (1..2).random() == 1) || second == 8
                val secondMovePos = (firstMovePos + second) % 28
                val secondField = game.board.fields[secondMovePos]
                if (secondField.type == Type.STONKS) benefit += 3000 * chanceDiceDrop[first] * chanceDiceDrop[second]
                if (secondField.type == Type.TOPRISON) benefit -= 500 * chanceDiceDrop[first] * chanceDiceDrop[second]
                if ((secondField.owner != null && secondField.owner!!.id != owner.id) || secondField.type == Type.PUNISHMENT) {
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

    private fun playerNotNeedUpgrade(player: Player): Boolean {
        val monopoly = player.realty.filter { it.type in player.monopolyRealty }
        for (field in monopoly) {
            if (field.upgrade != 5) return false
        }
        return true
    }
}