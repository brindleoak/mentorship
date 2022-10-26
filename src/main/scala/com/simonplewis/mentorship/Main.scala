package com.simonplewis.mentorship

import cats.effect.{IO, IOApp}
import java.sql.{Connection, DriverManager}

object Main extends IOApp.Simple:

  val dbConnection: Connection =
    DriverManager.getConnection(
      "jdbc:mysql://127.0.0.1:3306/simon",
      "root", // username when connecting
      "9996") // password

  def run: IO[Unit] = MentorshipServer.stream[IO].compile.drain
