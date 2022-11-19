package com.simonplewis.mentorship.models

import java.time.{LocalDate, ZonedDateTime}
import scalikejdbc.*

case class UrlsDb(
  shortUrl: String,
  secretKey: String,
  targetUrl: String,
  isActive: Boolean,
  clicks: Int
) extends SQLSyntaxSupport[UrlsDb]:

  override val tableName = "urls"

  def findTargetUrl(url: String) =
    DB localTx { implicit session =>
      val u = UrlsDb.syntax("u")

      withSQL {
        select
        .from(UrlsDb as u)
        .where.eq(u.targetUrl, url)
      }.map(UrlsDb(u)).single.apply()   
    } 

  def findShortUrl(url: String) =
  DB localTx { implicit session =>
    val u = UrlsDb.syntax("u")

    withSQL {
      select
      .from(UrlsDb as u)
      .where.eq(u.shortUrl, url)
    }.map(UrlsDb(u)).single.apply()   
  }   

  // def updateClicks(shortUrl: String, clicks: Int) =
  //   DB localTx { implicit session =>
  //     sql"""
  //         |UPDATE urls
  //         |SET clicks = clicks + 1
  //         |WHERE short_url = $shortUrl""".stripMargin
  //     .update.apply()
  //   }

  // def setActive(shortUrl: String, isActive: Boolean) = 
  //   DB localTx { implicit session =>
  //     sql"""
  //         |UPDATE urls
  //         |SET is_active = ${isActive.toString}
  //         |WHERE short_url = $shortUrl""".stripMargin
  //     .update.apply()
  //   }

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

object UrlsDb extends SQLSyntaxSupport[UrlsDb]:
  override val tableName = "urls"

  def apply(): UrlsDb = 
    new UrlsDb("", "", "", true, 0)

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
