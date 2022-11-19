package com.simonplewis.mentorship

import com.simonplewis.mentorship.routes.*
import com.simonplewis.mentorship.models.UrlsDb

import cats.effect.*
import cats.implicits.*

import io.circe.generic.auto.*
import io.circe.syntax.*

import org.http4s.*
import org.http4s.dsl.Http4sDsl
import org.http4s.circe.*
import org.http4s.circe.CirceEntityDecoder.*
import org.http4s.headers.Location


object MentorshipRoutes:

  def urlRoutes[F[_] : Concurrent](using db: UrlsDb): HttpRoutes[F] =
    val dsl = Http4sDsl[F]
    import dsl._
    implicit val decoder:EntityDecoder[IO, ShortenUrlRequest] = jsonOf[IO, ShortenUrlRequest]
    HttpRoutes.of[F] {
      case GET -> Root =>
        Ok("Hello, World!")

      case urlRequest @ POST -> Root / "url" =>
        for
          shortenUrlRequest <- urlRequest.as[ShortenUrlRequest]
          targetUri =  Uri.fromString(shortenUrlRequest.url).leftMap(e => new UrlInvalid(e.sanitized)) 
          urlShortener = UrlShortener(targetUri).shortenUrl
          response <- urlShortener match
            case e: UrlFailure => BadRequest(e.description)
            case resp: UrlShorten => 
              val shortenUrlResponse = ShortenUrlResponse(
                resp.targetUrl.renderString,
                resp.isActive,
                resp.clicks,
                resp.shortenedUrl,
                resp.adminKey
              ).asJson
              Ok(shortenUrlResponse)
            
        yield response          

      case GET -> Root / shortUrl  =>
        UrlShortener(shortUrl).lookupShortUrl match
          case e: UrlFailure => BadRequest(e.description)
          case resp: UrlShorten => Found(Location(resp.targetUrl))   
    }  