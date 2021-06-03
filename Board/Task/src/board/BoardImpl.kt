package board

import board.Direction.*
import java.lang.IllegalArgumentException

fun createSquareBoard(width: Int): SquareBoard = SquareBoardImpl(width)
fun <T> createGameBoard(width: Int): GameBoard<T> = GameBoardImpl(SquareBoardImpl(width))

class SquareBoardImpl(override val width: Int) : SquareBoard {
    private val cells = (1..width)
        .flatMap { i -> (1..width).map { j -> (i to j) to Cell(i, j) } }
        .associateBy({ (cords, _) -> cords }, { (_, cell) -> cell })

    override fun getCellOrNull(i: Int, j: Int): Cell? = cells[i to j]

    override fun getCell(i: Int, j: Int): Cell {
        return getCellOrNull(i, j) ?: throw IllegalArgumentException("$i $j coords are invalid for width $width")
    }

    override fun getAllCells(): Collection<Cell> = cells.values

    override fun getRow(i: Int, jRange: IntProgression): List<Cell> = jRange.mapNotNull { j -> cells[i to j] }

    override fun getColumn(iRange: IntProgression, j: Int): List<Cell> = iRange.mapNotNull { i -> cells[i to j] }

    override fun Cell.getNeighbour(direction: Direction): Cell? {
        val (i, j) = this
        return when (direction) {
            UP -> cells[i - 1 to j]
            DOWN -> cells[i + 1 to j]
            RIGHT -> cells[i to j + 1]
            LEFT -> cells[i to j - 1]
        }
    }
}

class GameBoardImpl<T>(private val board: SquareBoard) : SquareBoard by board, GameBoard<T> {
    private val values: MutableMap<Cell, T?> = board.getAllCells().associateWith { null }.toMutableMap()

    override fun get(cell: Cell): T? = values[cell]

    override fun set(cell: Cell, value: T?) {
        values[cell] = value
    }

    override fun filter(predicate: (T?) -> Boolean): Collection<Cell> = values.filterValues(predicate).keys

    override fun find(predicate: (T?) -> Boolean): Cell? = filter(predicate).firstOrNull()

    override fun any(predicate: (T?) -> Boolean): Boolean = values.values.any(predicate)

    override fun all(predicate: (T?) -> Boolean): Boolean = values.values.all(predicate)

}
