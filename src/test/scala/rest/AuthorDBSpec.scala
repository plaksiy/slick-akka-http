package rest

import persistence.entities.{Author, Book}

import scala.concurrent.ExecutionContext.Implicits.global


class AuthorDBSpec extends DBTest {
  import rest.TestData._

  "Author Repository" should {

    "return an empty array of authors" in {
      val authors = getAllAuthors()
      authors.isEmpty shouldEqual true
    }

    "create an author" in {
      addAuthor(trask)
      val author = getAuthor(1)
      author shouldBe defined
    }

    "create an book" in {
      val author = addAuthor(trask)
//      val  book = addBook(Book(None, "", "", author.id, 2001))
    }
  }

}
