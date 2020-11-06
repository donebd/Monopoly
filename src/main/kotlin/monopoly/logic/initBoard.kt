package monopoly.logic

import java.io.FileInputStream
import java.util.*

fun initBoard(board: GameBoard) {
    val prop = Properties()
    val input = FileInputStream("src/main/resources/monopoly/config/board.property")
    prop.load(input)
    val keys = prop.keys.sortedBy { it.toString().toInt() }
    for (key in keys) {
        val properties = prop.getProperty(key as String).split("[", ",", "]").map { it.trim() }.filter { it != "" }
        val location = properties[0].toInt()
        val type = Type.valueOf(properties[1])
        val couldBuy = properties[2] != "false"
        val name = properties[3]
        val posX = properties[4].toDouble()
        val posY = properties[5].toDouble()
        board.fields.add(Field(location, type))
        board.fields[location].couldBuy = couldBuy
        board.fields[location].name = name
        board.fields[location].layoutX = posX
        board.fields[location].layoutY = posY
        if (type == Type.PUNISHMENT) {
            board.fields[location].penalty = 2000
        }
        if (properties.size != 6 ) {
            board.fields[location].particular = true
            board.fields[location].costUpdate(3000)
        }
    }
    input.close()
}