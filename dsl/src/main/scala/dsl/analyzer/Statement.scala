package dsl.analyzer

/**
  * Created by iodone on {20-7-30}.
  */
trait Statement

case class Source(format: String, namespace: String)

case class ConnectStatement(source: Source, where: Map[String, String], originText: String) extends Statement
case class LoadStatement(source: Source, tempView: String, where: Map[String, String], originText: String) extends Statement
case class SelectStatement(sqlText: String, tempView: String, where: Map[String, String], from: Seq[String], originText: String) extends Statement
case class SaveStatement(mode: String, source: Source, where: Map[String, String], from: Seq[String], originText: String) extends Statement

