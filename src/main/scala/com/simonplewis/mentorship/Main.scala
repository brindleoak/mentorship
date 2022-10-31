package com.simonplewis.mentorship

import cats.effect.{IO, IOApp}
import java.sql.{Connection, DriverManager}
import scalikejdbc.*

object Main extends IOApp.Simple:

  ConnectionPool.singleton("jdbc:mysql://127.0.0.1:3306/master", "root", "9996")

  def run: IO[Unit] = MentorshipServer.stream[IO].compile.drain
