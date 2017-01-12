package rest

import org.scalatest.{Matchers, WordSpec}
import org.specs2.mock.Mockito
import persistence.entities.{Supplier, SupplierRepository}
import utils.{ActorModule, ConfigurationModuleImpl, DbModule, PersistenceModule}
import com.typesafe.config.Config
import com.typesafe.config.ConfigFactory
import akka.http.scaladsl.testkit.ScalatestRouteTest
import com.byteslounge.slickrepo.repository.Repository
import slick.backend.DatabaseConfig
import slick.driver.JdbcProfile

trait AbstractRestTest  extends WordSpec with Matchers with ScalatestRouteTest with Mockito{

  trait Modules extends ConfigurationModuleImpl with ActorModule with PersistenceModule with DbModule{
    val system = AbstractRestTest.this.system

    private val dbConfig : DatabaseConfig[JdbcProfile]  = DatabaseConfig.forConfig("h2db")
    override implicit val profile: JdbcProfile = dbConfig.driver
    override implicit val db: JdbcProfile#Backend#Database = dbConfig.db
    override val suppliersDal = mock[Repository[Supplier, Int]]
  }

  def getConfig: Config = ConfigFactory.empty();


}
