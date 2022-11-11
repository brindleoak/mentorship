package com.simonplewis.mentorship

import cats._
import org.http4s.HttpRoutes
import org.http4s.dsl.Http4sDsl
import org.http4s.dsl.impl.QueryParamDecoderMatcher

object MentorshipRoutes:

  object PersonQueryParamMatcher extends QueryParamDecoderMatcher[String]("id")

  def personRoutes[F[_] : Monad]: HttpRoutes[F] =
    val dsl = Http4sDsl[F]
    import dsl._
    HttpRoutes.of[F] {
      case GET -> Root / "person" / "get" :? PersonQueryParamMatcher(personId) =>
        personId.toIntOption match
          case Some(id) => 
            val person = Persons.get(id)
            val greeting = Persons.hello(person)
            Ok(greeting)
          case None => BadRequest("Person id must be numeric")  
    }