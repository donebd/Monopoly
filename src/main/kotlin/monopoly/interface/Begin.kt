package monopoly.`interface`

import javafx.collections.FXCollections
import javafx.scene.control.CheckBox
import javafx.scene.control.ComboBox
import javafx.scene.control.TextField
import javafx.scene.layout.BorderPane
import monopoly.logic.Difficulty
import monopoly.logic.Game
import monopoly.logic.Player
import tornadofx.*

class Begin : View("Monopoly"){

    override val root : BorderPane by fxml()

    private val playfield1 : TextField by fxid()

    private val playfield2 : TextField by fxid()

    private val playfield3 : TextField by fxid()

    private val playfield4 : TextField by fxid()

    private val playfield5 : TextField by fxid()

    private val botCheck1 : CheckBox by fxid()

    private val botCheck2 : CheckBox by fxid()

    private val botCheck3 : CheckBox by fxid()

    private val botCheck4 : CheckBox by fxid()

    private val botCheck5 : CheckBox by fxid()

    private val player3Check : CheckBox by fxid()

    private val player4Check : CheckBox by fxid()

    private val player5Check : CheckBox by fxid()

    private val difficultyBox1 : ComboBox<String> by fxid()

    private val difficultyBox2 : ComboBox<String> by fxid()

    private val difficultyBox3 : ComboBox<String> by fxid()

    private val difficultyBox4 : ComboBox<String> by fxid()

    private val difficultyBox5 : ComboBox<String> by fxid()

    fun howToPlay(){
        find<HowToPlay>().openModal(resizable =  false)
    }

    fun active3(){
        if (player3Check.isSelected) {
            playfield3.disableProperty().value = false
            playfield3.text = "Player 3"
            botCheck3.disableProperty().value = false
        }
        else {
            playfield3.disableProperty().value = true
            playfield3.text = ""
            botCheck3.disableProperty().value = true
            botCheck3.isSelected = false
        }
    }

    fun active4(){
        if (player4Check.isSelected) {
            playfield4.disableProperty().value = false
            playfield4.text = "Player 4"
            botCheck4.disableProperty().value = false
        }
        else {
            playfield4.disableProperty().value = true
            playfield4.text = ""
            botCheck4.disableProperty().value = true
            botCheck4.isSelected = false
        }
    }

    fun active5(){
        if (player5Check.isSelected) {
            playfield5.disableProperty().value = false
            playfield5.text = "Player 5"
            botCheck5.disableProperty().value = false
        }
        else {
            playfield5.disableProperty().value = true
            playfield5.text = ""
            botCheck5.disableProperty().value = true
            botCheck5.isSelected = false
        }
    }

    fun botAction1(){
        if (botCheck1.isSelected){
            playfield1.disableProperty().value = true
            playfield1.text = "EasyAI 1"
            difficultyBox1.disableProperty().value = false
            difficultyBox1.value = "Легкий"
            difficultyBox1.opacity = 1.0
        } else {
            playfield1.disableProperty().value = false
            playfield1.text = "Player 1"
            difficultyBox1.disableProperty().value = true
            difficultyBox1.value = "null"
            difficultyBox1.opacity = 0.0
        }
    }
    fun botAction2(){
        if (botCheck2.isSelected){
            playfield2.disableProperty().value = true
            playfield2.text = "EasyAI 2"
            difficultyBox2.disableProperty().value = false
            difficultyBox2.value = "Легкий"
            difficultyBox2.opacity = 1.0
        } else {
            playfield2.disableProperty().value = false
            playfield2.text = "Player 2"
            difficultyBox2.disableProperty().value = true
            difficultyBox2.value = "null"
            difficultyBox2.opacity = 0.0
        }
    }

    fun botAction3(){
        if (botCheck3.isSelected){
            playfield3.disableProperty().value = true
            playfield3.text = "EasyAI 3"
            difficultyBox3.disableProperty().value = false
            difficultyBox3.value = "Легкий"
            difficultyBox3.opacity = 1.0
        } else {
            playfield3.disableProperty().value = false
            playfield3.text = "Player 3"
            difficultyBox3.disableProperty().value = true
            difficultyBox3.value = "null"
            difficultyBox3.opacity = 0.0
        }
    }

    fun botAction4(){
        if (botCheck4.isSelected){
            playfield4.disableProperty().value = true
            playfield4.text = "EasyAI 4"
            difficultyBox4.disableProperty().value = false
            difficultyBox4.value = "Легкий"
            difficultyBox4.opacity = 1.0
        } else {
            playfield4.disableProperty().value = false
            playfield4.text = "Player 4"
            difficultyBox4.disableProperty().value = true
            difficultyBox4.value = "null"
            difficultyBox4.opacity = 0.0
        }
    }

