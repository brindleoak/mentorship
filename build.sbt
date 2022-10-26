val Http4sVersion = "1.0.0-M29"
val MunitVersion = "0.7.29"
val MunitCatsEffectVersion = "1.0.7"
val ScalikejdbcVersion = "4.0.0"
val H2databaseVersion = "1.4.200"
val logBackVersion = "1.4.4"

lazy val root = (project in file("."))
  .settings(
    organization := "com.simonplewis",
    name := "mentorship",
    version := "0.0.1-SNAPSHOT",
    scalaVersion := "3.1.0",
    libraryDependencies ++= Seq(
      "org.http4s"      %% "http4s-ember-server" % Http4sVersion,
      "org.http4s"      %% "http4s-ember-client" % Http4sVersion,
      "org.http4s"      %% "http4s-circe"        % Http4sVersion,
      "org.http4s"      %% "http4s-dsl"          % Http4sVersion,
      "org.scalameta"   %% "munit"               % MunitVersion           % Test,
      "org.typelevel"   %% "munit-cats-effect-3" % MunitCatsEffectVersion % Test,
      "mysql"           %  "mysql-connector-java" % "8.0.30",
      "ch.qos.logback"  %  "logback-classic"     % logBackVersion
    ),
    testFrameworks += new TestFramework("munit.Framework")
  )
