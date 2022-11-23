package com.simonplewis.mentorship

import cats.effect.*
import org.http4s.HttpRoutes
import org.http4s.HttpApp
import com.simonplewis.mentorship.models.PersistUrls



object MentorshipServer:

  given db: PersistUrls = persistUrl  

  def allRoutes[F[_] : Concurrent]: HttpRoutes[F] = MentorshipRoutes.urlRoutes[F]
  def allRoutesComplete[F[_] : Concurrent]: HttpApp[F] = allRoutes.orNotFound

