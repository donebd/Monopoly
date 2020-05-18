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

    private var position = 1

    private var monopolySize = 2

    private var type : Type

    init {
        position = gamePlay.click
        companyName.text = board.fields[position].name
        typeOfField.text = board.fields[position].type.toString()

        when (position){
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

        monopolySize = when(position){
            1,2,8,9,11,13,22,24,26,27 -> 2
            else -> 3
        }
        type = when(position){
            1,2 -> Type.Perfume
            3,5,6 -> Type.Clothes
            8,9 -> Type.SocialNetwork
            11,13 -> Type.Soda
            15,16,19 -> Type.Airlanes
            22,24 -> Type.FastFood
            26,27 -> Type.Software
            else -> Type.Car
        }
        player.text = data[gamePlay.presentId].name
        costLabel.text = "${board.fields[position].cost}"
        costOfUpgrade.text = "${board.fields[position].upgradeCost}"
        changable()
    }


    private fun changable(){
        penaltyLabel.text = "${board.fields[position].penalty}"
        countUpgrade.text = "${board.fields[position].upgrade}"
        if (data[gamePlay.presentId].realty.filter { it.type == type}.size == monopolySize){
            upgradeButton.disableProperty().value = (board.fields[position].upgrade > 4 ||
                    data[gamePlay.presentId].currentMotionUpgrade.contains(type))
            //check for count upgrade
            sellUpgradeButton.disableProperty().value = board.fields[position].upgrade == 0
        }
        sellByHalfButton.disableProperty().value = board.fields[position].upgrade != 0
        val upgrade = when (board.fields[position].upgrade){
            0 -> ""
            1 -> "*"
            2 -> "**"
            3 -> "***"
            4 -> "****"
            else -> "*****"
        }
        when(position){
            1 -> gamePlay.labelUpgrade1.text = upgrade
            2 -> gamePlay.labelUpgrade2.text = upgrade
            3 -> gamePlay.labelUpgrade3.text = upgrade
            5 -> gamePlay.labelUpgrade5.text = upgrade
            6 -> gamePlay.labelUpgrade6.text = upgrade
            8 -> gamePlay.labelUpgrade8.text = upgrade
            9 -> gamePlay.labelUpgrade9.text = upgrade
            10 -> gamePlay.labelUpgrade10.text = upgrade
            11 -> gamePlay.labelUpgrade11.text = upgrade
            13 -> gamePlay.labelUpgrade13.text = upgrade
            15 -> gamePlay.labelUpgrade15.text = upgrade
            16 -> gamePlay.labelUpgrade16.text = upgrade
            17 -> gamePlay.labelUpgrade17.text = upgrade
            19 -> gamePlay.labelUpgrade19.text = upgrade
            22 -> gamePlay.labelUpgrade22.text = upgrade
            24 -> gamePlay.labelUpgrade24.text = upgrade
            25 -> gamePlay.labelUpgrade25.text = upgrade
            26 -> gamePlay.labelUpgrade26.text = upgrade
            else -> gamePlay.labelUpgrade27.text = upgrade
        }
    }

    fun sellByHalf(){
        data[gamePlay.presentId].moneyChange(board.fields[position].cost/2)
        data[gamePlay.presentId].realty.remove( board.fields[position])
        board.fields[position].owner = null
        board.fields[position].upgrade = 0
        board.fields[position].penaltyUpdate()
        gamePlay.paintField(position, c("#d2edd7"))
        close()
    }

    fun buildUpgrade(){
        if (data[gamePlay.presentId].money >= board.fields[position].upgradeCost){
            data[gamePlay.presentId].moneyChange(-board.fields[position].upgradeCost)
            board.fields[position].upgrade++
            board.fields[position].penaltyUpdate()
            data[gamePlay.presentId].currentMotionUpgrade.add(type)
            changable()
            return
        }
        notEnoughMoney.opacity = 1.0
    }

    fun sellUpgrade(){
        data[gamePlay.presentId].moneyChange(board.fields[position].upgradeCost)
        board.fields[position].upgrade--
        board.fields[position].penaltyUpdate()
        changable()
    }
}