    fun botAction5(){
        if (botCheck5.isSelected){
            playfield5.disableProperty().value = true
            playfield5.text = "EasyAI 5"
            difficultyBox5.disableProperty().value = false
            difficultyBox5.value = "Легкий"
            difficultyBox5.opacity = 1.0
        } else {
            playfield5.disableProperty().value = false
            playfield5.text = "Player 5"
            difficultyBox5.disableProperty().value = true
            difficultyBox5.value = "null"
            difficultyBox5.opacity = 0.0
        }
    }

    fun difficultyAct1(){
        playfield1.text = when(difficultyBox1.value){
            "Легкий" -> "EasyAI 1"
            "Средний" -> "MediumAI 1"
            "0 Ошибок" -> "HardAI 1"
            else ->  "Player 1"
        }
    }

    fun difficultyAct2(){
        playfield2.text = when(difficultyBox2.value){
            "Легкий" -> "EasyAI 2"
            "Средний" -> "MediumAI 2"
            "0 Ошибок" -> "HardAI 2"
            else ->  "Player 2"
        }
    }

    fun difficultyAct3(){
        playfield3.text = when(difficultyBox3.value){
            "Легкий" -> "EasyAI 3"
            "Средний" -> "MediumAI 3"
            "0 Ошибок" -> "HardAI 3"
            else ->  "Player 3"
        }
    }

    fun difficultyAct4(){
        playfield4.text = when(difficultyBox4.value){
            "Легкий" -> "EasyAI 4"
            "Средний" -> "MediumAI 4"
            "0 Ошибок" -> "HardAI 4"
            else ->  "Player 4"
        }
    }

    fun difficultyAct5(){
        playfield5.text = when(difficultyBox5.value){
            "Легкий" -> "EasyAI 5"
            "Средний" -> "MediumAI 5"
            "0 Ошибок" -> "HardAI 5"
            else ->  "Player 5"
        }
    }

    init {
        primaryStage.width = 610.0
        primaryStage.height = 420.0
        primaryStage.centerOnScreen()
        difficultyBox1.items = FXCollections.observableArrayList("Легкий", "Средний", "0 Ошибок")
        difficultyBox2.items = FXCollections.observableArrayList("Легкий", "Средний", "0 Ошибок")
        difficultyBox3.items = FXCollections.observableArrayList("Легкий", "Средний", "0 Ошибок")
        difficultyBox4.items = FXCollections.observableArrayList("Легкий", "Средний", "0 Ошибок")
        difficultyBox5.items = FXCollections.observableArrayList("Легкий", "Средний", "0 Ошибок")
    }

    fun startGame(){
        game = Game()

        game.data[0].name = playfield1.text
        game.data[1].name = playfield2.text

        if (botCheck1.isSelected){
            game.data[0].ai = true
            when (difficultyBox1.value){
                "Легкий" -> game.data[0].aiDifficulty = Difficulty.Easy
                "Средний" -> game.data[0].aiDifficulty = Difficulty.Medium
                else -> game.data[0].aiDifficulty = Difficulty.Hard
            }
        }

        if (botCheck2.isSelected){
            game.data[1].ai = true
            when (difficultyBox2.value){
                "Легкий" -> game.data[1].aiDifficulty = Difficulty.Easy
                "Средний" -> game.data[1].aiDifficulty = Difficulty.Medium
                else -> game.data[1].aiDifficulty = Difficulty.Hard
            }
        }

        if (player3Check.isSelected){
            game.data.add(Player(game.data.size + 1))
            game.data.last().name = playfield3.text
            if (botCheck3.isSelected){
                game.data.last().ai = true
                when (difficultyBox3.value){
                    "Легкий" -> game.data.last().aiDifficulty = Difficulty.Easy
                    "Средний" -> game.data.last().aiDifficulty = Difficulty.Medium
                    else -> game.data.last().aiDifficulty = Difficulty.Hard
                }
            }
        }

        if (player4Check.isSelected){
            game.data.add(Player(game.data.size + 1))
            game.data.last().name = playfield4.text
            if (botCheck4.isSelected){
                game.data.last().ai = true
                when (difficultyBox4.value){
                    "Легкий" -> game.data.last().aiDifficulty = Difficulty.Easy
                    "Средний" -> game.data.last().aiDifficulty = Difficulty.Medium
                    else -> game.data.last().aiDifficulty = Difficulty.Hard
                }
            }
        }

        if (player5Check.isSelected){
            game.data.add(Player(game.data.size + 1))
            game.data.last().name = playfield5.text
            if (botCheck5.isSelected){
                game.data.last().ai = true
                when (difficultyBox5.value){
                    "Легкий" -> game.data.last().aiDifficulty = Difficulty.Easy
                    "Средний" -> game.data.last().aiDifficulty = Difficulty.Medium
                    else -> game.data.last().aiDifficulty = Difficulty.Hard
                }
            }
        }
        gamePlay.root.clear()
        gamePlay = GamePlay()
        replaceWith(gamePlay, ViewTransition.Explode(0.5.seconds))
    }

}