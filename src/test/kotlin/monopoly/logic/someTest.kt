package monopoly.logic

import org.junit.Test
import org.junit.Assert.*

class someTest{

    @Test
    fun checkSome(){
        val board = Game.GameBoard()
        assertEquals(28, board.fields.size)
        assertEquals(Type.Start, board.fields[0].type)
    }

}