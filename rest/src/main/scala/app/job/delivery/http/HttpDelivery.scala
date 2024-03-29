package app.job.delivery.http

/**
  * Created by iodone on {19-11-25}.
  */

import scala.concurrent.ExecutionContext
import akka.http.scaladsl.model.{ContentTypes, HttpEntity, HttpMethods}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import io.circe.generic.auto._


import core.Delivery
import core.Router
import app.job.service._
import app.job.repositry._
import app.job.delivery.http.router._
import common.middleware.LogginReqDirectives._


case class HttpDelivery(implicit ec: ExecutionContext) extends Delivery[Route] {

  def handle = {
    appRouter.routes
  }

  // DI
  import com.softwaremill.macwire._
  lazy val ordeRepo = wire[OrderRepositryImp]
  lazy val os = wire[OrderServiceImp]
  lazy val or = wire[OrderRouter]


  val appRouter: Router = new Router {
    override def routes: Route = {
      loggingReqInfo {
        pathPrefix("api") {
          pathPrefix("v1") {
            or.routes
          }
        } ~
          pathPrefix("healthcheck") {
            get {
              complete(HttpEntity(ContentTypes.`application/json`, """{"healthStatus": "OK"}"""))
            }
          }
      }
    }
  }
}
