package monopoly.`interface`

import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.layout.AnchorPane
import monopoly.logic.Type
import tornadofx.Fragment
import tornadofx.c
import tornadofx.imageview

class Field : Fragment(){

    override val root : AnchorPane by fxml()

    private val player : Label by fxid()
    private val penaltyLabel : Label by fxid()
    private val costLabel : Label by fxid()
    private val countUpgrade : Label by fxid()
    private val costOfUpgrade : Label by fxid()
    private val notEnoughMoney : Label by fxid()
    private val upgradeButton : Button by fxid()
    private val sellUpgradeButton : Button by fxid()
    private val sellByHalfButton : Button by fxid()
    private val companyName : Label by fxid()
    private val typeOfField : Label by fxid()

    init {
        game.fieldActionInit()
        companyName.text = game.board.fields[game.position].name
        typeOfField.text = game.board.fields[game.position].type.toString()
        when (game.position){
            1 -> with(root){
                val chanel = imageview("monopoly/fields/Chanel.png")
                chanel.rotate = -90.0
                chanel.layoutX = 40.0
                this.add(chanel)
            }
            2 -> with(root){
                val lacoste = imageview("monopoly/fields/Lacoste.png")
                lacoste.rotate = -90.0
                lacoste.layoutX = 40.0
                this.add(lacoste)
            }
            3 -> with(root){
                val adidas = imageview("monopoly/fields/Adidas.png")
                adidas.rotate = -90.0
                adidas.layoutX = 40.0
                this.add(adidas)
            }
            5 -> with(root){
                val puma = imageview("monopoly/fields/Puma.png")
                puma.rotate = -90.0
                puma.layoutX = 60.0
                this.add(puma)
            }
            6 -> with(root){
                val nike = imageview("monopoly/fields/Nike.png")
                nike.rotate = -90.0
                nike.layoutX = 60.0
                this.add(nike)
            }
            8 -> with(root){
                val facebook = imageview("monopoly/fields/Facebook.png")
                facebook.layoutY = 40.0
                this.add(facebook)
            }
            9 -> with(root){
                val twitter = imageview("monopoly/fields/Twitter.png")
                this.add(twitter)
            }
            10 -> with(root){
                val mercedes = imageview("monopoly/fields/Mercedes.png")
                this.add(mercedes)
            }
            11 -> with(root){
                val cola = imageview("monopoly/fields/Coca-cola.png")
                cola.layoutY = 25.0
                cola.layoutX = 25.0
                this.add(cola)
            }
            13 -> with(root){
                val pepsi = imageview("monopoly/fields/Pepsi.png")
                pepsi.layoutY = 30.0
                this.add(pepsi)
            }
            15 -> with(root){
                val lufthansa = imageview("monopoly/fields/Lufthansa.png")
                lufthansa.rotate = 90.0
                lufthansa.layoutX = 50.0
                this.add(lufthansa)
            }
            16 -> with(root){
                val evaAir = imageview("monopoly/fields/Eva air.png")
                evaAir.rotate = 90.0
                evaAir.layoutX = 80.0
                this.add(evaAir)
            }
            17 -> with(root){
                val audi = imageview("monopoly/fields/Audi.png")
                audi.rotate = 90.0
                audi.layoutX = 50.0
                this.add(audi)
            }
            19 -> with(root){
                val aeroFlot = imageview("monopoly/fields/Aeroflot.png")
                aeroFlot.rotate = 90.0
                aeroFlot.layoutX = 50.0
                this.add(aeroFlot)
            }
            22 -> with(root){
                val mcDonalds = imageview("monopoly/fields/Mcdonalds.png")
                mcDonalds.layoutX = 50.0
                mcDonalds.layoutY = 20.0
                this.add(mcDonalds)
            }
            24 -> with(root){
                val kfc = imageview("monopoly/fields/Kfc.png")
                kfc.layoutY = 30.0
                this.add(kfc)
            }
            25 -> with(root){
                val bmw = imageview("monopoly/fields/Bmw.png")
                bmw.layoutX = 50.0
                bmw.layoutY = 40.0
                this.add(bmw)
            }
            26 -> with(root){
                val microsoft = imageview("monopoly/fields/Microsoft.png")
                microsoft.layoutY = 30.0
                this.add(microsoft)
            }
            else -> with(root){
                val apple = imageview("monopoly/fields/Apple.png")
                apple.layoutY = 45.0
                apple.layoutX = 20.0
                this.add(apple)
            }
        }
        player.text =  game.data[game.playerClicked].name
        costLabel.text = "${game.board.fields[game.position].cost}"
        costOfUpgrade.text = "${game.board.fields[game.position].upgradeCost}"
        changable()
    }


    private fun changable(){
        penaltyLabel.text = "${game.board.fields[game.position].penalty}"
        countUpgrade.text = "${game.board.fields[game.position].upgrade}"
        if (game.playerHasMonopoly()){
            upgradeButton.disableProperty().value = game.fieldCantBeUpgraded()
            //check for count upgrade
            sellUpgradeButton.disableProperty().value = game.fieldCantBeSelled()
        }
        sellByHalfButton.disableProperty().value = !game.fieldCantBeSelled()
        gamePlay.updateUpgrade()
    }

    fun sellByHalf(){
        game.fieldSellByHalf()
        for (current in game.data[game.playerClicked].realty.filter{ it.type == game.board.fields[game.click].type}){
            gamePlay.labelUpgradeClear(current.location)
        }
        gamePlay.paintField(game.position, c("#d2edd7"))
        close()
    }

    fun buildUpgrade(){
        if (game.fieldBuildUpgrade()){
            changable()
            return
        }
        notEnoughMoney.opacity = 1.0
    }

    fun sellUpgrade(){
        game.fieldSellUpgrade()
        changable()
    }
}