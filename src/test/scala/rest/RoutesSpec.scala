package rest

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import entities.JsonProtocol
import persistence.entities.{SimpleSupplier, Supplier}
import akka.http.scaladsl.model.StatusCodes._
import JsonProtocol._
import SprayJsonSupport._
import akka.http.scaladsl.server.ValidationRejection
import org.scalatest.{BeforeAndAfter, BeforeAndAfterAll}
import slick.dbio.DBIOAction

import scala.concurrent.{Await, ExecutionContext, Future}
import scala.concurrent.duration.Duration

class RoutesSpec extends AbstractRestTest {

  def actorRefFactory = system
  val modules = new Modules {}
  val suppliers = new SupplierRoutes(modules)

  "Supplier Routes" should {

    "return an empty array of suppliers" in {
      val dbAction: modules.suppliersDal.driver.api.DBIO[Option[Supplier]] = DBIOAction.from(Future(None))
      modules.suppliersDal.findOne(1) returns dbAction
      Get("/supplier/1") ~> suppliers.routes ~> check {
        handled shouldEqual true
        status shouldEqual NotFound
      }
    }

    "return an empty array of suppliers when ask for supplier Bad Request when the supplier is < 1" in {
      val dbAction: modules.suppliersDal.driver.api.DBIO[Option[Supplier]] = DBIOAction.from(Future(None))
      modules.suppliersDal.findOne(1) returns dbAction
      Get("/supplier/0") ~> suppliers.routes ~> check {
        handled shouldEqual false
        rejection shouldEqual ValidationRejection("The supplier id should be greater than zero", None)
      }
    }


    "return an array with 1 suppliers" in {
      val dbAction: modules.suppliersDal.driver.api.DBIO[Option[Supplier]] = DBIOAction.from(Future(Some(Supplier(Some(1), "name", "desc"))))
      modules.suppliersDal.findOne(1) returns dbAction
      Get("/supplier/1") ~> suppliers.routes ~> check {
        handled shouldEqual true
        status shouldEqual OK
        responseAs[Option[Supplier]].isEmpty shouldEqual false
      }
    }

    "create a supplier with the json in post" in {
      val dbAction: modules.suppliersDal.driver.api.DBIO[Supplier] = DBIOAction.from(Future.successful(Supplier(None,"name 1","desc 1")))
      modules.suppliersDal.save(any[Supplier])(any[ExecutionContext]) returns dbAction
      Post("/supplier",SimpleSupplier("name 1","desc 1")) ~> suppliers.routes ~> check {
        handled shouldEqual true
        status shouldEqual Created
      }
    }


    "not handle the invalid json" in {
      Post("/supplier","{\"name\":\"1\"}") ~> suppliers.routes ~> check {
        handled shouldEqual false
      }
    }

    "not handle an empty post" in {
      Post("/supplier") ~> suppliers.routes ~> check {
        handled shouldEqual false
      }
    }

  }

}