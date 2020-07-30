package dsl.analyzer

/**
  * Created by iodone on {20-7-30}.
  */

import dsl.parser.DSLSQLParser._

object SaveAnalyzer extends Analyzer {
  def analyze(ctx: SqlContext): SaveStatement = {

    var format = ""
    var mode = ""
    var path = ""
    var option = Map[String, String]()
    var tempView = ""
    var partitionByCol = Array[String]()

    (0 until ctx.getChildCount).foreach {
      ctx.getChild(_) match {
        case s: FormatContext =>
          format = s.getText
        case s: PathContext =>
          path = cleanStr(s.getText)
        case s: TableNameContext =>
          tempView = s.getText
        case s: OverwriteContext =>
          mode = s.getText
        case s: AppendContext =>
          mode = s.getText
        case s: ErrorIfExistsContext =>
          mode = s.getText
        case s: IgnoreContext =>
          mode = s.getText
        case s: ColContext =>
          partitionByCol = cleanStr(s.getText).split(",")
        case s: ExpressionContext =>
          option += (cleanStr(s.identifier().getText) -> cleanStr(s.STRING().getText))
        case s: BooleanExpressionContext =>
          option += (cleanStr(s.expression().identifier().getText) -> cleanStr(s.expression().STRING().getText))
        case _ =>
      }
    }

  }
}
