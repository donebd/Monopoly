package monopoly.`interface`

import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.control.TableView
import javafx.scene.control.TextField
import javafx.scene.layout.AnchorPane
import javafx.util.Duration
import tornadofx.*
import java.lang.NumberFormatException

class ExchangeSolution : Fragment() {

    override val root: AnchorPane by fxml()

    private val tableViewSender: TableView<monopoly.logic.Field> by fxid()
    private val tableViewReceiver: TableView<monopoly.logic.Field> by fxid()

    private val nameSender: Label by fxid()
    private val nameReceiver: Label by fxid()
    private val money1Lbl: Label by fxid()
    private val money2Lbl: Label by fxid()

    private val sendBtn: Button by fxid()

    var exchange = game.currentExchange

    init {
        nameSender.text = exchange.exchangeSender.name
        nameReceiver.text = exchange.exchangeReceiver.name
        tableViewSender.column("Поле", monopoly.logic.Field::nameProperty)
        tableViewSender.column("Стоимость", monopoly.logic.Field::costProperty)
        tableViewSender.items = exchange.exchangeReceiverList.asObservable()
        tableViewReceiver.column("Поле", monopoly.logic.Field::nameProperty)
        tableViewReceiver.column("Стоимость", monopoly.logic.Field::costProperty)
        tableViewReceiver.items = exchange.exchangeSenderList.asObservable()
        money1Lbl.text = "${exchange.exchangeMoneyReceiver}$"
        money2Lbl.text = "$${exchange.exchangeMoneySender}"
    }

    fun acceptExchange() {
        exchange.acceptExchange()
        gamePlay.exchangeOfferLog(exchange)
        exit()
    }

    fun rejectExchange() {
        exit()
    }

    fun exit() {
        exchange.exchangePause = false
        close()
    }
}