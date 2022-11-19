package com.simonplewis.mentorship

import cats.effect.*
import org.http4s.HttpRoutes
import org.http4s.HttpApp
import com.simonplewis.mentorship.models.UrlsDb


object MentorshipServer:

  given db: UrlsDb = UrlsDb()
  
  def allRoutes[F[_] : Concurrent]: HttpRoutes[F] = MentorshipRoutes.urlRoutes[F]
  def allRoutesComplete[F[_] : Concurrent]: HttpApp[F] = allRoutes.orNotFound

