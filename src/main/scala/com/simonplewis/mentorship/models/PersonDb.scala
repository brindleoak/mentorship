package com.simonplewis.mentorship.models

import java.time.{LocalDate, ZonedDateTime}
import scalikejdbc.*

case class PersonDb(
  id: Int,
  name: String,
  age: Int,
  email: String)

object PersonDb extends SQLSyntaxSupport[PersonDb]:
  override val tableName = "Person"

  def apply(rs: WrappedResultSet) = new PersonDb(
    rs.int("Id"),
    rs.string("Name"),
    rs.int("Age"),
    rs.string("Email")
  )

  def find(id: Int): List[PersonDb] =
    implicit val session = AutoSession
    sql"""
        |SELECT Id, Name, Age, Email
        |FROM Person
        |WHERE Id = $id""".stripMargin
    .map(rs => PersonDb(rs)).list.apply()    

end PersonDb