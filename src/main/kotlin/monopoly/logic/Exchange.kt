package monopoly.logic

class ExchangeOffer() {

    constructor(sender: Player, receiver: Player) : this() {
        exchangeSender = sender
        exchangeReceiver = receiver
    }

    var exchangeSender: Player = Player(228)
    var exchangeReceiver: Player = Player(228)
    var exchangeSenderList = mutableListOf<Field>()
    var exchangeReceiverList = mutableListOf<Field>()
    var exchangeMoneySender = 0
    var exchangeMoneyReceiver = 0
    var exchangePause = false

    fun possibleExchange(
        sendingFields: List<Field>,
        receiveringFields: List<Field>,
        moneySend: Int,
        moneyGet: Int
    ): Boolean {
        if (sendingFields.isEmpty() && receiveringFields.isEmpty()) return false
        var costSend = moneySend
        var costReceive = moneyGet
        for (field in sendingFields) {
            costSend += field.cost
        }
        for (field in receiveringFields) {
            costReceive += field.cost
        }
        if (costSend > costReceive * 2 || 2 * costSend < costReceive) return false
        return true
    }

    fun acceptExchange() {
        for (field in exchangeSenderList) {
            exchangeSender.realty.remove(field)
            field.ownerUpdate(null)
            field.upgrade = 0
            exchangeSender.checkForMonopoly(field)
            field.penaltyUpdate()
            exchangeReceiver.realty.add(field)
            field.ownerUpdate( exchangeReceiver)
            exchangeReceiver.checkForMonopoly(field)
            field.penaltyUpdate()
        }// fields of sender to receiver

        for (field in exchangeReceiverList) {
            exchangeReceiver.realty.remove(field)
            field.ownerUpdate(null)
            field.upgrade = 0
            exchangeReceiver.checkForMonopoly(field)
            field.penaltyUpdate()
            exchangeSender.realty.add(field)
            field.ownerUpdate( exchangeSender)
            exchangeSender.checkForMonopoly(field)
            field.penaltyUpdate()
        }

        exchangeSender.moneyChange(exchangeMoneyReceiver)
        exchangeSender.moneyChange(-exchangeMoneySender)
        exchangeReceiver.moneyChange(exchangeMoneySender)
        exchangeReceiver.moneyChange(-exchangeMoneyReceiver)
    }
}