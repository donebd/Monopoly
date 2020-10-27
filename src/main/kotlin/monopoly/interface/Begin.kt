package monopoly.`interface`

import javafx.collections.FXCollections
import javafx.scene.Node
import javafx.scene.control.CheckBox
import javafx.scene.control.ComboBox
import javafx.scene.control.TextField
import javafx.scene.layout.BorderPane
import javafx.scene.layout.GridPane
import monopoly.logic.Difficulty
import monopoly.logic.Game
import monopoly.logic.Player
import tornadofx.*

class Begin : View("Monopoly") {

    override val root: BorderPane by fxml()

    private val playerPane: GridPane by fxid()

    fun howToPlay() {
        find<HowToPlay>().openModal(resizable = false)
    }

    private fun active(box: Node, num: Int) {
        val activeBox = box.getChildList()!![0] as CheckBox
        val playField = box.getChildList()!![1] as TextField
        val aiBox = box.getChildList()!![2] as CheckBox
        val aiDifficulty = box.getChildList()!![3] as ComboBox<*>
        if (activeBox.isSelected) {
            playField.disableProperty().value = false
            playField.text = "Player ${num + 1}"
            aiBox.disableProperty().value = false
        } else {
            playField.disableProperty().value = true
            aiBox.disableProperty().value = true
            aiBox.isSelected = false
            aiDifficulty.disableProperty().value = true
            aiDifficulty.value = "null"
            aiDifficulty.opacity = 0.0
            playField.text = ""
        }
    }

    private fun botAction(box: Node, num: Int) {
        val playField = box.getChildList()!![1] as TextField
        val aiBox = box.getChildList()!![2] as CheckBox
        val aiDifficulty = box.getChildList()!![3] as ComboBox<*>
        if (aiBox.isSelected) {
            playField.disableProperty().value = true
            playField.text = "EasyAI ${num + 1}}"
            aiDifficulty.disableProperty().value = false
            aiDifficulty.value = "Легкий"
            aiDifficulty.opacity = 1.0
        } else {
            playField.disableProperty().value = false
            playField.text = "Player ${num + 1}"
            aiDifficulty.disableProperty().value = true
            aiDifficulty.value = "null"
            aiDifficulty.opacity = 0.0
        }
    }

    private fun difficultyAct(box: Node, num: Int) {
        val playField = box.getChildList()!![1] as TextField
        val aiDifficulty = box.getChildList()!![3] as ComboBox<*>
        playField.text = when (aiDifficulty.value) {
            "Легкий" -> "EasyAI ${num + 1}"
            "Средний" -> "MediumAI ${num + 1}"
            "Сложный" -> "HardAI ${num + 1}"
            "Серьезный" -> "HardestAI ${num + 1}"
            else -> "Player ${num + 1}"
        }
    }

    init {
        primaryStage.width = 610.0
        primaryStage.height = 420.0
        primaryStage.centerOnScreen()
        for (i in 0..4) {
            val box = playerPane.children[i]
            (box.getChildList()!![0] as CheckBox).setOnAction { active(box, i) }
            (box.getChildList()!![2] as CheckBox).setOnAction { botAction(box, i) }
            (box.getChildList()!![3] as ComboBox<*>).items =
                FXCollections.observableArrayList("Легкий", "Средний", "Сложный", "Серьезный")
            (box.getChildList()!![3] as ComboBox<*>).setOnAction { difficultyAct(box, i) }
        }
    }

    fun startGame() {
        game = Game()

        game.data[0].name = (playerPane.children[0].getChildList()!![1] as TextField).text
        game.data[0].ai = (playerPane.children[0].getChildList()!![2] as CheckBox).isSelected
        game.data[0].aiDifficulty = when ((playerPane.children[0].getChildList()!![3] as ComboBox<*>).value) {
            "Легкий" -> Difficulty.EASY
            "Средний" -> Difficulty.MEDIUM
            "Сложный" -> Difficulty.HARD
            else -> Difficulty.HARDEST
        }

        for (i in 1..4) {
            val box = playerPane.children[i]
            val activeBox = box.getChildList()!![0] as CheckBox
            val playField = box.getChildList()!![1] as TextField
            val aiBox = box.getChildList()!![2] as CheckBox
            val aiDifficulty = box.getChildList()!![3] as ComboBox<*>
            if (activeBox.isSelected) {
                if (i != 1) {
                    game.data.add(Player(game.data.size + 1))
                }
                game.data.last().name = playField.text
                if (aiBox.isSelected) {
                    game.data.last().ai = true
                    game.data.last().aiDifficulty = when (aiDifficulty.value) {
                        "Легкий" -> Difficulty.EASY
                        "Средний" -> Difficulty.MEDIUM
                        "Сложный" -> Difficulty.HARD
                        else -> Difficulty.HARDEST
                    }
                }
            }
        }

        gamePlay.root.clear()
        gamePlay = GamePlay()
        replaceWith(gamePlay, ViewTransition.Explode(0.5.seconds))
    }

}