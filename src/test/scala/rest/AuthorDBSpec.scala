package rest


class AuthorDBSpec extends DBTest {
  import rest.TestData._

  "Author Repository" should {

    "return an empty array of authors" in {
      val authors = getAllAuthors
      authors.isEmpty shouldEqual true
      authorsCount shouldEqual 0
    }

    "create an author" in {
      val countBefore = authorsCount
      val author = addAuthor(trask)

      author.firstName shouldBe trask.firstName
      authorsCount shouldEqual (countBefore + 1)
    }

    "get one author" in {
      val countBefore = authorsCount
      val author = getAuthor(
                              addAuthor(trask).id.get
                            ).get

      author.firstName shouldBe trask.firstName
      authorsCount shouldEqual (countBefore + 1)
    }

    "return array of authors" in {
      val countBefore = authorsCount
      addAuthor(saumont)
      addAuthor(fain)

      getAllAuthors.size shouldEqual authorsCount
      authorsCount shouldEqual (countBefore + 2)
    }

    "delete an author" in {
      val countBefore = authorsCount

      val author = addAuthor(saumont)
      authorsCount shouldEqual (countBefore + 1)

      deleteAuthor(author.id.get)
      authorsCount shouldEqual countBefore
    }

    "update an author" in {
      val _trask = addAuthor(trask)
      val countBefore = authorsCount

      val authorId = _trask.id.get
      val _saumont = saumont.withId(authorId)

      val updatedAuthor = updateAuthor(_saumont)
      authorsCount shouldEqual countBefore
      updatedAuthor.id.get shouldEqual _trask.id.get
      updatedAuthor.firstName shouldEqual saumont.firstName
    }
  }

}
