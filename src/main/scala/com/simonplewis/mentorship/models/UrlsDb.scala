package com.simonplewis.mentorship.models

import java.time.{LocalDate, ZonedDateTime}
import scalikejdbc.*

case class UrlsDb(
  shortUrl: String,
  secretKey: String,
  targetUrl: String,
  isActive: Boolean,
  clicks: Int)

object UrlsDb extends SQLSyntaxSupport[UrlsDb]:
  override val tableName = "urls"

  def apply(u: ResultName[UrlsDb])(rs: WrappedResultSet): UrlsDb = 
    new UrlsDb(
      rs.string(u.shortUrl),
      rs.string(u.secretKey),
      rs.string(u.targetUrl),
      rs.boolean(u.isActive),
      rs.int(u.clicks)
    )

  def apply(u: SyntaxProvider[UrlsDb])(rs: WrappedResultSet): UrlsDb =
    apply(u.resultName)(rs)  

  def findTargetUrl(url: String) =
    DB localTx { implicit session =>
      val u = UrlsDb.syntax("u")

      withSQL {
        select
        .from(UrlsDb as u)
        .where.eq(u.targetUrl, url)
      }.map(UrlsDb(u)).single.apply()   
    } 
    

  //def findKey(shortUrl: String): Option[UrlsDb] =
  //  DB readOnly { implicit session =>
  //    sql"""
  //        |SELECT short_url, secret_key, target_url, is_active, clicks
  //        |FROM urls
  //        |WHERE short_url = $shortUrl""".stripMargin
  //    .map(rs => UrlsDb(rs)).single.apply()    
  //  }

  def updateClicks(shortUrl: String, clicks: Int) =
    DB localTx { implicit session =>
      sql"""
          |UPDATE urls
          |SET clicks = clicks + 1
          |WHERE short_url = $shortUrl""".stripMargin
      .update.apply()
    }

  def setActive(shortUrl: String, isActive: Boolean) = 
    DB localTx { implicit session =>
      sql"""
          |UPDATE urls
          |SET is_active = ${isActive.toString}
          |WHERE short_url = $shortUrl""".stripMargin
      .update.apply()
    }

  def newUrl(shortUrl: String, secretKey: String, targetUrl: String) =
    try
      DB localTx { implicit session =>
        withSQL {
          insert.into(UrlsDb)
            .columns(column.shortUrl, column.secretKey, column.targetUrl, column.isActive, column.clicks)
            .values(shortUrl, secretKey, targetUrl, true, 0)
        }.update.apply()
      ()  
      }
    catch 
      case e: Exception => e.toString
end UrlsDb