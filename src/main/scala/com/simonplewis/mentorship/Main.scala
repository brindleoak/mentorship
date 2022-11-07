package com.simonplewis.mentorship

import scalikejdbc.*
import cats.effect.*
import org.http4s.server.blaze.BlazeServerBuilder
import scala.concurrent.ExecutionContext.global

object Main extends IOApp:

  ConnectionPool.singleton("jdbc:mysql://127.0.0.1:3306/master", "root", "9996")

  override def run(args: List[String]): IO[ExitCode] = 

    val mentorshipApp = MentorshipServer.allRoutesComplete[IO]

    BlazeServerBuilder[IO](global)
      .bindHttp(8080, "localhost")
      .withHttpApp(mentorshipApp)
      .resource
      .use(_ => IO.never)
      .as(ExitCode.Success)

