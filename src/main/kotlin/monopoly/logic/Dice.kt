package monopoly.logic

import javafx.beans.property.SimpleIntegerProperty

class Dice {
    var count = 0
    var first = 0
    var checkRollProperty = SimpleIntegerProperty()
    var second = 0
    var double = false
    var secret = Pair(true, SecretAction.ACTION1)

    init {
        secretAction()
    }

    fun roll() {
        first = (1..4).random()
        second = (1..4).random()
        checkRollProperty.value++
        count = second + first
        double = first == second
    }

    fun secretAction() {
        val answer1 = (1..3).random() == 1
        val answer2 = when ((1..5).random()) {
            1 -> SecretAction.ACTION1
            2 -> SecretAction.ACTION2
            3 -> SecretAction.ACTION3
            4 -> SecretAction.ACTION4
            else -> SecretAction.ACTION5
        }
        secret = Pair(answer1, answer2)
    }
}