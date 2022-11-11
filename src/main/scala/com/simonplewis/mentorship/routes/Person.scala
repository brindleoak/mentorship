package com.simonplewis.mentorship.routes

import cats.*
import cats.data.*
import cats.data.Validated.*
import cats.implicits.*
import com.simonplewis.mentorship.models.PersonDb

case class Person(
  id: Int,
  name: String,
  age: Int,
  email: String
)

object Person:
  def get(id: Int): Validated[String, Person] =
    PersonDb.find(id) match
      case p :: _ => Person(p.id, p.name, p.age, p.email).valid
      case List() => s"No such person: $id".invalid
    
  def hello(validatedPerson: Validated[String, Person]): String = validatedPerson match
    case Invalid(er) => s"Error. $er"
    case Valid(p)  => s"Hello ${p.name}, your email address is ${p.email}"

