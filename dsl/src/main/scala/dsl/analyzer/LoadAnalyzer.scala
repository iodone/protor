package dsl.analyzer


/**
  * Created by iodone on {20-7-30}.
  */

import dsl.parser.DSLSQLParser._

object LoadAnalyzer extends Analyzer {

  def analyze(ctx: SqlContext): LoadStatement = {
    var option = Map[String, String]()
    var format = ""
    var path = ""
    var tempView = ""
    (0 until ctx.getChildCount()).foreach {
      ctx.getChild(_) match {
        case s: FormatContext =>
          format = s.getText
        case s: ExpressionContext =>
          option += (cleanStr(s.identifier().getText) -> cleanStr(s.STRING().getText))
        case s: BooleanExpressionContext =>
          option += (cleanStr(s.expression().identifier().getText) -> cleanStr(s.expression().STRING().getText))
        case s: PathContext =>
          path = s.getText
        case s: TableNameContext =>
          tempView = s.getText
        case _ =>
      }
    }

    val st = path.split("\\.", 2)
    val (namespace, table) = if (st.length == 1) ("", st(0)) else (st(0), st(1))

    LoadStatement(Source(format, namespace), tempView, option, ctx.getText)

  }
}
