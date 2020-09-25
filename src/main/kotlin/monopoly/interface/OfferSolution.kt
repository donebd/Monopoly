package monopoly.`interface`

import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.control.TableView
import javafx.scene.control.TextField
import javafx.scene.layout.AnchorPane
import javafx.util.Duration
import tornadofx.*
import java.lang.NumberFormatException

class OfferSolution : Fragment(){

    override val root : AnchorPane by fxml()

    private val tableViewSender : TableView<monopoly.logic.Field> by fxid()
    private val tableViewReceiver : TableView<monopoly.logic.Field> by fxid()

    private val nameSender : Label by fxid()
    private val nameReceiver : Label by fxid()
    private val money1Lbl : Label by fxid()
    private val money2Lbl : Label by fxid()

    private val sendBtn : Button by fxid()

    init {
        nameSender.text = game.offerSender.name
        nameReceiver.text = game.offerReceiver.name
        tableViewSender.column("Поле", monopoly.logic.Field::nameProperty)
        tableViewSender.column("Стоимость", monopoly.logic.Field::costProperty)
        tableViewSender.items = game.offerReceiverList.asObservable()
        tableViewReceiver.column("Поле", monopoly.logic.Field::nameProperty)
        tableViewReceiver.column("Стоимость", monopoly.logic.Field::costProperty)
        tableViewReceiver.items = game.offerSenderList.asObservable()
        money1Lbl.text = "${game.offerMoneyReceiver}$"
        money2Lbl.text = "$${game.offerMoneySender}"
    }

    fun acceptOffer() {
        game.acceptOffer()
        gamePlay.updateColor(game.offerSender)
        gamePlay.updateColor(game.offerReceiver)
        exit()
    }

    fun rejectOffer() {
        exit()
    }

    fun exit(){
        game.offerPause = false
        close()
    }
}