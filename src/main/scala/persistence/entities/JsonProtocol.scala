package entities

import persistence.entities._
import persistence.entities.BookRepository._

import spray.json.DefaultJsonProtocol

object JsonProtocol extends DefaultJsonProtocol {
  implicit val supplierFormat = jsonFormat3(Supplier)
  implicit val simpleSupplierFormat = jsonFormat2(SimpleSupplier)

  implicit val authorFormat = jsonFormat4(Author)
  implicit val simpleAuthorFormat = jsonFormat3(SimpleAuthor)

  implicit val bookFormat = jsonFormat5(Book)
  implicit val bookObjFormat = jsonFormat5(BookObj)
  implicit val simpleBookFormat = jsonFormat4(SimpleBook)
}