package com.simonplewis.mentorship

import cats._
import org.http4s.HttpRoutes
import org.http4s.dsl.Http4sDsl

object MentorshipRoutes:

  def personRoutes[F[_] : Monad]: HttpRoutes[F] =
    val dsl = Http4sDsl[F]
    import dsl._
    HttpRoutes.of[F] {
      case GET -> Root / "person" / "get" /  id =>
        val person = Persons.get(id)
        val greeting = Persons.hello(person)
        Ok(greeting)
    }