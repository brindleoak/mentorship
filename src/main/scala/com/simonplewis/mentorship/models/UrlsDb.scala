package com.simonplewis.mentorship.models

import scalikejdbc.*
import com.simonplewis.mentorship.routes.*

class UrlsDb(
  val shortUrl: String = "",
  val secretKey: String = "",
  val targetUrl: String = "",
  val isActive: Boolean = true,
  val clicks: Int = 0
) extends SQLSyntaxSupport[UrlsDb] with PersistUrls:

  override val tableName = "urls"

  override def findTargetUrl(url: String): Option[UrlRecord] =
    DB readOnly { implicit session =>
      sql"""
          |SELECT short_url, secret_key, target_url, is_active, clicks
          |FROM urls
          |WHERE target_url = $url""".stripMargin
      .map(rs => UrlsDb(rs)).single.apply()   
      .map(r => UrlRecord(r.shortUrl, r.secretKey, r.targetUrl, r.isActive, r.clicks))
    } 

  override def findShortUrl(url: String): Option[UrlRecord] =
    DB readOnly { implicit session => 
      sql"""
          |SELECT short_url, secret_key, target_url, is_active, clicks
          |FROM urls
          |WHERE short_url = $url""".stripMargin
      .map(rs => UrlsDb(rs)).single.apply()
      .map(r => UrlRecord(r.shortUrl, r.secretKey, r.targetUrl, r.isActive, r.clicks))
  }   

  override def newUrl(urlRecord: ValidUrl): ValidUrl =
    urlRecord match
      case Left(_) => urlRecord
      case Right(u) =>   
        try
          DB localTx { implicit session =>
            sql"""
                  |INSERT INTO urls
                  |  (short_url, secret_key, target_url, is_active, clicks)
                  |VALUES (${u.shortUrl}, ${u.secretKey}, ${u.targetUrl}, true, 0)""".stripMargin
            .update.apply()
            Right(u)  
          }
        catch 
          case e: Exception => Left(DbError(e.toString))

object UrlsDb extends SQLSyntaxSupport[UrlsDb]:

  ConnectionPool.singleton("jdbc:mysql://127.0.0.1:3306/PersonDB", "simon", "password")

  def apply(): UrlsDb = new UrlsDb

  def apply(rs: WrappedResultSet) = new UrlsDb(
    rs.string("short_url"),
    rs.string("secret_key"),
    rs.string("target_url"),
    rs.boolean("is_active"),
    rs.int("clicks")
  )