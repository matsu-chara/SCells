package org.stairwaybook.scells

import swing._
import swing.event._

class Model(val height: Int, val width: Int) extends Evaluator with Arithmetic {
  case class Cell(row: Int, column: Int) extends Publisher {
    private var v: Double = 0
    def value: Double = v
    def value_=(w: Double): Unit = {
      if (!(v == w || v.isNaN && w.isNaN)) {
        v = w
        publish(ValueChanged(this))
      }
    }

    private var f: Formula = Empty
    def formula: Formula = f
    def formula_=(f: Formula): Unit = {
      for (c <- references(formula)) deafTo(c)
      this.f = f
      for (c <- references(formula)) listenTo(c)
      value = evaluate(f)
    }

    override def toString = formula match {
      case Textual(s) => s
      case _ => value.toString
    }
    reactions += {
      case ValueChanged(_) => value = evaluate(formula)
    }
  }
  case class ValueChanged(cell: Cell) extends event.Event
  val cells = Array.tabulate[Cell](height, width)(new Cell(_,_))
}