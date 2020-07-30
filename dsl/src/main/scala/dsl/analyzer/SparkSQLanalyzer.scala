package dsl.analyzer

/**
  * Created by iodone on {20-7-28}.
  */
import org.apache.spark.sql.catalyst.parser.{SqlBaseLexer, SqlBaseParser}
import org.apache.spark.sql.catalyst.analysis.UnresolvedRelation
import org.apache.spark.sql.SparkSession

object SparkSQLanalyzer {

  val sparkSession = SparkSession.builder().appName("local").master("local[1]").getOrCreate()

  def parse(sql: String): Seq[String] = {
    val logicPlan = sparkSession.sessionState.sqlParser.parsePlan(sql)
    logicPlan.collect {
      case node: UnresolvedRelation => node.tableName
    }
  }

  def main(args: Array[String]): Unit = {
    println(parse("select * from t1 where a = 'b'"))
    println(parse("select distinct * from \n(select b.sample_hash,cast(b.keyhash as string)keyhash,a.package_name,a.program_name,a.source,a.mau,a.leiji_zhuangji from\n(select package_name,program_name,source,mau,leiji_zhuangji from  hue_zhaobing_0727_engine_cnt group by package_name,program_name,source,mau,leiji_zhuangji) a  left outer join t2 b on a.package_name = b.package_name and a.program_name = b.program_name)a"))
  }

}
