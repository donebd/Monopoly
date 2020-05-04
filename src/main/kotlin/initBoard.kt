import Game.GameBoard.*

enum class Type{
    Start,
    ToPrison,
    Secret,
    Punisment,
    Free,
    Perfume,
    Clothes,
    SocialNetwork,
    Soda,
    Airlanes,
    FastFood,
    Hotel,
    Car,
    It
}

fun initBoard(board: Game.GameBoard) : Game.GameBoard {
    board.fields.add(Field(0, Type.Start))
    board.fields.add(Field(1, Type.Perfume))
    board.fields.add(Field(2, Type.Secret))
    board.fields.add(Field(3, Type.Perfume))
    board.fields.add(Field(4, Type.Punisment))
    board.fields.add(Field(5, Type.Car))
    board.fields[5].costUpdate(2000)
    board.fields.add(Field(6, Type.Clothes))
    board.fields.add(Field(7, Type.Secret))
    board.fields.add(Field(8, Type.Clothes))
    board.fields.add(Field(9, Type.Clothes))
    board.fields.add(Field(10, Type.Free))
    board.fields.add(Field(11, Type.SocialNetwork))
    board.fields.add(Field(12, Type.Secret))
    board.fields.add(Field(13, Type.SocialNetwork))
    board.fields.add(Field(14, Type.SocialNetwork))
    board.fields.add(Field(15, Type.Car))
    board.fields[15].costUpdate(2000)
    board.fields.add(Field(16, Type.Soda))
    board.fields.add(Field(17, Type.Punisment))
    board.fields.add(Field(18, Type.Soda))
    board.fields.add(Field(19, Type.Soda))
    board.fields.add(Field(20, Type.Free))
    board.fields.add(Field(21, Type.Airlanes))
    board.fields.add(Field(22, Type.Punisment))
    board.fields.add(Field(23, Type.Airlanes))
    board.fields.add(Field(24, Type.Airlanes))
    board.fields.add(Field(25, Type.Car))
    board.fields[25].costUpdate(2000)
    board.fields.add(Field(26, Type.FastFood))
    board.fields.add(Field(27, Type.FastFood))
    board.fields.add(Field(28, Type.Secret))
    board.fields.add(Field(29, Type.FastFood))
    board.fields.add(Field(30, Type.ToPrison))
    board.fields.add(Field(31, Type.Hotel))
    board.fields.add(Field(32, Type.Hotel))
    board.fields.add(Field(33, Type.Secret))
    board.fields.add(Field(34, Type.Hotel))
    board.fields.add(Field(35, Type.Car))
    board.fields[35].costUpdate(2000)
    board.fields.add(Field(36, Type.Punisment))
    board.fields.add(Field(37, Type.It))
    board.fields.add(Field(38, Type.Secret))
    board.fields.add(Field(39, Type.It))
    return board
}