package dsl.analyzer

import dsl.XQLAnalyzeListener
import dsl.parser.DSLSQLLexer
import dsl.parser.DSLSQLParser._
import org.antlr.v4.runtime.misc.Interval

/**
  * Created by iodone on {20-7-30}.
  *
  */
case class SelectAnalyzer(context: XQLAnalyzeListener) extends Analyzer {

  def analyze(ctx: SqlContext): SelectStatement = {
    var option = Map[String, String]()
    (0 until ctx.getChildCount()).foreach {
      ctx.getChild(_) match {
        case s: ExpressionContext =>
          option += (cleanStr(s.identifier().getText) -> cleanStr(s.STRING().getText))
        case s: BooleanExpressionContext =>
          option += (cleanStr(s.expression().identifier().getText) -> cleanStr(s.expression().STRING().getText))
        case _ =>
      }
    }

    val originalText = getOrginText(ctx)

    val chunks = originalText.replaceAll("options.*?$", "").split("\\s+")
    val tempView = chunks.last.replace(";", "")
    val sql = originalText.replaceAll("options.*?$", "").replaceAll(s"((?i)as)[\\s|\\n]+${tempView}\\s*\\n*$$", "")

    val from = parseSql(sql, context.sparkSession)
    SelectStatement(sql.trim, tempView, option, from, originalText)
  }
}
