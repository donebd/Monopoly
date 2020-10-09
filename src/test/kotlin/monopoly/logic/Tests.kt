package monopoly.logic

import org.junit.Test
import org.junit.Assert.*

class Tests{

    @Test
    fun boardLogic(){
        val board = GameBoard()
        assertEquals(28, board.fields.size)
        assertEquals(Type.Start, board.fields[0].type)
    }

    @Test
    fun checkPositionChange(){
        val game = Game()
        game.motionPlayer = 0
        game.data[0].positionChange(1)
        assertEquals(1,  game.data[0].position)
    }

    @Test
    fun checkBuyRealty(){
        val game = Game()
        game.data[0].positionChange(1)
        game.playerAnswer.playerAcceptBuyRealty(game.data[0])
        assertEquals(15000 - 2100,game.data[0].money)
        assertEquals(game.board.fields[1].owner, game.data[0])
        assertEquals(true, game.data[0].realty.contains(game.board.fields[1]))
    }

    @Test
    fun checkMonopolyFunctional(){
        val game = Game()
        game.data[0].positionChange(1)
        game.playerAnswer.playerAcceptBuyRealty(game.data[0])
        game.data[0].positionChange(1)
        assertEquals(game.board.fields[2].owner, null)
        game.playerAnswer.playerAcceptBuyRealty(game.data[0])
        assertEquals(12900 - 2200,game.data[0].money)// player 1 buy 2 field and get monopoly
        assertEquals(true, game.canControl(1))
        assertEquals(true, game.data[0].hasMonopoly(game.board.fields[1]))// checking for a monopoly
        assertEquals(true, !game.currentAct.fieldCantBeUpgraded())
        assertEquals(true, game.canControl(2))
        assertEquals(true, !game.currentAct.fieldCantBeUpgraded())// monopoly fields can be upgraded

        assertEquals(false, game.canControl(3))// can player control not his field
    }


    @Test
    fun checkUpgradeField(){
        val game = Game()
        game.data[0].positionChange(1)
        game.playerAnswer.playerAcceptBuyRealty(game.data[0])
        game.data[0].positionChange(1)
        game.playerAnswer.playerAcceptBuyRealty(game.data[0])
        assertEquals(true, game.canControl(2))
        game.currentAct.fieldBuildUpgrade()
        assertEquals(10700 - 1100,game.data[0].money)
        assertEquals(1,game.board.fields[2].upgrade)
        println(game.board.fields[1].type)
        assertEquals(Type.Perfume, game.data[0].currentMotionUpgrade[0])
        game.currentAct.fieldSellUpgrade()
        assertEquals(10700 ,game.data[0].money)
        assertEquals(0,game.board.fields[0].upgrade)
    }

   @Test
    fun checkCircleComplete(){
        val game = Game()
        game.data[0].positionChange(28)
        game.event.checkCircleComplete()
        assertEquals(15000+2000, game.data[0].money)//check game circle completed
        assertEquals(game.event.startAction(), true)
        assertEquals(17000+1000, game.data[0].money)//check start field
    }

    @Test
    fun checkPenaltyLogic(){
        val game = Game()
        game.data[0].positionChange(1)
        game.playerAnswer.playerAcceptBuyRealty(game.data[0])
        game.data[0].positionChange(1)
        game.playerAnswer.playerAcceptBuyRealty(game.data[0])
        game.endMotionLogic()
        assertEquals(1, game.data.indexOf(game.currentPlayer))
        assertEquals(2, game.currentPlayer.id)//internal logic of the move

        game.data[1].positionChange(1)// player 2 go to player1's realty
        assertEquals(true, game.event.punishmentOrPenalty(game.data[1]))
        assertEquals(false, game.event.ifPunishment(game.data[1]))
        assertEquals(true, game.playerAnswer.playerPayPenalty(game.data[1]))
        assertEquals(15000 - 700,game.data[1].money)
        assertEquals(10700 + 700, game.data[0].money)//check field penalty work correctly

        game.data[1].positionChange(1)// player 2 go to player1's realty
        game.playerLose(game.data[1])
        assertEquals(true, game.gameIsEnd())
    }
}