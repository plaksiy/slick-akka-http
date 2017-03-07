package rest

import persistence.entities.BookRepository._


class BookDBSpec extends DBTest {
  import rest.TestData._

  "Book Repository" should {

    "return an empty array of books" in {
      val books = getAllBooks
      books.isEmpty shouldEqual true
      booksCount shouldBe 0
    }

    "create an book" in {
      val countBefore = booksCount
      val author = addAuthor(trask)
      val book = addBook(grokking.withAuthorId(author.id.get))

      book.title shouldBe grokking.title
      book.authorId shouldBe author.id.get
      booksCount shouldBe (countBefore + 1)
    }

    "get one book" in {
      val countBefore = booksCount
      val author = addAuthor(trask)
      val book = addBook(grokking.withAuthorId(author.id.get))

      val restoreBook = getBook(book.id.get).get
      restoreBook.title shouldBe grokking.title
      restoreBook.authorId shouldBe author.id.get
      booksCount shouldBe (countBefore + 1)
    }

    "return array of books" in {
      val countBefore = booksCount
      addBook(fpJava.withAuthorId(addAuthor(saumont).id.get))
      addBook(angular2.withAuthorId(addAuthor(fain).id.get))

      getAllBooks.size shouldEqual booksCount
      booksCount shouldEqual (countBefore + 2)
    }

    "delete a book" in {
      val countBefore = booksCount

      val book = addBook(angular2.withAuthorId(addAuthor(moiseev).id.get))
      booksCount shouldEqual (countBefore + 1)

      deleteAuthor(book.id.get)
      booksCount shouldEqual countBefore
    }

    "update a book" in {
      val book = addBook(angular2.withAuthorId(addAuthor(moiseev).id.get))
      val countBefore = booksCount

      val _saumont = addAuthor(saumont)
      val updatedBook = updateBook(fpJava.withId(book.id.get).withAuthorId(_saumont.id.get))
      booksCount shouldEqual countBefore
      book.id.get shouldBe updatedBook.id.get
      updatedBook.authorId shouldEqual _saumont.id.get
      updatedBook.title shouldEqual fpJava.title
    }

    "get a book with author" in {
      getAllBooks.map(book => deleteBook(book.id.get))

      // get one book
      val countBefore = booksCount
      val _trask = addAuthor(trask)
      val _grokking = addBook(grokking.withAuthorId(_trask.id.get))

      val oneBook = getBookWithAuthor(_grokking.id.get).get
      oneBook.book.id.get shouldEqual _grokking.id.get
      oneBook.author.id.get shouldEqual _grokking.authorId
      booksCount shouldEqual (countBefore + 1)

      // get two books
      val _walls = addAuthor(walls)
      val _spring = addBook(spring.withAuthorId(_walls.id.get))
      val _spring2ed = addBook(spring2ed.withAuthorId(_walls.id.get))

      val twoBooksWithAuthor = getBooksByIdsWithAuthor(Set(_spring.id.get, _spring2ed.id.get))
      twoBooksWithAuthor.size shouldBe 2
      booksCount shouldEqual (countBefore + 3)
      twoBooksWithAuthor.head.book.id.get shouldEqual _spring.id.get
      twoBooksWithAuthor(1).book.id.get shouldEqual _spring2ed.id.get

      // get all books
      addBook(angular2.withAuthorId(addAuthor(moiseev).id.get))

      val allBooksWithAuthor = getAllBooksWithAuthor
      booksCount shouldEqual (countBefore + 4)
    }
  }

}
