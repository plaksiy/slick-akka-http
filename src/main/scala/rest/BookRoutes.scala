package rest

import persistence.entities.{Book, SimpleBook}

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.server.{Directives, Route}

import entities.JsonProtocol
import utils.{Configuration, DbModule, PersistenceModule}
import scala.util.{Failure, Success}
import JsonProtocol._
import SprayJsonSupport._

import javax.ws.rs.Path
import io.swagger.annotations._

import scala.concurrent.ExecutionContext.Implicits.global


@Path("/book")
@Api(value = "/book", produces = "application/json")
class BookRoutes(modules: Configuration with PersistenceModule with DbModule) extends Directives {
  import modules.executeOperation

  @Path("/{id}")
  @ApiOperation(value = "Return Book", notes = "", nickname = "", httpMethod = "GET")
  @ApiImplicitParams(Array(
    new ApiImplicitParam(name = "id", value = "Book Id", required = false, dataType = "int", paramType = "path")
  ))
  @ApiResponses(Array(
    new ApiResponse(code = 200, message = "Return Book", response = classOf[Book]),
    new ApiResponse(code = 400, message = "The book id should be greater than zero"),
    new ApiResponse(code = 404, message = "Return Book Not Found"),
    new ApiResponse(code = 500, message = "Internal server error")
  ))
  def bookGetRoute = path("book" / IntNumber) { (bookId) =>
    get {
      validate(bookId > 0, "The book id should be greater than zero") {

        onComplete(modules.booksDal.findOne(bookId)) {
          case Success(bookOpt) => bookOpt match {
            case Some(book) => complete(book)
            case None => complete(NotFound, s"The book doesn't exist")
          }
          case Failure(ex) => complete(InternalServerError, s"An error occurred: ${ex.getMessage}")
        }
      }
    }
  }

  @ApiOperation(value = "Add Book", notes = "", nickname = "", httpMethod = "POST", produces = "text/plain")
  @ApiImplicitParams(Array(
    new ApiImplicitParam (name = "body", value = "Book Object", required = true,
      dataType = "persistence.entities.SimpleBook", paramType = "body")
  ))
  @ApiResponses(Array(
    new ApiResponse(code = 500, message = "Internal server error"),
    new ApiResponse(code = 400, message = "Bad Request"),
    new ApiResponse(code = 201, message = "Entity Created")
  ))
  def bookPostRoute = path("book") {
    post {
      entity(as[SimpleBook]) { bookToInsert =>
        onComplete(modules.booksDal.save(Book(None, bookToInsert.title, bookToInsert.desc, bookToInsert.authorId, bookToInsert.issueYear))) {
          case Success(_) => complete(Created)
          case Failure(ex) => complete(InternalServerError, s"An error occurred: ${ex.getMessage}")
        }
      }
    }
  }

  val routes: Route = bookPostRoute ~ bookGetRoute
}
