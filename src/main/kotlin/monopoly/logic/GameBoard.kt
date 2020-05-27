package monopoly.logic

class GameBoard{
    val fields = mutableListOf<Field>()

    init {
        initBoard(this)
    }
}