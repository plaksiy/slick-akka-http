import akka.http.scaladsl.Http
import akka.http.scaladsl.server.RouteConcatenation
import akka.stream.ActorMaterializer
import rest.{AuthorRoutes, BookRoutes, SupplierRoutes}
import utils._

import scala.concurrent.Await
import scala.concurrent.duration.Duration

object Main extends App with RouteConcatenation with CorsSupport{
  // configuring modules for application, cake pattern for DI
  val modules = new ConfigurationModuleImpl  with ActorModuleImpl with PersistenceModuleImpl
  implicit val system = modules.system
  implicit val materializer = ActorMaterializer()
  implicit val ec = modules.system.dispatcher

  import modules.profile.api._
  Await.result(modules.db.run(modules.suppliersDal.tableQuery.schema.create), Duration.Inf)
  Await.result(modules.db.run(modules.authorsDal.tableQuery.schema.create), Duration.Inf)
  Await.result(modules.db.run(modules.booksDal.tableQuery.schema.create), Duration.Inf)


  val swaggerService = new SwaggerDocService(system)

  val bindingFuture = Http().bindAndHandle(
    new AuthorRoutes(modules).routes ~
    new BookRoutes(modules).routes ~
    new SupplierRoutes(modules).routes ~
    swaggerService.assets ~
    corsHandler(swaggerService.routes), "localhost", 8080)

  println(s"Server online at http://localhost:8080/")

}