fun main(){
  Game().startGame()
}

class Game{

    class Player(val id: Int){
        private var numberOfMoves = 0
        private var position = numberOfMoves % 40
        var money = 15000.0
        var prisonDays = 0
        var doubleInARow = 0
        private val realty = mutableListOf<GameBoard.Field>()

        private fun positionUpdate(a : Int){
            numberOfMoves += a
            position = numberOfMoves % 40
        }

        private fun goToPrison(){
            if (position <= 10)
                positionUpdate(10-position)
            else positionUpdate(-(position - 10))
            prisonDays = 1
        }

        fun move(prisonOut : Int,board: GameBoard){
            val dice = Dice()
            do {
                if (prisonOut == 0) dice.roll()
                if (dice.double) doubleInARow ++
                else doubleInARow = 0

                if ( doubleInARow == 3) break

                if (numberOfMoves % 40 > (numberOfMoves + dice.count + prisonOut) % 40){
                    money += 2000
                    message(111)
                }

                positionUpdate(dice.count + prisonOut)
                report(board)
            }while (dice.double )

            if (doubleInARow == 3) {
                goToPrison()
                message(228)
                doubleInARow -= 3
            }
            return
        }

        private fun report(board: GameBoard){
            when(board.fields[position].type){
                Type.Start -> {
                    money += 1000
                    message(123)
                }
                Type.Secret -> secretAction(board)
                Type.ToPrison -> {
                    goToPrison()
                    message(229)
                }
                Type.Punisment -> {
                    money -= 2000
                    message(322)
                }
                Type.Free -> message(12)
                else -> realtyAction(board)
            }
        }

        private fun message(code: Int){
            when(code){
                12 -> println("Вы прогулялись по парку")
                111 -> println("За прохрд круга вы получаете 2000$")
                228 -> println("Вы отправляетсь в тюрьму, за махинации с кубиками.")
                229 -> println("Вы отправляетсь в тюрьму, за уход от налогов.")
                322 -> println("Вас обокрали на 2000$")
                123 -> println("За попадание на поле СТАРТ вы получаете 1000$")
                545 -> println("Вы нашли на дороге 750$")
                546 -> println("Вы получаете шанс кинуть кубики еще раз")
                547 -> println("Вы попали на распродажу и потратили там 500$")
                548 -> println("Вы выйграли 1000$ в лотерее")
                else -> println("abc")
            }
            println("Ваш баланс : $money$")
            readLine()
        }

        private fun realtyAction(board: GameBoard){

            if (board.fields[position].owner.id == 0) {
                if ( money >= board.fields[position].cost){
                    println("Хотите ли вы купить недвижимость типа: ${board.fields[position].type}, за ${board.fields[position].cost}$? У вас на счтеу ${money}$")
                if (readLine() == "y"){
                    board.fields[position].owner = Player(id)
                    money -= board.fields[position].cost
                    realty.add(board.fields[position])
                    return
                    }
                }
                println("Недвижимость будет выставлена на аукцион")

                return
            }

            if (board.fields[position].owner.id != id ){
                println("Вы должны заплатить Игроку${board.fields[position].owner.id } ${board.fields[position].penalty}$")
                readLine()
                money -= board.fields[position].penalty
                board.fields[position].owner.money += board.fields[position].penalty
                println("У вас на счету: ${money}$")
                return
            }
            println("Вы попали на свое поле и ничего не платите.")
        }

        private fun secretAction(board: GameBoard){
            val secret = (545..548).random()
            when(secret){
                545 -> money += 700
                546 -> {
                    message(secret)
                    move(0, board)
                    return
                }
                547 -> money -= 500
                548 -> money += 1000
            }
            message(secret)
        }

    }

    class Dice{
        var count = 0
        var double = false

        fun roll() {
            val first = (1..6).random()
            val second = (1..6).random()
            count = second + first
            double = first - second == 0
            print("На кубиках выпало $count ")
            if (double) print(", дублем!")
            println()
        }
    }

    class GameBoard{
        val fields = mutableListOf<Field>()

        class Field(location: Int, val type: Type){
            var owner = Player(0)
            var cost =  1000 + location*100
            var penalty = cost / 10

            fun costUpdate(a : Int){
                cost = a
                penalty = cost / 10
            }
        }
        }

    private val board = initBoard(GameBoard())

    fun startGame(){
        println("Начало игры")
        val players = mutableListOf<Player>()
        println("Сколько игроков?")
        val data = readLine() ?: "2"
        for (i in 1..data.toInt())
            players.add(Player(i))
        while (players.size > 1){
            players.map {motion(it) }
        }
    }

    private fun motion(player: Player){
        println()
        println("Ход игрока${player.id} :")
        val dice = Dice()
        if (player.prisonDays == 0) {
            player.move(0, board)
            return
        }

        println("Вы должны заплатить 500$, или выбить дубль. Через ${4 - player.prisonDays} хода вы будете, обязаны выплатить 600$, если не выйдите из тюрьмы")
        print("Заплатить?(y/n)")
        if (readLine() == "y"){
            player.money -= 500
            player.prisonDays = 0
            player.move(0, board)
            return
        }

        dice.roll()
        if (dice.double) {
            player.prisonDays = 0
            player.doubleInARow++
            player.move(dice.count, board)
            return
        }

        player.prisonDays++
        if(player.prisonDays == 4){
            println("Вы платите 600$, и выходите из тюрьмы")
            player.prisonDays = 0
            player.money -= 600
            player.move(0, board)
        }

    }

    }


