package app.job.repositry

/**
  * Created by iodone on {18-11-5}.
  */

import java.io.File
import java.net.URLClassLoader

import app.BaseServiceTest
import SubmitUtil._

class SparkOnYarnRepositrySpec extends BaseServiceTest {


  override def beforeAll(): Unit = {
  }

  "submit spark appliction on yarn" when {
    // 先设置启动环境变量：HADOOP_CONF_DIR和HADOOP_USER_NAME
    // 读取hadoop配置文件
    val hadoopConfigFiles = new File("tmp/hadoop").toURI.toURL

    "spark 2.3.2 jar" should {
      // 读取spark jars
      val sparkJarFiles = new File("tmp/2.3.2").listFiles.map(_.toURI.toURL)
      val appArgs = Array("--jars","hdfs://nameservice1/user/spark2/app/cube/jar/ext/2.3.2/*,hdfs://nameservice1/user/spark2/app/cube/jar/lib/2.3.2/*,hdfs://nameservice1/user/spark2/app/cube/jar/udf/*","--conf","spark.sql.files.maxPartitionBytes=134217728","--conf","spark.serializer=org.apache.spark.serializer.KryoSerializer","--conf","spark.shuffle.service.enable=true","--conf","spark.sql.warehouse.dir=/user/hive/warehouse","--conf","spark.speculation=false","--conf","spark.yarn.jars=hdfs://nameservice1/user/spark2/app/cube/jar/lib/2.3.2/*","--name","recovery_test-f33c1185309a47a59a53a2a09f462ea8","--conf","spark.executor.extraJavaOptions=-Xms2g -XX:MetaspaceSize=80m -XX:MaxMetaspaceSize=128m -XX:+PrintGCDetails -XX:-UseGCOverheadLimit -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=/tmp/xmatrix/gc/ -XX:+UnlockExperimentalVMOptions -XX:NewRatio=1 -XX:SurvivorRatio=6 -XX:+UseG1GC -XX:MaxGCPauseMillis=100 -XX:GCPauseIntervalMillis=200 -XX:InitiatingHeapOccupancyPercent=80 -XX:G1HeapRegionSize=16M","--conf","spark.sql.hive.verifyPartitionPath=true","--conf","spark.sql.autoBroadcastJoinThreshold=10485760","--conf","spark.io.compression.codec=lz4","--conf","spark.hadoop.hive.metastore.uris=thrift://hadoop-tworker02:9083,thrift://hadoop-tworker03:9083,thrift://hadoop-tworker04:9083","--conf","spark.eventLog.enabled=true","--conf","spark.executor.heartbeatInterval=30s","--conf","spark.executor.memoryOverhead=512","--conf","spark.sql.adaptive.enabled=true","--conf","spark.sql.adaptive.skewJoin.enabled=true","--conf","spark.driver.maxResultSize=10g","--conf","spark.sql.orc.enableVectorizedReader=false","--num-executors","1","--conf","spark.extraListeners=xmatrix.manager.ApplicationListener","--master","yarn","--conf","spark.driver.extraLibraryPath=hdfs://nameservice1/user/spark2/app/cube/jar/lib/2.3.2","--queue","spark2","--conf","spark.hadoop.hive.exec.max.dynamic.partitions.pernode=100","--conf","spark.kryoserializer.buffer.max=512m","--conf","spark.hadoop.hive.exec.dynamic.partition=false","--conf","spark.yarn.historyServer.address=192.168.12.102:18089","--conf","spark.yarn.historyServer.allowTracking=true","--conf","spark.hadoop.hive.exec.max.dynamic.partitions=1000","--conf","spark.memory.storageFraction=0.5","--conf","spark.sql.crossJoin.enabled=true","--executor-cores","1","--conf","spark.network.timeout=60000","--conf","spark.memory.offHeap.enabled=false","--conf","spark.yarn.am.extraLibraryPath=hdfs://nameservice1/user/spark2/app/cube/jar/lib/2.3.2","--conf","spark.scheduler.mode=FIFO","--deploy-mode","cluster","--conf","spark.locality.wait=0ms","--conf","spark.driver.memory=4g","--conf","spark.tispark.pd.addresses=192.168.8.16:9101","--conf","spark.hadoop.hive.exec.max.created.files=100000","--conf","spark.ui.killEnable=true","--conf","spark.driver.memoryOverhead=512","--conf","spark.hadoop.hive.exec.dynamic.partition.mode=strict","--conf","spark.default.parallelism=200","--conf","spark.dynamicAllocation.maxExecutors=30","--conf","spark.yarn.max.executor.failures=10","--conf","spark.sql.adaptive.coalescePartitions.enabled=true","--conf","spark.kryoserializer.buffer=128m","--conf","spark.hadoop.dfs.replication=3","--conf","spark.driver.extraJavaOptions=-Xms4g -XX:MetaspaceSize=160m -XX:MaxMetaspaceSize=256m -XX:+PrintGCDetails -XX:-UseGCOverheadLimit -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=/tmp/xmatrix/gc/ -XX:+UnlockExperimentalVMOptions -XX:NewRatio=1 -XX:SurvivorRatio=6 -XX:+UseG1GC -XX:MaxGCPauseMillis=100 -XX:GCPauseIntervalMillis=200 -XX:InitiatingHeapOccupancyPercent=60 -XX:G1HeapRegionSize=16M","--conf","spark.sql.hive.convertMetastoreOrc=false","--conf","spark.shuffle.service.port=7337","--conf","spark.sql.broadcastTimeout=300","--conf","spark.sql.codegen=true","--conf","spark.authenticate=false","--class","xmatrix.XmatrixApp","--conf","spark.executor.extraLibraryPath=hdfs://nameservice1/user/spark2/app/cube/jar/lib/2.3.2","--conf","spark.shuffle.file.buffer=64k","--conf","spark.eventLog.dir=hdfs://nameservice1/user/spark/applicationHistory","--conf","spark.dynamicAllocation.enabled=false","--conf","spark.sql.statistics.fallBackToHdfs=true","--conf","spark.memory.fraction=0.6","--conf","spark.dynamicAllocation.minExecutors=1","--conf","spark.sql.orc.impl=native","--conf","spark.shuffle.memoryFraction=0.2","--conf","spark.sql.extensions=org.apache.spark.sql.TiExtensions","--executor-memory","2g","--conf","spark.dynamicAllocation.executorIdleTimeout=60","--conf","spark.shuffle.consolidateFiles=true","--conf","spark.task.maxFailures=10","--conf","spark.sql.shuffle.partitions=1000","hdfs://nameservice1/user/spark2/app/cube/jar/proj/xmatrix-3.1.8_2.3.2.jar","-xmatrix.type","stream","-xmatrix.carbondata.store","hdfs://nameservice1/user/hive/carbonstore/default","-xmatrix.udfClassPath","antiy.udf.sparksql.UDF","-xmatrix.name","recovery_test-f33c1185309a47a59a53a2a09f462ea8","-xmatrix.enableCarbonDataSupport","false","-xmatrix.zookeeper","hadoop-tworker05:2181,hadoop-tworker04:2181,hadoop-tworker06:2181","-xmatrix.zkPathPrefix","/xmatrix/app","-xmatrix.tispark","false","-xmatrix.enableHiveSupport","true","-xmatrix.metrics.kafka","192.168.48.193:9092","-xmatrix.carbondata.meta","hdfs://nameservice1/user/hive/carbonstore/default")
      "submit success" in {
        val context = new URLClassLoader((hadoopConfigFiles +: sparkJarFiles).toArray, null)
        submitSparkApplicationAsync(context, appArgs)
      }

    }
  }
}


