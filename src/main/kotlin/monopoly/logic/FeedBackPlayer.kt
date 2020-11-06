package monopoly.logic

import tornadofx.runAsync
import tornadofx.ui

class FeedBackPlayer(val game: Game) {
    private val view = game.view
    private val board = game.board
    private val dice = game.dice

    fun negativePay(player: Player): Boolean {
        when (dice.secret.second) {
            SecretAction.ACTION1 -> if (player.money >= 300) {
                player.moneyChange(-300)
                return true
            }
            SecretAction.ACTION2 -> if (player.money >= 500) {
                player.moneyChange(-500)
                return true
            }
            SecretAction.ACTION3 -> if (player.money >= 40) {
                player.moneyChange(-40)
                return true
            }
            SecretAction.ACTION4 -> if (player.money >= 750) {
                player.moneyChange(-750)
                return true
            }
            else -> if (player.money >= 250) {
                player.moneyChange(-250)
                return true
            }
        }
        return false
    }

    fun prisonPay(player: Player): Int {
        if (player.prisonDays == 4 && player.money >= 750) {
            player.prisonDays = 0
            player.moneyChange(-750)
            player.justOutJail = true
            view.notifyInView.value = ("${player.name} выходит из тюрьмы, заплатив 750$.")
            game.endMotion()
            return 1
        }
        if (player.prisonDays != 4 && player.money >= 500) {
            player.prisonDays = 0
            player.moneyChange(-500)
            player.justOutJail = true
            view.notifyInView.value = ("${player.name} выходит из тюрьмы, заплатив 500$.")
            game.endMotion()
            return 2
        }
        return -1
    }

    fun prisonTryLogic(player: Player) {
        dice.roll()
        runAsync { if (game.delay) Thread.sleep(600) } ui {
            if (prisonTry(player)) {
                view.notifyInView.value = ("${player.name} выходит из тюрьмы, выбив дубль!")
                game.playerMove(player)
            } else {
                if (4 - player.prisonDays + 1 != 1)
                    view.notifyInView.value =
                        ("Игрок остается в тюрьме еще на ${4 - player.prisonDays + 1} хода.")
                else
                    view.notifyInView.value = ("Всего один ход отлучает ${player.name}, от свободы!")
                game.endMotion()
            }
        }
    }

    private fun prisonTry(player: Player): Boolean {
        if (dice.double) {
            player.prisonDays = 0
            dice.double = false
            return true
        }
        player.prisonDays++
        return false
    }

    fun fieldSellByHalf(player: Player, field: Field) {
        player.moneyChange(field.cost / 2)
        player.realty.remove(field)
        field.ownerUpdate(null)
        field.upgrade = 0
        player.checkForMonopoly(field)
        field.penaltyUpdate()
    }

    fun playerAcceptBuyRealty(player: Player): Boolean {
        if (player.money >= board.fields[player.position].cost) {
            player.moneyChange(-board.fields[player.position].cost)
            player.realty.add(board.fields[player.position])
            board.fields[player.position].ownerUpdate(player)
            player.checkForMonopoly(board.fields[player.position])
            board.fields[player.position].penaltyUpdate()
            return true
        }
        return false
    }

    fun playerPayPenalty(player: Player): Boolean {
        if (player.money >= board.fields[player.position].penalty) {
            player.moneyChange(-board.fields[player.position].penalty)
            if (board.fields[player.position].type != Type.PUNISHMENT) {
                board.fields[player.position].owner!!.moneyChange(board.fields[player.position].penalty)
            }
            return true
        }
        return false
    }
}