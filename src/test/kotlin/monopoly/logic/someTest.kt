package monopoly.logic

import org.junit.Test
import org.junit.Assert.*

class someTest{

    @Test
    fun checkLogic(){
        val board = Game.GameBoard()
        assertEquals(28, board.fields.size)// check internal logic board size
        assertEquals(Type.Start, board.fields[0].type)

        val game = Game()
        game.motionPlayer = 0
        game.data[0].positionChange(1)// player 1 go to 1 field
        assertEquals(1,  game.data[0].position)

        game.playerAcceptBuyRealty()
        assertEquals(15000 - 2100,game.data[0].money)
        assertEquals(game.board.fields[1].owner, game.data[0])//player buy realty functional test
        assertEquals(true, game.data[0].realty.contains(game.board.fields[1]))

        game.data[0].positionChange(1)// player 1 go to 2 field
        assertEquals(game.board.fields[2].owner, null)
        game.playerAcceptBuyRealty()
        assertEquals(12900 - 2200,game.data[0].money)// player 1 buy 2 field and get monopoly

        game.click = 1
        game.playerClicked = 0// action with realty field 1
        assertEquals(true, game.playerHasMonopoly())// checking for a monopoly
        assertEquals(true, !game.fieldCantBeUpgraded())
        game.click = 2
        assertEquals(true, !game.fieldCantBeUpgraded())// monopoly fields can be upgraded

        assertEquals(false, game.canControl(3))// can player control not his field


        game.motionNext()
        game.endMotionLogic()
        assertEquals(1, game.motionPlayer)
        assertEquals(1, game.presentId)//internal logic of the move

        game.data[1].positionChange(28)// player 2 finish circle and stay on start field
        game.checkCircleComplete()
        assertEquals(15000+2000, game.data[1].money)//check game circle completed
        assertEquals(game.startAction(), true)
        assertEquals(17000+1000, game.data[1].money)//check start field
        game.data[1].positionChange(1)// player 2 go to player1's realty
        assertEquals(true, game.punishmentOrPenalty())
        assertEquals(false, game.ifPunishment())

        assertEquals(true, game.playerPayPenalty())
        assertEquals(18000 - 210,game.data[1].money)
        assertEquals(10700 + 210, game.data[0].money)//check field penalty work correctly

        game.data[1].positionChange(1)// player 2 go to player1's realty
        game.playerLose()
        assertEquals(true, game.gameIsEnd())
    }

}