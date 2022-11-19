package com.simonplewis.mentorship.routes
import com.simonplewis.mentorship.models.UrlsDb
import com.simonplewis.mentorship.routes.*

import cats.syntax.either.catsSyntaxEither
import org.http4s.Uri
import scala.util.Random
import cats.Monad

trait UrlShortener:
  def shortenUrl: UrlShortener
  def lookupShortUrl: UrlShortener

trait UrlFailure(val description: String) extends UrlShortener:
  override def shortenUrl: UrlShortener = this
  override def lookupShortUrl: UrlShortener = this

case class UrlInvalid(er: String) extends UrlFailure(er)
case class UrlAlreadyShortened(er: String = "This URL has already been shortened") extends UrlFailure(er)
case class DbError(er:String) extends UrlFailure(er)
case class ShortUrlNotFound(er: String = "This short URL cannot be found") extends UrlFailure(er)

case class UrlShorten(
  val targetUrl: Uri,
  val isActive: Boolean,
  val clicks: Int,
  val shortenedUrl: String,
  val adminKey: String
)(using db: UrlsDb) extends UrlShortener:
    def targetUrlAlreadyShortened: UrlShortener =
      db.findTargetUrl(targetUrl.renderString) match
      case Some(_) => UrlAlreadyShortened()
      case _ => this

    override def shortenUrl: UrlShortener =
      val shortUrlKey = Random.shuffle('A' to 'Z').mkString.take(5)
      val adminKey = Random.shuffle('A' to 'Z').mkString.take(8)

      targetUrlAlreadyShortened match
        case e: UrlFailure => e
        case u: UrlShorten => 
          db.newUrl(shortUrlKey, adminKey, targetUrl.renderString) match
            case () => UrlShorten(targetUrl, true, 0, shortUrlKey, adminKey)
            case e => DbError(s"DB error $e")

    def lookupShortUrl: UrlShortener =
      db.findShortUrl(shortenedUrl) match
        case Some(u) => UrlShorten(Uri.unsafeFromString(u.targetUrl), u.isActive, u.clicks, u.shortUrl, u.secretKey)
        case _ => ShortUrlNotFound() 
              
object UrlShortener:
  def apply(targetUrl: Either[UrlFailure, Uri])(using db: UrlsDb): UrlShortener = targetUrl match
    case Left(e) => e
    case Right(u) => UrlShorten(u, true, 0, "", "")

  def apply(shortUrl: String)(using db: UrlsDb): UrlShortener = UrlShorten(Uri.unsafeFromString(""), true, 0, shortUrl, "")