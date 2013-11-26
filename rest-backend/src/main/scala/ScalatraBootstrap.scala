import _root_.akka.actor.ActorSystem
import com.mongodb.casbah.MongoClient
import org.scalatra.example._
import org.scalatra._
import javax.servlet.ServletContext

class ScalatraBootstrap extends LifeCycle {

  override def init(context: ServletContext) {

    context.mount(new AppServlet, "/rest/*")
  }

}
