package monopoly.`interface`

import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.control.TableView
import javafx.scene.control.TextField
import javafx.scene.layout.AnchorPane
import javafx.util.Duration
import tornadofx.*
import java.lang.NumberFormatException

class ExchangeSolution : Fragment(){

    override val root : AnchorPane by fxml()

    private val tableViewSender : TableView<monopoly.logic.Field> by fxid()
    private val tableViewReceiver : TableView<monopoly.logic.Field> by fxid()

    private val nameSender : Label by fxid()
    private val nameReceiver : Label by fxid()
    private val money1Lbl : Label by fxid()
    private val money2Lbl : Label by fxid()

    private val sendBtn : Button by fxid()

    init {
        nameSender.text = game.exchangeSender.name
        nameReceiver.text = game.exchangeReceiver.name
        tableViewSender.column("Поле", monopoly.logic.Field::nameProperty)
        tableViewSender.column("Стоимость", monopoly.logic.Field::costProperty)
        tableViewSender.items = game.exchangeReceiverList.asObservable()
        tableViewReceiver.column("Поле", monopoly.logic.Field::nameProperty)
        tableViewReceiver.column("Стоимость", monopoly.logic.Field::costProperty)
        tableViewReceiver.items = game.exchangeSenderList.asObservable()
        money1Lbl.text = "${game.exchangeMoneyReceiver}$"
        money2Lbl.text = "$${game.exchangeMoneySender}"
    }

    fun acceptExchange() {
        game.acceptExchange()
        gamePlay.updateColor(game.exchangeSender)
        gamePlay.updateColor(game.exchangeReceiver)
        gamePlay.offerLog()
        exit()
    }

    fun rejectExchange() {
        exit()
    }

    fun exit(){
        game.exchangePause = false
        close()
    }
}