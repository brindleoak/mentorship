package com.simonplewis.mentorship.models

import scalikejdbc.*
import com.simonplewis.mentorship.routes.*

trait ShortUrlStore:
  def findTargetUrl(url: String): Option[UrlRecord]
  def findShortUrl(url: String): Option[UrlRecord]
  def newUrl(urlRecord: ValidUrl): ValidUrl
  
class ShortUrlDbStore(
  val shortUrl: String = "",
  val secretKey: String = "",
  val targetUrl: String = "",
  val isActive: Boolean = true,
  val clicks: Int = 0
) extends SQLSyntaxSupport[ShortUrlDbStore] with ShortUrlStore:

  override val tableName = "urls"

  override def findTargetUrl(url: String): Option[UrlRecord] =
    DB readOnly { implicit session =>
      sql"""
          |SELECT short_url, secret_key, target_url, is_active, clicks
          |FROM urls
          |WHERE target_url = $url""".stripMargin
      .map(rs => ShortUrlDbStore(rs)).single.apply()   
      .map(r => UrlRecord(r.shortUrl, r.secretKey, r.targetUrl, r.isActive, r.clicks))
    } 

  override def findShortUrl(url: String): Option[UrlRecord] =
    DB readOnly { implicit session => 
      sql"""
          |SELECT short_url, secret_key, target_url, is_active, clicks
          |FROM urls
          |WHERE short_url = $url""".stripMargin
      .map(rs => ShortUrlDbStore(rs)).single.apply()
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

object ShortUrlDbStore extends SQLSyntaxSupport[ShortUrlDbStore]:

  ConnectionPool.singleton("jdbc:mysql://127.0.0.1:3306/PersonDB", "simon", "password")

  def apply(): ShortUrlDbStore = new ShortUrlDbStore

  def apply(rs: WrappedResultSet) = new ShortUrlDbStore(
    rs.string("short_url"),
    rs.string("secret_key"),
    rs.string("target_url"),
    rs.boolean("is_active"),
    rs.int("clicks")
  )