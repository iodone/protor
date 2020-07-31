package dsl.analyzer

import java.io.{StringReader, StringWriter}

import dsl.parser.DSLSQLLexer
import dsl.parser.DSLSQLParser.SqlContext
import org.antlr.v4.runtime.misc.Interval
import org.joda.time.DateTime
import org.apache.velocity.VelocityContext
import org.apache.velocity.app.Velocity
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.catalyst.analysis.UnresolvedRelation

/**
  * Created by iodone on {20-7-30}.
  */

trait Analyzer extends AnalyzerTool {
  def analyze(ctx: SqlContext): Statement
}

trait AnalyzerTool {

  def parseSql(sql: String, sparkSession: SparkSession): Seq[String] = {
    val logicPlan = sparkSession.sessionState.sqlParser.parsePlan(sql)
    logicPlan.collect {
      case node: UnresolvedRelation => node.tableName
    }
  }

  def getOrginText(ctx: SqlContext): String = {
    val input = ctx.start.getTokenSource().asInstanceOf[DSLSQLLexer]._input
    val start = ctx.start.getStartIndex()
    val stop = ctx.stop.getStopIndex()
    val interval = new Interval(start, stop)
    input.getText(interval)
  }

  def render(templateStr: String, root: Map[String, AnyRef]) = {
    val context: VelocityContext = new VelocityContext
    root.map { f =>
      context.put(f._1, f._2)
    }
    val w: StringWriter = new StringWriter
    Velocity.evaluate(context, w, "", new StringReader(templateStr))
    w.toString
  }

  def merge(sql: String, root: Map[String, String]) = {

    val dformat = "yyyy-MM-dd"

    val newRoot = root ++ Map[String, String](
      "yesterday" -> DateTime.now().minusDays(1).toString(dformat),
      "today" -> DateTime.now().toString(dformat),
      "tomorrow" -> DateTime.now().plusDays(1).toString(dformat),
      "theDayBeforeYesterday" -> DateTime.now().minusDays(2).toString(dformat)
    )

    render(sql, newRoot)
  }

  def cleanStr(str: String): String = {
    if (str.startsWith("`") || str.startsWith("\"") || str.startsWith("'"))
      str.substring(1, str.length - 1)
    else str
  }

  def withPathPrefix(prefix: String, path: String): String = {

    val newPath = cleanStr(path)
    if (prefix.isEmpty) return newPath

    if (path.contains("..")) {
      throw new RuntimeException("path should not contains ..")
    }
    if (path.startsWith("/")) {
      return prefix + path.substring(1, path.length)
    }

    prefix + newPath
  }
}
