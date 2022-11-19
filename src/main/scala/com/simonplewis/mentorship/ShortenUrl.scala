package com.simonplewis.mentorship.routes
import com.simonplewis.mentorship.models.UrlsDb
import com.simonplewis.mentorship.routes.*

import cats.syntax.either.catsSyntaxEither
import org.http4s.Uri
import scala.util.Random

trait UrlShortener:
  def shortenUrl: UrlShortener

trait UrlFailure(val description: String) extends UrlShortener:
  override def shortenUrl: UrlShortener = this

case class UrlInvalid(er: String) extends UrlFailure(er)
case class UrlAlreadyShortened(er: String = "This URL has already been shortened") extends UrlFailure(er)
case class DbError(er:String) extends UrlFailure(er)

case class UrlShorten(
  val targetUrl: Uri,
  val isActive: Boolean,
  val clicks: Int,
  val shortenedUrl: Uri,
  val adminKey: String
)(using db: UrlsDb) extends UrlShortener:
    def targetUrlAlreadyShortened: UrlShortener =
      db.findTargetUrl(targetUrl.renderString) match
      case Some(_) => UrlAlreadyShortened()
      case _ => this

    override def shortenUrl: UrlShortener =
      val shortUrlKey = Random.shuffle('A' to 'Z').mkString.take(5)
      val adminKey = Random.shuffle('A' to 'Z').mkString.take(8)
      val shortUrl = Uri.unsafeFromString(shortUrlKey)

      db.newUrl(shortUrl.renderString, adminKey, targetUrl.renderString) match
        case () => UrlShorten(targetUrl, true, 0, shortUrl, adminKey)
        case e => DbError(s"SQL error $e")

    def shortenedUriAlreadyExists(urlResponse: ShortenUrlResponse): Boolean = false


object UrlShortener:
  def apply(targetUrl: Either[UrlFailure, Uri])(using db:UrlsDb): UrlShortener = targetUrl match
    case Left(e) => e
    case Right(u) => UrlShorten(u, true, 0, Uri.unsafeFromString(""), "")

  /*
    for 
      targetUri <-  Uri.fromString(urlRequest.url).leftMap(e => new UrlInvalid(e.sanitized)) 
      validatedUri <- targetUrlAlreadyShortened(targetUri)
      urlResponse <- shortenUri(validatedUri)
    yield urlResponse
  */


