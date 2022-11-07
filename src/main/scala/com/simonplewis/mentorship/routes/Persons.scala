package com.simonplewis.mentorship

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

object Persons:
  def get(id: String): Validated[String, Person] = 
    val idInt = id.toInt
    PersonDb.find(idInt) match  
      case p :: _ => Person(p.id, p.name, p.age, p.email).valid
      case List() => s"No such person: $id".invalid
    
  def hello(validatedPerson: Validated[String, Person]): String = validatedPerson match
    case Invalid(er) => s"Error. $er"
    case Valid(p)  => s"Hello ${p.name}, your email address is ${p.email}"

