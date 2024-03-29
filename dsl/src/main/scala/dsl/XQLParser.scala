package dsl

/**
  * Created by iodone on {20-7-30}.
  */


import org.antlr.v4.runtime.tree._
import org.antlr.v4.runtime._
import dsl.analyzer._
import dsl.parser._
import dsl.parser.DSLSQLParser._
import dsl.analyzer.Statement
import org.apache.spark.sql.SparkSession

object XQLParser {

  def parse(input: String, listener: XQLAnalyzeListener) = {
    val loadLexer = new DSLSQLLexer(new ANTLRInputStream(input))
    val tokens = new CommonTokenStream(loadLexer)
    val parser = new DSLSQLParser(tokens)
    parser.removeErrorListeners()
    parser.addErrorListener(ThrowErrorListener.INSTANCE)
    val stat = parser.statement()
    ParseTreeWalker.DEFAULT.walk(listener, stat)
    (listener.statements, input)
  }

  def main(args: Array[String]) = {
    val xql =   """
                  |select * from table_1 as table_2 options `x`="true";
                  |
                  |load tidb.`db2.tidb_news_info` options `a`="b" and `c`="d" as tidb_hello;
                  |
                  |save overwrite result as csv.`tmp_csv_test` options header="true" and fileNum="1";
                  |
                  |connect es where `es.nodes`="192.168.200.152" and `es.port`="9200" and `es.net.http.auth.user`="test" and `es.net.http.auth.pass`="123456" as db1;
                """.stripMargin
    val sparkSession = SparkSession.builder().appName("dsl").master("local[1]").getOrCreate()
    val res = parse(xql, new XQLAnalyzeListener(sparkSession))
    res._1.foreach(println(_))
  }
}

class XQLAnalyzeListener(val sparkSession: SparkSession) extends DSLSQLListener {

  var statements: Seq[Statement] = Nil

  override def exitSql(ctx: SqlContext): Unit = {
    ctx.getChild(0).getText.toLowerCase() match {
      case "load" =>
        statements = statements ++ Seq(LoadAnalyzer.analyze(ctx))
      case "select" =>
        statements = statements ++ Seq(SelectAnalyzer(this).analyze(ctx))
      case "save" =>
        statements = statements ++ Seq(SaveAnalyzer.analyze(ctx))
      case "connect" =>
        statements = statements ++ Seq(ConnectAnalyzer.analyze(ctx))
//      case "create" =>
//        new CreateAdaptor(this).parse(ctx)
//      case "insert" =>
//        new InsertAdaptor(this).parse(ctx)
//      case "drop" =>
//        new DropAdaptor(this).parse(ctx)
//      case "refresh" =>
//        new RefreshAdaptor(this).parse(ctx)
//      case "set" =>
//        new SetAdaptor(this).parse(ctx)
//      case "register" =>
//        new RegisterAdaptor(this).parse(ctx)
//      case "train" =>
//        new TrainAdaptor(this).parse(ctx)
//      case "trans" =>
//        new TransformAdaptor(this).parse(ctx)
    }

  }

  override def enterStatement(ctx: StatementContext): Unit = {}

  override def exitStatement(ctx: StatementContext): Unit = {}

  override def enterSql(ctx: SqlContext): Unit = {}

  override def enterFormat(ctx: FormatContext): Unit = {}

  override def exitFormat(ctx: FormatContext): Unit = {}

  override def enterPath(ctx: PathContext): Unit = {}

  override def exitPath(ctx: PathContext): Unit = {}

  override def enterTableName(ctx: TableNameContext): Unit = {}

  override def exitTableName(ctx: TableNameContext): Unit = {}

  override def enterCol(ctx: ColContext): Unit = {}

  override def exitCol(ctx: ColContext): Unit = {}

  override def enterQualifiedName(ctx: QualifiedNameContext): Unit = {}

  override def exitQualifiedName(ctx: QualifiedNameContext): Unit = {}

  override def enterIdentifier(ctx: IdentifierContext): Unit = {}

  override def exitIdentifier(ctx: IdentifierContext): Unit = {}

  override def enterStrictIdentifier(ctx: StrictIdentifierContext): Unit = {}

  override def exitStrictIdentifier(ctx: StrictIdentifierContext): Unit = {}

  override def enterQuotedIdentifier(ctx: QuotedIdentifierContext): Unit = {}

  override def exitQuotedIdentifier(ctx: QuotedIdentifierContext): Unit = {}

  override def visitTerminal(node: TerminalNode): Unit = {}

  override def visitErrorNode(node: ErrorNode): Unit = {}

  override def exitEveryRule(ctx: ParserRuleContext): Unit = {}

  override def enterEveryRule(ctx: ParserRuleContext): Unit = {}

  override def enterEnder(ctx: EnderContext): Unit = {}

  override def exitEnder(ctx: EnderContext): Unit = {}

  override def enterExpression(ctx: ExpressionContext): Unit = {}

  override def exitExpression(ctx: ExpressionContext): Unit = {}

  override def enterBooleanExpression(ctx: BooleanExpressionContext): Unit = {}

  override def exitBooleanExpression(ctx: BooleanExpressionContext): Unit = {}

  override def enterDb(ctx: DbContext): Unit = {}

  override def exitDb(ctx: DbContext): Unit = {}

  override def enterOverwrite(ctx: OverwriteContext): Unit = {}

  override def exitOverwrite(ctx: OverwriteContext): Unit = {}

  override def enterAppend(ctx: AppendContext): Unit = {}

  override def exitAppend(ctx: AppendContext): Unit = {}

  override def enterErrorIfExists(ctx: ErrorIfExistsContext): Unit = {}

  override def exitErrorIfExists(ctx: ErrorIfExistsContext): Unit = {}

  override def enterIgnore(ctx: IgnoreContext): Unit = {}

  override def exitIgnore(ctx: IgnoreContext): Unit = {}

  override def enterFunctionName(ctx: FunctionNameContext): Unit = {}

  override def exitFunctionName(ctx: FunctionNameContext): Unit = {}

  override def enterSetValue(ctx: SetValueContext): Unit = {}

  override def exitSetValue(ctx: SetValueContext): Unit = {}

  override def enterSetKey(ctx: SetKeyContext): Unit = {}

  override def exitSetKey(ctx: SetKeyContext): Unit = {}

}

