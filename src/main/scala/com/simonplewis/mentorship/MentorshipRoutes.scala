package com.simonplewis.mentorship

import cats.effect.*
import cats.implicits.*

import io.circe.generic.auto.*
import io.circe.syntax.*

import org.http4s.*
import org.http4s.dsl.Http4sDsl
import org.http4s.circe.*
import org.http4s.circe.CirceEntityDecoder.*

import com.simonplewis.mentorship.routes.*

object MentorshipRoutes:

  def urlRoutes[F[_] : Concurrent]: HttpRoutes[F] =
    val dsl = Http4sDsl[F]
    import dsl._
    implicit val decoder:EntityDecoder[IO, UrlRequest] = jsonOf[IO, UrlRequest]
    HttpRoutes.of[F] {
      case GET -> Root =>
        Ok("Hello, World!")

      case urlRequest @ POST -> Root / "url" =>
        for
          targetUrl <- urlRequest.as[UrlRequest]
          shortenedUrl = UrlResponse(targetUrl)
          response <- shortenedUrl match
            case Left(er) => BadRequest(er.description)
            case Right(resp) => Ok(resp.asJson)
        yield response  
            

      case GET -> Root / "redirect" =>
        Uri.fromString("https://www.bbc.co.uk") match
          case Left(er) => BadRequest(er.details)
          case Right(uri) => Ok(uri.toString)   
    }  