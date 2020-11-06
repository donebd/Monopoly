package monopoly.logic

abstract class AI(val game: Game, val player: Player) {

    protected val answer = FeedBackPlayer(game)

    abstract fun instructions() : List<Int>

    abstract fun prisonInstructions()

    abstract fun buyInstructions()

    abstract fun negativeEvent()

    abstract fun punisment()

    protected fun sellSomeNotMonopolyField(): Int {
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

    protected fun sellSomeUpgrade(needSell: Boolean): Int {
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

    protected fun sellSomeField(): Int {
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

    protected fun onBoardHasBuyableFields(): Boolean {
        return game.board.fields.any { it.couldBuy && it.owner == null }
    }

    protected fun someOnBoardNearlyHasMonopoly(): Boolean { // функция для проверки необходимости покупки поля для руина монополии другому игроку
        for (player in game.data)
            if (playerNearlyHasMonopoly(player, game.board.fields[game.currentPlayer.position])) return true
        return false
    }

    protected fun playerNearlyHasMonopoly(player: Player, field: Field): Boolean {
        when (player.realty.filter { it.type == field.type }.size) {
            1 -> if (field.type == Type.PERFUME || field.type == Type.SOFTWARE || field.type == Type.SODA || field.type == Type.FASTFOOD || field.type == Type.NETWORK) return true
            2 -> if (field.type == Type.CLOTHES || field.type == Type.CAR || field.type == Type.AIRLINES) return true
            else -> return false
        }
        return false
    }

    protected fun playerNearlyHasSomeMonopoly(player: Player): Boolean {
        for (i in player.realty) if (playerNearlyHasMonopoly(player, i)) return true
        return false
    }

    protected fun calculateRealtyCost(player: Player): Int {// возвращает кол-во денег с продажи всех немонопольных полей и апгрейдов
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

    protected fun calculateUpgradeCost(player: Player): Int {
        var sum = 0
        for (i in player.monopolyRealty) {
            for (j in game.board.fields.filter { it.type == i })
                if (j.upgrade > 0) {
                    sum += j.upgrade * j.upgradeCost
                }
        }
        return sum
    }

    protected fun payNegative() : Boolean {
        when (game.dice.secret.second) {
            SecretAction.ACTION1 -> if (player.money >= 300) {
                answer.negativePay(player)
                game.endMotion()
                return true
            }
            SecretAction.ACTION2 -> if (player.money >= 500) {
                answer.negativePay(player)
                game.endMotion()
                return true
            }
            SecretAction.ACTION3 -> if (player.money >= 40) {
                answer.negativePay(player)
                game.endMotion()
                return true
            }
            SecretAction.ACTION4 -> if (player.money >= 750) {
                answer.negativePay(player)
                game.endMotion()
                return true
            }
            else -> if (player.money >= 250) {
                answer.negativePay(player)
                game.endMotion()
                return true
            }
        }
        return false
    }

    protected fun payPunishment() : Boolean {
        if (player.money >= game.board.fields[player.position].penalty) {
            answer.playerPayPenalty(player)
            game.endMotion()
            return true
        }
        return false
    }

    protected fun sellSomeOtherTypeField(player: Player): Int {
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