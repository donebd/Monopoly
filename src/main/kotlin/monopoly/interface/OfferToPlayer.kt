package monopoly.`interface`

import javafx.event.EventHandler
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.control.TableView
import javafx.scene.control.TextField
import javafx.scene.input.MouseEvent
import javafx.scene.layout.AnchorPane
import javafx.stage.WindowEvent
import javafx.util.Duration
import tornadofx.*

class OfferToPlayer : Fragment(){

    override val root : AnchorPane by fxml()

    private val tableViewSender : TableView<monopoly.logic.Field> by fxid()
    private val tableViewReceiver : TableView<monopoly.logic.Field> by fxid()
    private val senderOfferTrade : TableView<monopoly.logic.Field> by fxid()
    private val receiverOfferTrade : TableView<monopoly.logic.Field> by fxid()

    private val nameSender : Label by fxid()
    private val nameReceiver : Label by fxid()
    private val errorLabel : Label by fxid()

    private val realtySender = game.offerSender.realty.asObservable()
    private val realtyReceiver = game.offerReceiver.realty.asObservable()

    private val selectedToTrade = mutableListOf<monopoly.logic.Field>().asObservable()
    private val receivedToTrade = mutableListOf<monopoly.logic.Field>().asObservable()

    private val money1Field : TextField by fxid()
    private val money2Field : TextField by fxid()

    private val sendBtn : Button by fxid()

    init {
        nameSender.text = game.offerSender.name
        nameReceiver.text = game.offerReceiver.name
        tableViewSender.column("Поле", monopoly.logic.Field::nameProperty)
        tableViewSender.column("Стоимость", monopoly.logic.Field::costProperty)
        tableViewSender.items = realtySender
        tableViewReceiver.column("Поле", monopoly.logic.Field::nameProperty)
        tableViewReceiver.column("Стоимость", monopoly.logic.Field::costProperty)
        tableViewReceiver.items = realtyReceiver
        senderOfferTrade.column("Поле", monopoly.logic.Field::nameProperty)
        senderOfferTrade.column("Стоимость", monopoly.logic.Field::costProperty)
        senderOfferTrade.items = selectedToTrade
        receiverOfferTrade.column("Поле", monopoly.logic.Field::nameProperty)
        receiverOfferTrade.column("Стоимость", monopoly.logic.Field::costProperty)
        receiverOfferTrade.items = receivedToTrade
        sendBtn.tooltip("Разность в обмене не может превышать двух")
    }

    fun mouseClickedSend(e : MouseEvent) {
        if (e.clickCount == 2) addField()
    }

    fun mouseClickedBack(e : MouseEvent) {
        if (e.clickCount == 2) deleteField()
    }

    fun addField() {
        if (tableViewSender.selectedItem != null) {
            selectedToTrade.add(tableViewSender.selectedItem)
            tableViewSender.items = realtySender.filter { it !in selectedToTrade }.asObservable()
        }
        if (tableViewReceiver.selectedItem != null) {
            receivedToTrade.add(tableViewReceiver.selectedItem)
            tableViewReceiver.items = realtyReceiver.filter { it !in receivedToTrade }.asObservable()
        }
    }

    fun deleteField() {
        if (senderOfferTrade.selectedItem != null) {
            selectedToTrade.remove(senderOfferTrade.selectedItem)
            tableViewSender.items = realtySender.filter { it !in selectedToTrade }.asObservable()
        }
        if (receiverOfferTrade.selectedItem != null) {
            receivedToTrade.remove(receiverOfferTrade.selectedItem)
            tableViewReceiver.items = realtyReceiver.filter { it !in receivedToTrade }.asObservable()
        }
    }

    fun sendOffer(){
        var temp1 = money1Field.text
        try {
            temp1.toInt().toString()
        } catch (e : NumberFormatException){
            temp1 = "0"
        }
        val money1 = temp1.toInt()
        var temp2 = money2Field.text
        try {
            temp2.toInt().toString()
        } catch (e : NumberFormatException){
            temp2 = "0"
        }
        val money2 = temp2.toInt()
        val checkHasMoney = money1 <= game.offerSender.money && money2 <= game.offerReceiver.money
        if (game.correctExchange(selectedToTrade, receivedToTrade, money1, money2) && checkHasMoney) {
            game.offerSenderList = selectedToTrade
            game.offerReceiverList = receivedToTrade
            game.offerMoneySender = money1
            game.offerMoneyReceiver = money2
            close()
            find<OfferSolution>().openModal(resizable = false)!!.setOnCloseRequest {
                game.offerPause = false
            }
            return
        }
        errorLabel.opacity = 1.0
        timeline {
            keyframe(Duration.seconds(2.0)) {
                keyvalue(errorLabel.opacityProperty(), 0.0)
            }
        }
    }

}