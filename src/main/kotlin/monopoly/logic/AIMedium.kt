package monopoly.logic

class AIMedium(game: Game, player: Player) : AI(game, player) {

    override fun instructions(): List<Int> {
        val tmp = mutableListOf<Int>()
        if ((1..100).random() in (1..85)) {
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

    override fun prisonInstructions() {
        if (!player.isPrisonPayDay()) {
            if (player.money >= 500 && player.monopolyRealty.isEmpty() && onBoardHasBuyableFields()) {
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
                val selledFieldId = sellSomeNotMonopolyField()
                answer.fieldSellByHalf(player, game.board.fields[selledFieldId])
                game.view.fieldSelledProperty.value = selledFieldId
                prisonInstructions()
                return//sellField
            }
            if (player.monopolyRealty.isNotEmpty() && sellSomeUpgrade(false) != -1) {
                val idDegradeField = sellSomeUpgrade(true)
                game.view.fieldDegradeProperty.value = idDegradeField
                game.triggerProperty(game.view.sellUpgradeViewProperty)
                prisonInstructions()
                return//sellUpgrade
            }
            if (player.hasSomething()) {
                val selledFieldId = sellSomeField()
                answer.fieldSellByHalf(player, game.board.fields[selledFieldId])
                game.view.fieldSelledProperty.value = selledFieldId
                prisonInstructions()
                return//sellField
            }
        }
        game.playerSurrender()
    }

    override fun buyInstructions() {
        if (player.money >= game.board.fields[player.position].cost && (player.monopolyRealty.isEmpty() || someOnBoardNearlyHasMonopoly())) {
            answer.playerAcceptBuyRealty(player)
            game.triggerProperty(game.view.fieldBoughtProperty)
            return//buy
        }
    }

    override fun negativeEvent() {
        if (payNegative()) return
        if (player.monopolyRealty.isNotEmpty() && sellSomeUpgrade(false) != -1) {
            val idDegradeField = sellSomeUpgrade(true)
            game.view.fieldDegradeProperty.value = idDegradeField
            game.triggerProperty(game.view.sellUpgradeViewProperty)
            negativeEvent()
            return//sellUpgrade
        } else {
            if (player.hasSomething()) {
                val selledFieldId = sellSomeField()
                answer.fieldSellByHalf(player, game.board.fields[selledFieldId])
                game.view.fieldSelledProperty.value = selledFieldId
                negativeEvent()
                return//sellField
            }
        }
        game.playerSurrender()
    }

    override fun punisment() {
        if (payPunishment()) return
        if (player.monopolyRealty.isNotEmpty() && sellSomeUpgrade(false) != -1) {
            val idDegradeField = sellSomeUpgrade(true)
            game.view.fieldDegradeProperty.value = idDegradeField
            game.triggerProperty(game.view.sellUpgradeViewProperty)
            punisment()
            return//sellUpgrade
        } else {
            if (player.hasSomething()) {
                val selledFieldId = sellSomeField()
                answer.fieldSellByHalf(player, game.board.fields[selledFieldId])
                game.view.fieldSelledProperty.value = selledFieldId
                punisment()
                return//sellField
            }
        }
        game.playerSurrender()
    }

}