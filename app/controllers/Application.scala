package controllers

import models.Product
import play.api._
import play.api.mvc._

object Application extends Controller {

  def index = Action {
    Redirect(routes.Application.list())
  }

  def list = Action { implicit request =>
    val products = Product.findAll
    Ok(views.html.products.list(products))
  }

}