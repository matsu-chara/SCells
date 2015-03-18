package org.stairwaybook.scells
import swing._
import swing.event._

class Spreadsheet(val height: Int, val width: Int) extends ScrollPane {
  val cellModel = new Model(height, width)
  import cellModel._
  val table = new Table(height, width) {
    rowHeight      = 25
    autoResizeMode = Table.AutoResizeMode.Off
    showGrid       = true
    gridColor      = new java.awt.Color(150, 150, 150)
    override def rendererComponent(isSelected: Boolean, focused: Boolean, row: Int, column: Int): Component =
      if (focused) new TextField(userData(row, column))
      else new Label(cells(row)(column).toString) { xAlignment = Alignment.Right }
    def userData(row: Int, column: Int): String = {
      val v = this(row, column)
      if (v == null) "" else v.toString
    }
    reactions += {
      case TableUpdated(table, rows, column) =>
        for (row <- rows)
          cells(row)(column).formula =
            FormulaParsers.parse(userData(row, column))
      case ValueChanged(cell) =>
        updateCell(cell.row, cell.column)
    }
    for (row <- cells; cell <- row) listenTo(cell)
  }
  val rowHeader =
    new ListView((0 until height) map (_.toString)) {
      fixedCellHeight = table.rowHeight
      fixedCellWidth  = 30
    }
  viewportView  = table
  rowHeaderView = rowHeader
}
