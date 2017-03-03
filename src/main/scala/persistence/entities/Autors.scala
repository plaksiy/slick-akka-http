package persistence.entities

import com.byteslounge.slickrepo.meta.{Entity, Keyed}
import com.byteslounge.slickrepo.repository.Repository
import slick.ast.BaseTypedType
import slick.driver.JdbcProfile

case class SimpleAuthor(firstName: String, secondName: String, lastName: String)

case class Author(
          override val id : Option[Int],
                firstName : String,
                secondName: String,
                lastName  : String) extends Entity[Author, Int] {

  def withId(id: Int): Author = this.copy(id = Some(id))
}

class AuthorRepository(override val driver: JdbcProfile) extends Repository[Author, Int](driver) {
  import driver.api._
  val pkType = implicitly[BaseTypedType[Int]]
  val tableQuery = TableQuery[Authors]
  type TableType = Authors

  class Authors(tag: slick.lifted.Tag) extends Table[Author](tag, "AUTHORS") with Keyed[Int] {
    def id = column[Int]("ID", O.PrimaryKey, O.AutoInc)
    def firstName = column[String]("firstName")
    def secondName = column[String]("secondName")
    def lastName = column[String]("lastName")
    def * = (id.?, firstName, secondName, lastName) <> ((Author.apply _).tupled, Author.unapply)
  }
}