package dsl.analyzer

/**
  * Created by iodone on {20-7-30}.
  */

import dsl.parser.DSLSQLParser._

object ConnectAnalyzer extends Analyzer {

  def analyze(ctx: SqlContext): ConnectStatement = {
    var option = Map[String, String]()
    var format = ""
    var namespace = ""

    (0 until ctx.getChildCount()).foreach {
      ctx.getChild(_) match {
        case s: FormatContext =>
          format = s.getText

        case s: ExpressionContext =>
          option += (cleanStr(s.identifier().getText) -> cleanStr(s.STRING().getText))
        case s: BooleanExpressionContext =>
          option += (cleanStr(s.expression().identifier().getText)-> cleanStr(s.expression().STRING().getText))
        case s: DbContext =>
          namespace = s.getText
        case _ =>
      }
    }
    ConnectStatement(Source(format, namespace), option, ctx.getText)

  }

}
