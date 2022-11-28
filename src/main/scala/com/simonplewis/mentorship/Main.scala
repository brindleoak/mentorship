package com.simonplewis.mentorship

import cats.effect.*

object Main extends IOApp:

  override def run(args: List[String]): IO[ExitCode] = 

  Dependencies.bladeServerBuilder
      .as(ExitCode.Success)

