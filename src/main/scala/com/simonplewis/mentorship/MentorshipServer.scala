package com.simonplewis.mentorship

import cats.effect.*
import org.http4s.HttpRoutes
import org.http4s.HttpApp

object MentorshipServer:

  def allRoutes[F[_] : Concurrent]: HttpRoutes[F] = MentorshipRoutes.urlRoutes[F]
  def allRoutesComplete[F[_] : Concurrent]: HttpApp[F] = allRoutes.orNotFound

