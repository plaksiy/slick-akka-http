package utils

import com.byteslounge.slickrepo.repository.Repository
import slick.backend.DatabaseConfig
import slick.driver.JdbcProfile
import persistence.entities._
import persistence.entities.BookRepository._
import slick.dbio.DBIO

import scala.concurrent.Future


trait Profile {
	val profile: JdbcProfile
}


trait DbModule extends Profile{
	val db: JdbcProfile#Backend#Database

	implicit def executeOperation[T](databaseOperation: DBIO[T]): Future[T] = {
		db.run(databaseOperation)
	}

}

trait PersistenceModule {
	val suppliersDal: Repository[Supplier, Int]
	val authorsDal: Repository[Author, Long]
	val booksDal: Repository[Book, Long]
}


trait PersistenceModuleImpl extends PersistenceModule with DbModule{
	this: Configuration  =>

	// use an alternative database configuration ex:
	// private val dbConfig : DatabaseConfig[JdbcProfile]  = DatabaseConfig.forConfig("pgdb")
	private val dbConfig : DatabaseConfig[JdbcProfile]  = DatabaseConfig.forConfig("h2db")

	override implicit val profile: JdbcProfile = dbConfig.driver
	override implicit val db: JdbcProfile#Backend#Database = dbConfig.db

	override val suppliersDal = new SupplierRepository(profile)
	override val authorsDal = new AuthorRepository(profile)
	override val booksDal = new BookRepository(db, profile, authorsDal)

}
