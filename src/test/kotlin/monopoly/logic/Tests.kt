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
        game.playerAcceptBuyRealty()
        assertEquals(15000 - 2100,game.data[0].money)
        assertEquals(game.board.fields[1].owner, game.data[0])
        assertEquals(true, game.data[0].realty.contains(game.board.fields[1]))
    }

    @Test
    fun checkMonopolyFunctional(){
        val game = Game()
        game.data[0].positionChange(1)
        game.playerAcceptBuyRealty()
        game.data[0].positionChange(1)
        assertEquals(game.board.fields[2].owner, null)
        game.playerAcceptBuyRealty()
        assertEquals(12900 - 2200,game.data[0].money)// player 1 buy 2 field and get monopoly

        assertEquals(true, game.canControl(1))
        assertEquals(true, game.playerHasMonopoly())// checking for a monopoly
        assertEquals(true, !game.fieldCantBeUpgraded())
        assertEquals(true, game.canControl(2))
        assertEquals(true, !game.fieldCantBeUpgraded())// monopoly fields can be upgraded

        assertEquals(false, game.canControl(3))// can player control not his field
    }

    @Test
    fun checkUpgradeField(){
        val game = Game()
        game.data[0].positionChange(1)
        game.playerAcceptBuyRealty()
        game.data[0].positionChange(1)
        game.playerAcceptBuyRealty()
        assertEquals(true, game.canControl(2))
        game.fieldActionInit()
        game.fieldBuildUpgrade()
        assertEquals(10700 - 733,game.data[0].money)
        assertEquals(1,game.board.fields[2].upgrade)
        assertEquals(Type.Perfume, game.data[0].currentMotionUpgrade[0])
        game.fieldSellUpgrade()
        assertEquals(10700 ,game.data[0].money)
        assertEquals(0,game.board.fields[0].upgrade)
    }

    @Test
    fun checkCircleComplete(){
        val game = Game()
        game.data[0].positionChange(28)
        game.checkCircleComplete()
        assertEquals(15000+2000, game.data[0].money)//check game circle completed
        assertEquals(game.startAction(), true)
        assertEquals(17000+1000, game.data[0].money)//check start field
    }

    @Test
    fun checPenaltykLogic(){
        val game = Game()
        game.data[0].positionChange(1)
        game.playerAcceptBuyRealty()
        game.data[0].positionChange(1)
        game.playerAcceptBuyRealty()
        game.motionNext()
        game.endMotionLogic()
        assertEquals(1, game.motionPlayer)
        assertEquals(1, game.presentId)//internal logic of the move

        game.data[1].positionChange(1)// player 2 go to player1's realty
        assertEquals(true, game.punishmentOrPenalty())
        assertEquals(false, game.ifPunishment())
        assertEquals(true, game.playerPayPenalty())
        assertEquals(15000 - 210,game.data[1].money)
        assertEquals(10700 + 210, game.data[0].money)//check field penalty work correctly

        game.data[1].positionChange(1)// player 2 go to player1's realty
        game.playerLose()
        assertEquals(true, game.gameIsEnd())
    }

    @Test
    fun checkGameEnd(){
        val game = Game()
        game.data[0].positionChange(1)
        game.playerAcceptBuyRealty()
        game.motionNext()
        game.endMotionLogic()
        game.data[1].positionChange(1)
        game.playerLose()
        assertEquals(true, game.gameIsEnd())
    }

}