package monopoly.`interface`

import javafx.scene.control.Label
import javafx.scene.layout.AnchorPane
import javafx.scene.text.Text
import monopoly.logic.Game
import monopoly.logic.SecretAction
import monopoly.logic.Type
import tornadofx.Fragment

class SomeActionAlert : Fragment(){
    override val root : AnchorPane by fxml()

    private val message : Text by fxid()

    private val player : Label by fxid()

    private val prisonText : Text by fxid()

    init {
        player.text = data[gamePlay.presentId].name
        //prison
        if (data[gamePlay.presentId].playerInPrison()){
            prisonText.opacity = 1.0
            if (data[gamePlay.presentId].doubleInARow == 2){
                message.text = "Вы отправляетесь в тюрьму, за махинации с кубиками."
            }else{
                message.text = "Вы отправляетесь в тюрьму, за неуплату налогов."
            }
        }
        //stonks
        if (data[gamePlay.presentId].position == 23){
            message.text = "Вы выйграли 3000 в лотерее!"
        }
        //secret only positive
        if (board.fields[data[gamePlay.presentId].position].type == Type.Secret){
            when(Game.Dice().secret.second){
                SecretAction.Action1 -> {
                    data[gamePlay.presentId].moneyChange(250)
                    message.text = "Вы нашли в зимней куртке забытые 250"
                }
                SecretAction.Action2 -> {
                    data[gamePlay.presentId].moneyChange(500)
                    message.text = "Вы выйграли на ставках 500"
                }
                SecretAction.Action3 -> {
                    data[gamePlay.presentId].moneyChange(300)
                    message.text = "Вам вернули долг 300"
                }
                SecretAction.Action4 -> {
                    data[gamePlay.presentId].moneyChange(750)
                    message.text = "В банке произошла ошибка, на ваш счет перечислено 750"
                }
                else -> {
                    data[gamePlay.presentId].moneyChange(100)
                    message.text = "Ваша собака принесла вам 100"
                }
            }
        }
        //start bonus
        if (data[gamePlay.presentId].position == 0) message.text = "За попадание на поле СТАРТ, вы получаете 1000"
    }

    fun exit(){
        close()
    }
}