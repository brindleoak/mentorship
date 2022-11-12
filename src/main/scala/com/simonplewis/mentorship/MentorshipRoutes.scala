package com.simonplewis.mentorship

import cats._
import org.http4s.HttpRoutes
import org.http4s.dsl.Http4sDsl
import org.http4s.dsl.impl.QueryParamDecoderMatcher
import com.simonplewis.mentorship.routes.Person

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
    }