package com.simonplewis.mentorship

import scalikejdbc.*
import cats.effect.*
import org.http4s.blaze.server.BlazeServerBuilder
import scala.concurrent.ExecutionContext

object Main extends IOApp:

  ConnectionPool.singleton("jdbc:mysql://127.0.0.1:3306/PersonDB", "simon", "password")

  override def run(args: List[String]): IO[ExitCode] = 

    val mentorshipApp = MentorshipServer.allRoutesComplete[IO]

    BlazeServerBuilder[IO]
      .bindHttp(8080, "localhost")
      .withHttpApp(mentorshipApp)
      .resource
      .use(_ => IO.never)
      .as(ExitCode.Success)

