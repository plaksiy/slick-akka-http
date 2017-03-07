package rest

import org.scalatest._
import persistence.entities._
import persistence.entities.BookRepository._
import utils.{ConfigurationModuleImpl, PersistenceModuleImpl}

import scala.concurrent.{Await, Future}
import scala.concurrent.duration.Duration
import scala.concurrent.ExecutionContext.Implicits.global


abstract class DBTest extends WordSpec with Matchers with BeforeAndAfterEach with BeforeAndAfterAll{

  var modules: PersistenceModuleImpl with ConfigurationModuleImpl = _

  override protected def beforeAll(): Unit = {
    super.beforeAll()
    val modulesInner = new ConfigurationModuleImpl with PersistenceModuleImpl
    import modulesInner.profile.api._
    Await.result(modulesInner.db.run(modulesInner.authorsDal.tableQuery.schema.create), Duration.Inf)
    Await.result(modulesInner.db.run(modulesInner.booksDal.tableQuery.schema.create), Duration.Inf)
    modules = modulesInner
  }

/*
  override protected def afterEach(): Unit = {
    super.afterEach()
    val modulesInner = modules
    import modulesInner.profile.api._

    Await.result(modulesInner.db.run(sqlu"TRUNCATE TABLE AUTHORS;"), Duration.Inf)
  }
*/

  def await[T](f: Future[T]) = Await.result(f, Duration.Inf)

  def addAuthor(author: Author) = await(modules.db.run(modules.authorsDal.save(author)))
  def getAuthor(authorId: Long) = await(modules.db.run(modules.authorsDal.findOne(authorId)))
  def getAllAuthors = await(modules.db.run(modules.authorsDal.findAll()))
  def authorsCount = await(modules.db.run(modules.authorsDal.count()))
  def updateAuthor(author: Author) = await(modules.db.run(modules.authorsDal.update(author)))
  def deleteAuthor(authorId: Long) = await(modules.db.run(modules.authorsDal.delete(authorId)))

  def addBook(book: Book) = await(modules.db.run(modules.booksDal.save(book)))
  def getBook(bookId: Long) = await(modules.db.run(modules.booksDal.findOne(bookId)))
  def getAllBooks = await(modules.db.run(modules.booksDal.findAll()))
  def booksCount = await(modules.db.run(modules.booksDal.count()))
  def updateBook(book: Book) = await(modules.db.run(modules.booksDal.update(book)))
  def deleteBook(bookId: Long) = await(modules.db.run(modules.booksDal.delete(bookId)))

  def getBookWithAuthor(bookId: Long) = await(modules.db.run(modules.booksDal.getBookWithAuthor(bookId)))
  def getAllBooksWithAuthor = await(modules.db.run(modules.booksDal.getAllBooksWithAuthor))
  def getBooksByIdsWithAuthor(bookIds: Set[Long]) = await(modules.db.run(modules.booksDal.getBooksByIdsWithAuthor(bookIds)))
}


object TestData {
  val trask = Author(None, "Andrew", "W.", "Trask")
  val saumont = Author(None, "Pierre", "Yves", "Saumont")
  val fain = Author(None, "Yakov", "", "Fain")
  val moiseev = Author(None, "Anton", "", "Moiseev")
  val walls = Author(None, "Craig", "", "Walls")

//  author - "trask"
  val grokking = Book(None, "Grokking Deep Learning", "Grokking Deep Learning is the perfect place to begin your deep learning journey.", 0, 2016 )
//  author - "saumont"
  val fpJava = Book(None, "Functional Programming in Java", "Functional Programming in Java teaches Java developers how to incorporate the most powerful benefits of functional programming into new and existing Java code.", 0, 2017 )
//  author - "fain"
//  author - "moiseev"
  val angular2 = Book(None, "Angular 2 Development with TypeScript", "Angular 2 Development with Typescript teaches you what you need to start using Angular, while you also learn TypeScript and how to take advantage of its benefits.", 0, 2016 )
//  author - "walls"
  val spring = Book(None, "Spring in Action", "Spring in Action introduces you to the ideas behind Spring and then quickly launches into a hands-on exploration of the framework.", 0, 2005 )
//  author - "walls"
  val spring2ed = Book(None, "Spring in Action, Second Edition", "In this second edition, Spring in Action has been completely updated to cover the exciting new features of Spring 2.0.", 0, 2007 )
//  author - "walls"
  val spring3ed = Book(None, "Spring in Action, Third Edition", "Totally revised for Spring 3.0, Spring in Action, Third Edition is a hands-on guide to the Spring Framework.", 0, 2011 )
//  author - "walls"
  val spring4ed = Book(None, "Spring in Action, Fourth Edition", "Spring in Action, Fourth Edition is a hands-on guide to the Spring Framework, updated for version 4.", 0, 2014 )
}