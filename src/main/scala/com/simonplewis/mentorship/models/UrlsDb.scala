package com.simonplewis.mentorship.models

import java.time.{LocalDate, ZonedDateTime}
import scalikejdbc.*

case class UrlsDb(
  id: Int,
  key: String,
  secretKey: String,
  targetUrl: String,
  isActive: Boolean,
  clicks: Int)

object UrlsDb extends SQLSyntaxSupport[UrlsDb]:
  override val tableName = "urls"

  def apply(rs: WrappedResultSet) = new UrlsDb(
    rs.int("id"),
    rs.string("key"),
    rs.string("secret_key"),
    rs.string("target_url"),
    rs.boolean("is_active"),
    rs.int("clicks")
  )

  def find_key(key: String): List[UrlsDb] =
    implicit val session = AutoSession
    sql"""
        |SELECT id, key, secret_key, target_url, is_active, clicks
        |FROM urls
        |WHERE key = $key""".stripMargin
    .map(rs => UrlsDb(rs)).list.apply()    

end UrlsDb