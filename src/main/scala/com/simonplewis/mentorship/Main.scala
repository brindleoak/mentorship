package com.simonplewis.mentorship

import scalikejdbc._
import cats.effect.{IO, IOApp}
import java.sql.DriverManager

object Main extends IOApp.Simple:

// initialize JDBC driver & connection pool
  Class.forName("com.mysql.jdbc.Driver")
  ConnectionPool.singleton("jdbc:mysql://localhost:3308/master", "root", "crdv046p")

  def run: IO[Unit] =

    val connection = DriverManager.getConnection(
      "jdbc:mysql://127.0.0.1:3306/master",
      "root", // username when connecting
      "crdv046p") // password

    MentorshipServer.stream[IO].compile.drain
