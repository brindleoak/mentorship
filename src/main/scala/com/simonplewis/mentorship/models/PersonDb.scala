package com.simonplewis.mentorship.models

import scalikejdbc._
import java.time.{LocalDate, ZonedDateTime}

case class PersonDb(
  id: Int,
  name: String,
  age: Option[Int] = None,
  email: Option[String] = None):
  
    def save()(implicit session: DBSession = PersonDb.autoSession): PersonDb = PersonDb.save(this)(session)
    def destroy()(implicit session: DBSession = PersonDb.autoSession): Int = PersonDb.destroy(this)(session)


object PersonDb extends SQLSyntaxSupport[PersonDb]:

  override val tableName = "Person"
  override val columns = Seq("Id", "Name", "Age", "Email")

  def apply(m: SyntaxProvider[PersonDb])(rs: WrappedResultSet): PersonDb = apply(m.resultName)(rs)
  def apply(m: ResultName[PersonDb])(rs: WrappedResultSet): PersonDb = new PersonDb(
    id = rs.get(m.id),
    name = rs.get(m.name),
    age = rs.get(m.age),
    email = rs.get(m.email)
  )

  val p = PersonDb.syntax("p")

  override val autoSession = AutoSession

  def find(id: Int)(implicit session: DBSession = autoSession): Option[PersonDb] = 
    withSQL {
      val res = select.from(PersonDb as p).where.eq(p.id, id)
      res
    }.map(PersonDb(p.resultName)).single.apply()

  def create(person: PersonDb)(implicit session: DBSession = autoSession): PersonDb = 
    val generatedKey = withSQL {
      insert.into(PersonDb).namedValues(
        column.name -> person.name,
        column.age -> person.age,
        column.email -> person.email
      )
    }.updateAndReturnGeneratedKey.apply()

    person.copy(id = generatedKey.toInt)

  def save(entity: PersonDb)(implicit session: DBSession = autoSession): PersonDb = 
    withSQL {
      update(PersonDb).set(
        column.id -> entity.id,
        column.name -> entity.name,
        column.age -> entity.age,
        column.email -> entity.email
      ).where.eq(column.id, entity.id)
    }.update.apply()
    entity
  

  def destroy(entity: PersonDb)(implicit session: DBSession = autoSession): Int = 
    withSQL { 
      delete.from(PersonDb).where.eq(column.id, entity.id) 
    }.update.apply()
    
end PersonDb