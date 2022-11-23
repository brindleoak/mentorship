package com.simonplewis.mentorship

import scalikejdbc.*
import cats.effect.*
import org.http4s.blaze.server.BlazeServerBuilder

//val persistUrl = com.simonplewis.mentorship.models.UrlsDb()
val persistUrl = com.simonplewis.mentorship.test.models.UrlsMockDb()

object Main extends IOApp:

  override def run(args: List[String]): IO[ExitCode] = 

    val mentorshipApp = MentorshipServer.allRoutesComplete[IO]

    BlazeServerBuilder[IO]
      .bindHttp(8080, "localhost")
      .withHttpApp(mentorshipApp)
      .resource
      .use(_ => IO.never)
      .as(ExitCode.Success)

