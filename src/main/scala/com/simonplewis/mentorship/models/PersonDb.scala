package com.simonplewis.mentorship.models

import com.simonplewis.mentorship.Main.dbConnection

import java.time.{LocalDate, ZonedDateTime}

case class PersonDb(
  id: Int,
  name: String,
  age: Int,
  email: String)

object PersonDb:

  def find(id: Int): Option[PersonDb] =
    val statement = dbConnection.createStatement
    val rs = statement.executeQuery(s"""
        |SELECT Name, Age, Email
        |FROM Person
        |WHERE Id = $id""".stripMargin)
    if rs.next() then Some(PersonDb(
      id,
      rs.getString("Name"),
      rs.getInt("Age"),
      rs.getString("Email")))
    else None

end PersonDb