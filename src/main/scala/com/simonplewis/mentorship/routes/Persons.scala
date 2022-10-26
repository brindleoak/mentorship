package com.simonplewis.mentorship

import cats.effect.Concurrent
import cats.implicits.*
import cats.data.*
import cats.data.Validated.*
import io.circe.{Encoder, Json}
import org.http4s.*
import org.http4s.implicits.*
import org.http4s.client.Client
import org.http4s.client.dsl.Http4sClientDsl
import org.http4s.circe.*
import org.http4s.Method.*
import cats.Applicative
import cats.MonadError
import cats.Monad
import com.simonplewis.mentorship.models.PersonDb

type ValidatedPerson[A] = Validated[Persons.PersonError, A]

trait Persons[F[_]]:
  def get(id: Int): ValidatedPerson[Persons.Person]
  def hello(validatedPerson: ValidatedPerson[Persons.Person]): F[Persons.Greeting]

object Persons:
  def apply[F[_]](implicit ev: Persons[F]): Persons[F] = ev

  final case class Person(id: Int, name: String, age: Int, email: String)

  final case class Greeting(greeting: String) extends AnyVal

  object Greeting:
    given Encoder[Greeting] = (a: Greeting) => Json.obj(
      ("message", Json.fromString(a.greeting)),
    )

    given [F[_]]: EntityEncoder[F, Greeting] =
      jsonEncoderOf[F, Greeting]  

  final case class PersonError(error: String)

  def impl[F[_] : Applicative]: Persons[F] = new Persons[F]:
    def get(id: Int): ValidatedPerson[Person] = PersonDb.find(id) match  // to be replaced with MySQL lookup
      case Some(p) => Person(id, p.name, p.age, p.email).valid
      case None => PersonError("No such person: " + id).invalid
      
    def hello(validatedPerson: ValidatedPerson[Person]): F[Greeting] = validatedPerson match
      case Invalid(er) => Greeting(er.error).pure[F]
      case Valid(p)  => Greeting("Hello " + p.name + ", your email address is " + p.email).pure[F]

