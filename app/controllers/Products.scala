package controllers

import models.Product
import play.api._
import play.api.data.Form
import play.api.data.Forms.{mapping, longNumber, nonEmptyText}
import play.api.mvc._

object Products extends Controller {

  def show(ean: Long) = Action { implicit request =>
    Product.findByEan(ean).map { product =>
      Ok(views.html.products.details(product))
    }.getOrElse(NotFound)
  }

  private val productForm: Form[Product] = Form(
    mapping(
      "ean" -> longNumber.verifying("A product with this EAN code already exists", Product.findByEan(_).isEmpty),
      "name" -> nonEmptyText,
      "description" -> nonEmptyText
    )(Product.apply)(Product.unapply)
  )

  def save = Action { implicit request =>
    val newProductForm = productForm.bindFromRequest()

    newProductForm.fold(

      hasErrors = { form =>
        Redirect(routes.Products.newProduct()).flashing(Flash(form.data) + ("error" -> "validation error"))
      },

      success = { newProduct =>
        Product.add(newProduct)
        Redirect(routes.Products.show(newProduct.ean)).flashing("success" -> "save success")
      }

    )
  }

  def newProduct = Action { implicit request =>
    val form = if(request.flash.get("error").isDefined)
      productForm.bind(request.flash.data)
    else
      productForm

    Ok(views.html.products.editProduct(form))
  }

}