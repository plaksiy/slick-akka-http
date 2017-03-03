package persistence.entities

import com.byteslounge.slickrepo.meta.{Entity, Keyed}
import com.byteslounge.slickrepo.repository.Repository
import slick.ast.BaseTypedType
import slick.driver.JdbcProfile

import scala.concurrent.Future
import scala.language.implicitConversions


case class SimpleBook(title: String, desc: String, authorId: Long, issueYear: Int)

case class Book(
     override val id: Option[Int],
          title     : String,
          desc      : String,
          authorId  : Long,
          issueYear : Int) extends Entity[Book, Int] {

  def withId(id: Int): Book = this.copy(id = Some(id))
}

case class BookObj (id: Option[Int], title: String, desc: String, author: Author, issueYear: Int)

class BookRepository(db: JdbcProfile#Backend#Database, override val driver: JdbcProfile, authorRep: AuthorRepository) extends Repository[Book, Int](driver) {
  import driver.api._
  val pkType = implicitly[BaseTypedType[Int]]
  val tableQuery = TableQuery[Books]
  type TableType = Books

  class Books(tag: slick.lifted.Tag) extends Table[Book](tag, "BOOKS") with Keyed[Int] {
    def id = column[Int]("ID", O.PrimaryKey, O.AutoInc)
    def title = column[String]("title")
    def desc = column[String]("desc")
    def authorId = column[Long]("authorId")
    def issueYear = column[Int]("issueYear")
    def * = (id.?, title, desc, authorId, issueYear) <> ((Book.apply _).tupled, Book.unapply)
  }

  def getBookWithAuthor(bookId: Int): Future[Option[BookObj]] = {
    val query = for {
      book <- tableQuery if book.id === bookId
      author <- authorRep.tableQuery if book.authorId === author.id
    } yield (book, author)

    db.run(query.result.headOption).map(o => o.map(b => b: BookObj))
  }

  implicit def toBookObj(obj: (Book, Author)): BookObj = {
    val (book, author) = obj
    BookObj(book.id, book.title, book.desc, author,book.issueYear)
  }
}
