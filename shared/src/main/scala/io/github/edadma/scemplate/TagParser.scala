package io.github.edadma.scemplate

import scala.util.{Failure, Success}

import org.parboiled2._

import scala.language.implicitConversions

class TagParser(val input: ParserInput) extends Parser {

  implicit def wspStr(s: String): Rule0 = rule(str(s) ~ zeroOrMore(' '))

  def tag: Rule1[TagParserAST] =
    rule {
      ws ~ (
        expression
          | assignmentTag
          | ifTag
          | elseTag
          | endTag
          | withTag
          | rangeTag
      ) ~ EOI
    }

  def expression: Rule1[ExprAST] = conditional

  def conditional: Rule1[ExprAST] =
    rule {
      ("if" ~ condition ~ "then" ~ expression ~ "else" ~ expression ~> ConditionalAST) | condition
    }

  def condition: Rule1[ExprAST] = disjunctive

  def disjunctive: Rule1[ExprAST] =
    rule {
      conjunctive ~ zeroOrMore(
        "or" ~ conjunctive ~> OrExpr
      )
    }

  def conjunctive: Rule1[ExprAST] =
    rule {
      pipeline ~ zeroOrMore(
        "and" ~ pipeline ~> AndExpr
      )
    }

  def not: Rule1[ExprAST] =
    rule {
      "not" ~ not |
        pipeline
    }

  //  def comparison: Rule1[ComparisonExpr]
  def pipeline: Rule1[ExprAST] =
    rule {
      applicative ~ zeroOrMore(
        "|" ~ (apply | variable) ~> PipeExpr
      )
    }

  def applicative: Rule1[ExprAST] = rule(apply | additive)

  def apply: Rule1[ApplyExpr] = rule(ident ~ oneOrMore(additive) ~> ApplyExpr)

  def additive: Rule1[ExprAST] =
    rule {
      multiplicative ~ zeroOrMore(
        "+" ~ multiplicative ~> AddExpr
          | "-" ~ multiplicative ~> SubExpr
      )
    }

  def multiplicative: Rule1[ExprAST] =
    rule {
      negative ~ zeroOrMore(
        "*" ~ negative ~> MulExpr
          | "/" ~ negative ~> DivExpr)
    }

  def negative: Rule1[ExprAST] =
    rule {
      "-" ~ negative |
        primary
    }

  def primary: Rule1[ExprAST] = rule {
    number |
      variable |
      string |
      "(" ~ expression ~ ")"
  }

  def number: Rule1[NumberExpr] = rule(decimal ~> NumberExpr)

  def decimal: Rule1[BigDecimal] =
    rule {
      capture(
        (zeroOrMore(CharPredicate.Digit) ~ '.' ~ digits | digits ~ '.') ~
          optional((ch('e') | 'E') ~ optional(ch('+') | '-') ~ digits) |
          digits
      ) ~ ws ~> ((s: String) => BigDecimal(s))
    }

  def integer: Rule1[Int] = rule(capture(digits) ~ ws ~> ((s: String) => s.toInt))

  def digits: Rule0 = rule(oneOrMore(CharPredicate.Digit))

  def variable: Rule1[VarExpr] = rule(capture(optional('$')) ~ ident ~> VarExpr)

  def string: Rule1[StringExpr] =
    rule((singleQuoteString | doubleQuoteString) ~> ((s: String) => StringExpr(unescape(s))))

  def backtickString: Rule1[String] = rule(capture('`' ~ zeroOrMore("\\`" | noneOf("`"))) ~ '`' ~ ws)

  def singleQuoteString: Rule1[String] = rule('\'' ~ capture(zeroOrMore("\\'" | noneOf("'\n"))) ~ '\'' ~ ws)

  def doubleQuoteString: Rule1[String] = rule('"' ~ capture(zeroOrMore("\\\"" | noneOf("\"\n"))) ~ '"' ~ ws)

  def ident: Rule1[Ident] =
    rule {
      pos ~ capture((CharPredicate.Alpha | '_') ~ zeroOrMore(CharPredicate.AlphaNum | '_')) ~> Ident ~ ws
    }

  def pos: Rule1[Int] = rule(push(cursor))

  def ws: Rule0 = rule(zeroOrMore(' '))

  def assignmentTag: Rule1[AssignmentAST] = rule(capture(optional('$')) ~ ident ~ ":=" ~ expression ~> AssignmentAST)

  def ifTag: Rule1[IfAST] = rule(pos ~ "if" ~ condition ~> IfAST)

  def elseTag: Rule1[ElseAST] = rule(pos ~ "else" ~> ElseAST)

  def endTag: Rule1[EndAST] = rule(pos ~ "end" ~> EndAST)

  def withTag: Rule1[IfAST] = rule(pos ~ "with" ~ expression ~> IfAST)

  def rangeTag: Rule1[IfAST] = rule(pos ~ "range" ~ expression ~> IfAST)

  def parseTag: TagParserAST =
    tag.run() match {
      case Success(ast)           => ast
      case Failure(e: ParseError) => sys.error("Expression is not valid: " + formatError(e))
      case Failure(e)             => sys.error("Unexpected error during parsing run: " + e)
    }

  def parseExpression: ExprAST =
    expression.run() match {
      case Success(ast)           => ast
      case Failure(e: ParseError) => sys.error("Expression is not valid: " + formatError(e))
      case Failure(e)             => sys.error("Unexpected error during parsing run: " + e)
    }

}
