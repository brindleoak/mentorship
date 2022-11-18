package com.simonplewis.mentorship.routes

import cats.syntax.either.catsSyntaxEither
import org.http4s.Uri
import scala.util.Random
import com.simonplewis.mentorship.models.UrlsDb

trait UrlFailure(val description: String) 
case class UrlInvalid(er: String) extends UrlFailure(er)
case class UrlAlreadyShortened(er: String = "This URL has already been shortened") extends UrlFailure(er)
case class DbError(er:String) extends UrlFailure(er)


case class UrlRequest(url: String)

case class UrlResponse(
  target_url: String,
  is_active: Boolean,
  clicks: Int,
  url: String,
  admin_url: String)

object UrlResponse:
  def apply(urlRequest: UrlRequest) =
    for 
      targetUri <-  Uri.fromString(urlRequest.url).leftMap(e => new UrlInvalid(e.sanitized)) 
      validatedUri <- validateUri(targetUri)
      urlResponse <- shortenUri(validatedUri)
    yield urlResponse

  def validateUri(uri: Uri): Either[UrlFailure, Uri] =
    UrlsDb.findTargetUrl(uri.renderString) match
      case Some(_) => Left(UrlAlreadyShortened())
      case _ => Right(uri)
        
  def shortenUri(uri: Uri): Either[UrlFailure, UrlResponse] =
    val url = Random.shuffle('A' to 'Z').mkString.take(5)
    val adminUrl = Random.shuffle('A' to 'Z').mkString.take(8)

    UrlsDb.newUrl(url, adminUrl, uri.renderString) match
      case () => Right(new UrlResponse(uri.toString, true, 0, url, adminUrl))
      case e => Left(DbError(s"SQL error $e"))
    

    

  // check the shortened url does not already exist  
  def shortenUriAlreadyExists(urlResponse: UrlResponse): Boolean
    = false
