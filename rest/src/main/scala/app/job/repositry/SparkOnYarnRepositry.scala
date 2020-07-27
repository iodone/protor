package app.job.repositry

/**
  * Created by iodone on {20-07-23}.
  */

import java.net.URLClassLoader

import scala.reflect.runtime.{universe => ru}
import scala.util.{Success, Failure}

object SubmitUtil {

  def submitSparkApplicationAsync(env: URLClassLoader, args: Array[String]): Unit = {

    val mirror = ru.runtimeMirror(env)
    val companion = mirror.reflectModule(mirror.staticModule("org.apache.spark.deploy.SparkSubmit")).instance
    val target = companion.getClass.getMethod("main", classOf[Array[String]])
    val thread = new Thread(new Runnable {
      override def run(): Unit = {
        target.invoke(companion, args)
      }
    })
    thread.setContextClassLoader(env)
    thread.start()
    thread.join(30*1000)
  }

  def submitSparkApplicationSync(env: URLClassLoader, clientArgs:Array[String], sparkArgs: Array[String], timeoutMills: Long = 0, version: String = "3") = {
    val clientClass = env.loadClass("org.apache.spark.deploy.yarn.Client")
    val sparkConfClass = env.loadClass("org.apache.spark.SparkConf")
    val clientArgClass = env.loadClass("org.apache.spark.deploy.yarn.ClientArguments")
    val rpcEnvClass = env.loadClass("org.apache.spark.rpc.RpcEnv")

    val submitApplicationMethod = clientClass.getDeclaredMethod("submitApplication")

    @volatile var appId:String = null
    @volatile var maybeException: Option[Throwable] = None
    val thread = new Thread(() => {
        val clientArgInstance = clientArgClass.getConstructor(classOf[Array[String]]).newInstance(Array[AnyRef](clientArgs):_*)
        val sparkConfInstance = sparkConfClass.getConstructor(classOf[Boolean]).newInstance(Array[AnyRef](true.asInstanceOf[AnyRef]):_*)
        val setterMethod = sparkConfClass.getDeclaredMethod("set", classOf[String], classOf[String])

        sparkArgs.grouped(2).foreach {
          case Array("--conf", kv) =>
            val args = kv.split("=",2)
            setterMethod.invoke(sparkConfInstance, args: _*)
          case _ =>
        }

        val clientInstance = if (version.startsWith("3")) {
          clientClass.getConstructor(clientArgClass, sparkConfClass, rpcEnvClass).newInstance(Array[AnyRef](clientArgInstance.asInstanceOf[AnyRef],
            sparkConfInstance.asInstanceOf[AnyRef], null.asInstanceOf[AnyRef]):_*)
        } else {
           clientClass.getConstructor(clientArgClass, sparkConfClass).newInstance(Array[AnyRef](clientArgInstance.asInstanceOf[AnyRef],
            sparkConfInstance.asInstanceOf[AnyRef]):_*)
        }
        appId = submitApplicationMethod.invoke(clientInstance).toString
    })

    thread.setContextClassLoader(env)
    thread.setUncaughtExceptionHandler((_: Thread, e: Throwable) => {maybeException = Some(e) })
    thread.start()
    thread.join(timeoutMills)

    maybeException match {
      case None =>
        if (appId == null) Failure(new Exception(s"Submitting application takes too long beyond the specified timeout(${timeoutMills}ms) "))
        else Success(appId)
      case Some(e) => Failure(e)
    }
  }

}


