package com.simonplewis.mentorship

import com.simonplewis.mentorship.routes.{UrlShortenService, ShortenUrlRequest, ShortenUrlResponse}
import com.simonplewis.mentorship.models.{UrlInvalid, UrlRecord}

import cats.effect.*
import cats.implicits.*

import io.circe.generic.auto.*
import io.circe.syntax.*

import org.http4s.*
import org.http4s.dsl.Http4sDsl
import org.http4s.circe.*
import org.http4s.circe.CirceEntityDecoder.*
import org.http4s.headers.Location

//import com.simonplewis.mentorship.models.UrlRecord


class MentorshipRoutes(urlShortner: UrlShortenService):

  def shortenUrlRoutes[F[_] : Concurrent]: HttpRoutes[F] =
    val dsl = Http4sDsl[F]
    import dsl._

    HttpRoutes.of[F] {
      case GET -> Root =>
        Ok("Hello, World!")

      case urlRequest @ POST -> Root / "url" =>
        for
          shortenUrlRequest <- urlRequest.as[ShortenUrlRequest]

          urlRecord = Uri.fromString(shortenUrlRequest.url) match
            case Left(e) => Left(UrlInvalid(e.sanitized))
            case Right(u) => Right(UrlRecord(targetUrl = u.renderString))

          shortenUrlResult = urlShortner.shortenUrl(urlRecord) 
           
          result <- shortenUrlResult match
            case Left(e) => BadRequest(e.description)
            case Right(u) => 
              val shortenUrlResponse = ShortenUrlResponse(
                u.targetUrl,
                u.isActive,
                u.clicks,
                u.shortUrl,
                u.secretKey
              ).asJson
              Ok(shortenUrlResponse)
        yield result
      

      case GET -> Root / shortUrl  =>
        val urlRecord = UrlRecord(shortUrl = shortUrl)
        urlShortner.lookupShortUrl(urlRecord) match
          case Left(e) => BadRequest(e.description)
          case Right(u) => Found(Location(Uri.unsafeFromString(u.targetUrl)))   
    }  

object MentorshipRoutes:
  implicit val decoder:EntityDecoder[IO, ShortenUrlRequest] = jsonOf[IO, ShortenUrlRequest]