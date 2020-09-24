package monopoly.logic

fun initBoard(board: GameBoard) {
    board.fields.add(Field(0, Type.Start))
    board.fields[0].couldBuy = false
    board.fields.add(Field(1, Type.Perfume))
    board.fields[1].nameUpdate("Chanel")
    board.fields[1].layoutX = 190.0
    board.fields.add(Field(2, Type.Perfume))
    board.fields[2].nameUpdate("Lacoste")
    board.fields[2].layoutX = 290.0
    board.fields.add(Field(3, Type.Clothes))
    board.fields[3].nameUpdate("Adidas")
    board.fields[3].layoutX = 390.0
    board.fields.add(Field(4, Type.Secret))
    board.fields[4].couldBuy = false
    board.fields[4].layoutX = 490.0
    board.fields.add(Field(5, Type.Clothes))
    board.fields[5].nameUpdate("Puma")
    board.fields[5].layoutX = 590.0
    board.fields.add(Field(6, Type.Clothes))
    board.fields[6].nameUpdate("Nike")
    board.fields[6].layoutX = 690.0
    board.fields.add(Field(7, Type.Free))
    board.fields[7].couldBuy = false
    board.fields[7].layoutX = 920.0
    board.fields[7].layoutY = -10.0
    board.fields.add(Field(8, Type.SocialNetwork))
    board.fields[8].nameUpdate("Facebook")
    board.fields[8].layoutX = 840.0
    board.fields[8].layoutY = 170.0
    board.fields.add(Field(9, Type.SocialNetwork))
    board.fields[9].nameUpdate("Twitter")
    board.fields[9].layoutX = 840.0
    board.fields[9].layoutY = 270.0
    board.fields.add(Field(10, Type.Car))
    board.fields[10].nameUpdate("Mercedes-Benz")
    board.fields[10].particular = true
    board.fields[10].costUpdate(3000)
    board.fields[10].layoutX = 840.0
    board.fields[10].layoutY = 370.0
    board.fields.add(Field(11, Type.Soda))
    board.fields[11].nameUpdate("Coca-Cola")
    board.fields[11].layoutX = 840.0
    board.fields[11].layoutY = 470.0
    board.fields.add(Field(12, Type.Punisment))
    board.fields[12].penalty = 2000
    board.fields[12].couldBuy = false
    board.fields[12].layoutX = 840.0
    board.fields[12].layoutY = 570.0
    board.fields.add(Field(13, Type.Soda))
    board.fields[13].nameUpdate("Pepsi")
    board.fields[13].layoutX = 840.0
    board.fields[13].layoutY = 650.0
    board.fields.add(Field(14, Type.Free))
    board.fields[14].couldBuy = false
    board.fields[14].layoutX = 840.0
    board.fields[14].layoutY = 820.0
    board.fields.add(Field(15, Type.Airlanes))
    board.fields[15].nameUpdate("Lufthansa")
    board.fields[15].layoutX = 690.0
    board.fields[15].layoutY = 820.0
    board.fields.add(Field(16, Type.Airlanes))
    board.fields[16].nameUpdate("Eva Air")
    board.fields[16].layoutX = 590.0
    board.fields[16].layoutY = 820.0
    board.fields.add(Field(17, Type.Car))
    board.fields[17].nameUpdate("Audi")
    board.fields[17].particular = true
    board.fields[17].costUpdate(3000)
    board.fields[17].layoutX = 480.0
    board.fields[17].layoutY = 820.0
    board.fields.add(Field(18, Type.Punisment))
    board.fields[18].penalty = 2000
    board.fields[18].couldBuy = false
    board.fields[18].layoutX = 380.0
    board.fields[18].layoutY = 820.0
    board.fields.add(Field(19, Type.Airlanes))
    board.fields[19].nameUpdate("Аэрофлот")
    board.fields[19].layoutX = 280.0
    board.fields[19].layoutY = 820.0
    board.fields.add(Field(20, Type.Secret))
    board.fields[20].couldBuy = false
    board.fields[20].layoutX = 190.0
    board.fields[20].layoutY = 820.0
    board.fields.add(Field(21, Type.ToPrison))
    board.fields[21].couldBuy = false
    board.fields[21].layoutY = 820.0
    board.fields.add(Field(22, Type.FastFood))
    board.fields[22].nameUpdate("McDonald's")
    board.fields[22].layoutY = 670.0
    board.fields.add(Field(23, Type.Stonks))
    board.fields[23].couldBuy = false
    board.fields[23].layoutY = 570.0
    board.fields.add(Field(24, Type.FastFood))
    board.fields[24].nameUpdate("KFC")
    board.fields[24].layoutY = 480.0
    board.fields.add(Field(25, Type.Car))
    board.fields[25].nameUpdate("BMW")
    board.fields[25].particular = true
    board.fields[25].costUpdate(3000)
    board.fields[25].layoutY = 380.0
    board.fields.add(Field(26, Type.Software))
    board.fields[26].nameUpdate("Microsoft")
    board.fields[26].layoutY = 290.0
    board.fields.add(Field(27, Type.Software))
    board.fields[27].nameUpdate("Apple")
    board.fields[27].layoutY = 190.0
}