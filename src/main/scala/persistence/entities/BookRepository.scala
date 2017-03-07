package persistence.entities

import com.byteslounge.slickrepo.meta.{Entity, Keyed}
import com.byteslounge.slickrepo.repository.Repository
import slick.ast.BaseTypedType
import slick.driver.JdbcProfile

import scala.concurrent.Future
import scala.language.implicitConversions

import scala.concurrent.ExecutionContext.Implicits.global

import BookRepository._

class BookRepository(db: JdbcProfile#Backend#Database, override val driver: JdbcProfile, val authorRep: AuthorRepository) extends Repository[Book, Long](driver) {
  import driver.api._
  val pkType = implicitly[BaseTypedType[Long]]
  val tableQuery = TableQuery[Books]
  type TableType = Books

  class Books(tag: slick.lifted.Tag) extends Table[Book](tag, "BOOKS") with Keyed[Long] {
    def id = column[Long]("ID", O.PrimaryKey, O.AutoInc)
    def title = column[String]("title")
    def desc = column[String]("desc")
    def authorId = column[Long]("authorId")
    def issueYear = column[Int]("issueYear")
    def author = foreignKey("AUT_FK", authorId, authorRep.tableQuery)(_.id, onUpdate = ForeignKeyAction.Restrict, onDelete = ForeignKeyAction.Cascade)
    def * = (id.?, title, desc, authorId, issueYear) <> ((Book.apply _).tupled, Book.unapply)
  }


  def getBookWithAuthor(bookId: Long) = caseClassJoinResults(Option(Set(bookId))).map(_.headOption)
  def getAllBooksWithAuthor = caseClassJoinResults(None)
  def getBooksByIdsWithAuthor(bookIds: Set[Long]) = caseClassJoinResults(Option(bookIds))

  private def caseClassJoinResults(bookIds: Option[Set[Long]]) = tupledJoin(bookIds).result.map(_.map(BookWithAuthor.tupled))
  private def tupledJoin(bookIds: Option[Set[Long]]) = {
    val bookQuery = bookIds match {
      case None => tableQuery
      case Some(ids) => tableQuery.filter(_.id inSet ids)
    }
    bookQuery join authorRep.tableQuery on (_.authorId === _.id)
  }
}

object BookRepository {

  case class SimpleBook(title: String, desc: String, authorId: Long, issueYear: Int)

  case class Book(override val id: Option[Long], title: String, desc: String, authorId: Long, issueYear : Int) extends Entity[Book, Long] {
    def withId(id: Long): Book = this.copy(id = Some(id))
    def withAuthorId(_authorId: Long): Book = this.copy(authorId = _authorId)
  }

  case class BookObj (id: Option[Long], title: String, desc: String, author: Author, issueYear: Int)

  case class BookWithAuthor(book: Book, author: Author)

  implicit def bookWithAuthor2BookObj(bookWithAuthor: BookWithAuthor): BookObj = {
    val BookWithAuthor(book, author) = bookWithAuthor
    BookObj(book.id, book.title, book.desc, author, book.issueYear)
  }

}
