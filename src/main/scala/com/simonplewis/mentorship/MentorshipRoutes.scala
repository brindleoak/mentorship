package com.simonplewis.mentorship

import cats.*
import cats.effect.*
import cats.implicits.*

import io.circe.*
import io.circe.generic.auto.*
import io.circe.syntax.*

import org.http4s.*
import org.http4s.dsl.Http4sDsl
import org.http4s.dsl.impl.QueryParamDecoderMatcher
import org.http4s.headers.Location
import org.http4s.circe.*
import org.http4s.circe.CirceEntityDecoder.*

import java.net.http.HttpClient.Redirect

import scala.concurrent.ExecutionContext.Implicits.global

import com.simonplewis.mentorship.routes.*

object MentorshipRoutes:

  def urlRoutes[F[_] : Concurrent]: HttpRoutes[F] =
    val dsl = Http4sDsl[F]
    import dsl._
    implicit val decoder:EntityDecoder[IO, String] = jsonOf[IO, String]
    HttpRoutes.of[F] {
      case GET -> Root =>
        Ok("Hello, World!")

      case urlRequest @ POST -> Root / "url" =>
        for
          target_url <- urlRequest.as[String]
          target_uri = Uri.fromString(target_url) 
          response = UrlResponse(target_url, true, 0, "ABCD", "EFGHIJ")
          resp <- Ok(response.asJson)
        yield resp  

      case GET -> Root / "redirect" =>
        Uri.fromString("https://www.bbc.co.uk") match
          case Left(er) => BadRequest(er.details)
          case Right(uri) => Ok(uri.toString)   
    }  