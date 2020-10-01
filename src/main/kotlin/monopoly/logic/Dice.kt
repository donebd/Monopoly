package monopoly.logic

import javafx.beans.property.SimpleIntegerProperty

class Dice{
    var count = 0
    var first = 0
    var checkRollProperty = SimpleIntegerProperty()
    var second = 0
    var double = false
    var secret = Pair(true,SecretAction.Action1)

    init {
        secretAction()
    }

    fun roll() {
        first = (1..4).random()
        second = (1..4).random()
        checkRollProperty.value++
        count = second + first
        double = first == second
        double = true
    }

    fun secretAction() {
        val answer1 = (1..3).random() == 1
        val answer2 = when((1..5).random()){
            1 -> SecretAction.Action1
            2 -> SecretAction.Action2
            3 -> SecretAction.Action3
            4 -> SecretAction.Action4
            else -> SecretAction.Action5
        }
        secret = Pair(answer1, answer2)
    }
}