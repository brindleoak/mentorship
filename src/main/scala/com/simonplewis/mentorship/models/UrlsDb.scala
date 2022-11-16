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

  def findTargetUrl(url: String): Option[UrlsDb] =
    DB readOnly { implicit session =>
      sql"""
          |SELECT id, key, secret_key, target_url, is_active, clicks
          |FROM urls
          |WHERE target_url = $url""".stripMargin
      .map(rs => UrlsDb(rs)).single.apply()    
    }

  def findKey(key: String): Option[UrlsDb] =
    DB readOnly { implicit session =>
      sql"""
          |SELECT id, key, secret_key, target_url, is_active, clicks
          |FROM urls
          |WHERE key = $key""".stripMargin
      .map(rs => UrlsDb(rs)).single.apply()    
    }

  def updateClicks(key: String, clicks: Int) =
    DB localTx { implicit session =>
      sql"""
          |UPDATE urls
          |SET clicks = clicks + 1
          |WHERE key = $key""".stripMargin
      .update.apply()
    }

  def setActive(key: String, isActive: Boolean) = 
    DB localTx { implicit session =>
      sql"""
          |UPDATE urls
          |SET is_active = ${isActive.toString}
          |WHERE key = $key""".stripMargin
      .update.apply()
    }

  def newUrl(key: String, secretKey: String, targetUrl: String) =
    DB localTx { implicit session =>
      sql"""
          |INSERT INTO urls (key, secret_key, target_url, is_active, clicks)
          |VALUES ($key, $secretKey, $targetUrl, TRUE, 0)""".stripMargin
      .update.apply()
  }
end UrlsDb