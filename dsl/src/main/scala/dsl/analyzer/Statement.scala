package dsl.analyzer

/**
  * Created by iodone on {20-7-30}.
  */
trait Statement

case class Source(format: String, namespace: String, table: String)

case class ConnectStatement(format: String, namespace: String, where: Map[String, String], originText: String) extends Statement
case class LoadStatement(load: Source, as: String, where: Map[String, String], originText: String) extends Statement
case class SelectStatement(sqlText: String, as: String, where: Map[String, String], from: Seq[String], originText: String) extends Statement
case class SaveStatement(mode: String, from: String, sink: Source, where: Map[String, String], originText: String) extends Statement

