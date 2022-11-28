package com.simonplewis.mentorship

import cats.effect.*
import org.http4s.blaze.server.BlazeServerBuilder
import org.http4s.HttpRoutes
import org.http4s.HttpApp

import com.simonplewis.mentorship.models.*
import com.simonplewis.mentorship.test.models.*
import com.simonplewis.mentorship.routes.UrlShortenService

object Dependencies:
  
  val shortUrlStore: ShortUrlStore = 
    //ShortUrlDbStore()
    ShortUrlMockStore()

  val urlShortner = UrlShortenService(shortUrlStore)
  
  val mentorshipApp = mentorshipRoutesComplete[IO]

  val bladeServerBuilder = BlazeServerBuilder[IO]
    .bindHttp(8080, "localhost")
    .withHttpApp(mentorshipApp)
    .resource
    .use(_ => IO.never)

  def mentorshipRoutes[F[_] : Concurrent]: HttpRoutes[F] = MentorshipRoutes(urlShortner).shortenUrlRoutes[F]
  def mentorshipRoutesComplete[F[_] : Concurrent]: HttpApp[F] = mentorshipRoutes.orNotFound  
