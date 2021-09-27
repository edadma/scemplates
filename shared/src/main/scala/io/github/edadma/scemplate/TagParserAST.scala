package io.github.edadma.scemplate

case class Ident(pos: Int, name: String)

trait TagParserAST

trait Positioned {
  val pos: Int
}

trait ExprAST extends TagParserAST

trait SimpleExpr extends ExprAST with Positioned

case class StringExpr(pos: Int, s: String) extends SimpleExpr

case class NumberExpr(pos: Int, n: BigDecimal) extends SimpleExpr

case class BooleanExpr(pos: Int, b: Boolean) extends SimpleExpr

case class VarExpr(pos: Int, user: String, name: Ident) extends SimpleExpr

case class ElementExpr(pos: Int, global: String, ids: Seq[Ident]) extends SimpleExpr

case class MapExpr(pairs: Seq[(Ident, ExprAST)]) extends ExprAST

case class UnaryExpr(op: String, expr: ExprAST) extends ExprAST

case class BinaryExpr(left: ExprAST, op: String, right: ExprAST) extends ExprAST

case class ApplyExpr(name: Ident, args: Seq[ExprAST]) extends ExprAST

case class ConcatExpr(elems: Seq[ExprAST]) extends ExprAST

case class ConditionalAST(cond: ExprAST, yes: ExprAST, no: Option[ExprAST]) extends ExprAST

case class CompareExpr(left: ExprAST, right: Seq[(String, ExprAST)]) extends ExprAST

case class MethodExpr(expr: ExprAST, method: Ident, args: Seq[ExprAST]) extends ExprAST

case class PipeExpr(left: ExprAST, right: ApplyExpr) extends ExprAST

case class AssignmentAST(name: Ident, expr: ExprAST) extends TagParserAST

case class CommentAST(comment: String) extends TagParserAST

trait ConstructAST extends TagParserAST with Positioned

trait SimpleBlockAST extends ConstructAST

case class IfAST(pos: Int, cond: ExprAST) extends ConstructAST

case class ElseIfAST(pos: Int, cond: ExprAST) extends ConstructAST

case class ElseAST(pos: Int) extends ConstructAST

case class EndAST(pos: Int) extends ConstructAST

case class WithAST(pos: Int, expr: ExprAST) extends SimpleBlockAST

case class ForAST(pos: Int, index: Option[(Option[Ident], Ident)], expr: ExprAST) extends SimpleBlockAST
