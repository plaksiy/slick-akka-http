package rest

import persistence.entities.{Author, SimpleAuthor}

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


@Path("/author")
@Api(value = "/author", produces = "application/json")
class AuthorRoutes(modules: Configuration with PersistenceModule with DbModule) extends Directives {
  import modules.executeOperation

  @Path("/{id}")
  @ApiOperation(value = "Return Author", notes = "", nickname = "", httpMethod = "GET")
  @ApiImplicitParams(Array(
    new ApiImplicitParam(name = "id", value = "Author Id", required = false, dataType = "int", paramType = "path")
  ))
  @ApiResponses(Array(
    new ApiResponse(code = 200, message = "Return Author", response = classOf[Author]),
    new ApiResponse(code = 400, message = "The author id should be greater than zero"),
    new ApiResponse(code = 404, message = "Return Author Not Found"),
    new ApiResponse(code = 500, message = "Internal server error")
  ))
  def authorGetRoute = path("author" / IntNumber) { (autId) =>
    get {
      validate(autId > 0, "The author id should be greater than zero") {
        onComplete(modules.authorsDal.findOne(autId)) {
          case Success(authorOpt) => authorOpt match {
            case Some(aut) => complete(aut)
            case None => complete(NotFound, s"The author doesn't exist")
          }
          case Failure(ex) => complete(InternalServerError, s"An error occurred: ${ex.getMessage}")
        }
      }
    }
  }

  @ApiOperation(value = "Add author", notes = "", nickname = "", httpMethod = "POST", produces = "text/plain")
  @ApiImplicitParams(Array(
    new ApiImplicitParam(name = "body", value = "Author Object", required = true,
      dataType = "persistence.entities.SimpleAuthor", paramType = "body")
  ))
  @ApiResponses(Array(
    new ApiResponse(code = 500, message = "Internal server error"),
    new ApiResponse(code = 400, message = "Bad Request"),
    new ApiResponse(code = 201, message = "Entity Created")
  ))
  def authorPostRoute = path("author") {
    post {
      entity(as[SimpleAuthor]) { authorToInsert =>
        onComplete(modules.authorsDal.save(Author(None, authorToInsert.firstName, authorToInsert.secondName, authorToInsert.lastName))) {
          case Success(_) => complete(Created)
          case Failure(ex) => complete(InternalServerError, s"An error occurred: ${ex.getMessage}")
        }
      }
    }
  }

  val routes: Route = authorPostRoute ~ authorGetRoute

}