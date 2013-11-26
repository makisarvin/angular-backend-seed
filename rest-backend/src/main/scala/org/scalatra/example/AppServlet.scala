package org.scalatra.example

import org.scalatra.json.JacksonJsonSupport
import org.scalatra.{ScalatraServlet, GZipSupport, CorsSupport}
import org.json4s._
import org.json4s.JsonDSL._
import org.scalatra.scalate.ScalateSupport

class AppServlet extends ScalatraServlet with ScalateSupport with JacksonJsonSupport
  with CorsSupport with GZipSupport {

  protected implicit val jsonFormats: Formats = DefaultFormats

  /* Set up all the routes to return Json */
  before() {
    contentType = formats("json")
    response.headers += ("Access-Control-Allow-Origin" -> "*")
  }
  /**
   * Add CORS support
   */
//  options("/*") {
//    response.setHeader("Access-Control-Allow-Headers", request.getHeader("Access-Control-Request-Headers"))
//  }

  get("/") {
    val json = ("message" -> "Hello world")
    compact(render(json))
  }

}
