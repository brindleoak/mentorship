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

  object PersonQueryParamMatcher extends QueryParamDecoderMatcher[String]("id")

  def personRoutes[F[_] : Monad]: HttpRoutes[F] =
    val dsl = Http4sDsl[F]
    import dsl._
    HttpRoutes.of[F] {
      case GET -> Root / "person" / "get" :? PersonQueryParamMatcher(personId) =>
        personId.toIntOption match
          case Some(id) => 
            val person = Person.get(id)
            val greeting = Person.hello(person)
            Ok(greeting)
          case None => BadRequest("Person id must be numeric")
      case GET -> Root / "redirect" =>
        Found(Location(Uri.unsafeFromString("https://www.bbc.co.uk"))) 

      case PUT -> Root / "url" / newUrl =>
        Ok(newUrl + "so far...")      
    }

  def urlRoutes[F[_] : Concurrent]: HttpRoutes[F] =
    val dsl = Http4sDsl[F]
    import dsl._
    implicit val decoder:EntityDecoder[IO, UrlRequest] = jsonOf[IO, UrlRequest]
    HttpRoutes.of[F] {

      case GET -> Root / "redirect" =>
        Found(Location(Uri.unsafeFromString("https://www.bbc.co.uk"))) 

      case urlRequest @ POST -> Root / "url" =>
        for
          target_url <- urlRequest.as[UrlRequest]
          response = UrlResponse(target_url.url, true, 0, "ABCD", "EFGHIJ")
          resp <- Ok(response.asJson)
        yield resp    
    }  