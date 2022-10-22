package com.simonplewis.mentorship

import cats.effect.Sync
import cats.implicits._
import cats.data._
import cats.data.Validated._
import cats.syntax.apply._
import org.http4s.HttpRoutes
import org.http4s.dsl.Http4sDsl

object MentorshipRoutes:

  def jokeRoutes[F[_]: Sync](j: Jokes[F]): HttpRoutes[F] =
    val dsl = new Http4sDsl[F]{}
    import dsl._
    HttpRoutes.of[F] {
      case GET -> Root / "joke" =>
        for {
          joke <- j.get
          resp <- Ok(joke)
        } yield resp
    }

  def helloWorldRoutes[F[_]: Sync](h: HelloWorld[F]): HttpRoutes[F] =
    val dsl = new Http4sDsl[F]{}
    import dsl._
    HttpRoutes.of[F] {
      case GET -> Root / "hello" / name =>
        for {
          greeting <- h.hello(HelloWorld.Name(name))
          resp <- Ok(greeting)
        } yield resp
    }
  

  def personRoutes[F[_] : Sync](p: Persons[F]): HttpRoutes[F] =
    val dsl = new Http4sDsl[F] {}
    import dsl._
    HttpRoutes.of[F] {
      case GET -> Root / "person" / "get" /  id =>
        val person = p.get(id)
        val greeting = p.hello(person)
        Ok(greeting)

        //Ok.apply(p.hello(p.get(id)))
        //for {
        //  person <- p.get(id)
        //  greeting <- p.hello(person)
        //  resp <- Ok(greeting)
        //} yield resp
    }