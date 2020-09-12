package monopoly.`interface`

import javafx.scene.control.Label
import javafx.scene.layout.AnchorPane
import javafx.scene.text.Text
import monopoly.logic.SecretAction
import tornadofx.Fragment

class SomeActionAlert : Fragment(){
    override val root : AnchorPane by fxml()

    private val message : Text by fxid()

    private val player : Label by fxid()

    private val prisonText : Text by fxid()

    init {
        player.text =  game.data[game.presentId].name
        //prison
        if (game.data[game.presentId].playerInPrison()){
            prisonText.opacity = 1.0
            if (game.prisonByDouble){
                message.text = "Вы отправляетесь в тюрьму, за махинации с кубиками."
            }else{
                message.text = "Вы отправляетесь в тюрьму, за неуплату налогов."
            }
        }
        //stonks
        if (game.ifStonks()){
            message.text = "Вы выйграли 3000 в лотерее!"
        }
        //secret only positive
        if (game.ifSecret()){
            when(game.dice.secret.second){
                SecretAction.Action1 -> message.text = "Вы нашли в зимней куртке забытые 250"
                SecretAction.Action2 -> message.text = "Вы выйграли на ставках 500"
                SecretAction.Action3 -> message.text = "Вам вернули долг 300"
                SecretAction.Action4 -> message.text = "В банке произошла ошибка, на ваш счет перечислено 750"
                else -> message.text = "Ваша собака принесла вам 100"
            }
        }
        //start bonus
        if (game.ifStart()) message.text = "За попадание на поле СТАРТ, вы получаете 1000"
    }

    fun exit(){
        close()
    }
}