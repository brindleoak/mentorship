package com.simonplewis.mentorship.models

import scalikejdbc.*
import com.simonplewis.mentorship.routes.*

case class UrlsDb(
  shortUrl: String = "",
  secretKey: String = "",
  targetUrl: String = "",
  isActive: Boolean = true,
  clicks: Int = 0
) extends SQLSyntaxSupport[UrlsDb]:

  override val tableName = "urls"

  def findTargetUrl(url: String): Option[UrlRecord] =
    DB readOnly { implicit session =>
      val u = UrlsDb.syntax("u")

      withSQL {
        select
        .from(UrlsDb as u)
        .where.eq(u.targetUrl, url)
      }
      .map(UrlsDb(u)).single.apply()   
      .map(res => UrlRecord(
        res.shortUrl,
        res.secretKey,
        res.targetUrl,
        res.isActive,
        res.clicks)
      )
    } 

  def findShortUrl(url: String): Option[UrlRecord] =
    DB localTx { implicit session =>
      val u = UrlsDb.syntax("u")

      withSQL {
        select
        .from(UrlsDb as u)
        .where.eq(u.shortUrl, url)
      }
      .map(UrlsDb(u)).single.apply()
      .map(res => UrlRecord(
        res.shortUrl,
        res.secretKey,
        res.targetUrl,
        res.isActive,
        res.clicks)
      )
  }   

  def newUrl(urlRecord: ValidUrl): ValidUrl =
    urlRecord match
      case Left(_) => urlRecord
      case Right(u) =>   
        try
          DB localTx { implicit session =>
            withSQL {
              insert.into(UrlsDb)
                .columns(column.shortUrl, column.secretKey, column.targetUrl, column.isActive, column.clicks)
                .values(u.shortUrl, u.secretKey, u.targetUrl, true, 0)
            }.update.apply()
            Right(u)  
          }
        catch 
          case e: Exception => Left(DbError(e.toString))

object UrlsDb extends SQLSyntaxSupport[UrlsDb]:

  def apply(): UrlsDb = new UrlsDb

  def apply(u: SyntaxProvider[UrlsDb])(rs: WrappedResultSet): UrlsDb =
    new UrlsDb(
      rs.string(u.shortUrl),
      rs.string(u.secretKey),
      rs.string(u.targetUrl),
      rs.boolean(u.isActive),
      rs.int(u.clicks)
    )
