package com.simonplewis.mentorship

import cats.effect.{IO, IOApp}

object Main extends IOApp.Simple:
  def run: IO[Unit] =
    MentorshipServer.stream[IO].compile.drain
