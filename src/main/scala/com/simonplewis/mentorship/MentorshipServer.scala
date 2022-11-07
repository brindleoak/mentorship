package com.simonplewis.mentorship

import cats.effect._
import org.http4s.HttpRoutes
import org.http4s.HttpApp

object MentorshipServer:

  def allRoutes[F[_] : Concurrent]: HttpRoutes[F] = MentorshipRoutes.personRoutes[F]
  def allRoutesComplete[F[_] : Concurrent]: HttpApp[F] = allRoutes.orNotFound

