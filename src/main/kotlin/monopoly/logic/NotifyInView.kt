package monopoly.logic

import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleIntegerProperty
import javafx.beans.property.SimpleStringProperty

class NotifyInView() {
    var prisonInitProperty = SimpleBooleanProperty()
    var penaltyInitProperty = SimpleBooleanProperty()
    var offerToBuyInitProperty = SimpleBooleanProperty()
    var negativeEventInitProperty = SimpleBooleanProperty()
    var positiveEventInitProperty = SimpleBooleanProperty()
    var diceDoubleProperty = SimpleBooleanProperty()
    var cycleCompleteProperty = SimpleBooleanProperty()
    var stonksActionProperty = SimpleBooleanProperty()
    var startActionProperty = SimpleBooleanProperty()
    var notifyInView = SimpleStringProperty()
    var updateUpgradeView = SimpleBooleanProperty()
    var viewEndMotionProperty = SimpleBooleanProperty()
    var toPrisonViewProperty = SimpleBooleanProperty()
    var surrenderViewProperty = SimpleBooleanProperty()
    var fieldBoughtProperty = SimpleBooleanProperty()
    var sellUpgradeViewProperty = SimpleBooleanProperty()
    var fieldDegradeProperty = SimpleIntegerProperty()
    var fieldSelledProperty = SimpleIntegerProperty()
}