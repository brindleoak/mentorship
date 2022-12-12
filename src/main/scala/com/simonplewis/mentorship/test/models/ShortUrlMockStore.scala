package com.simonplewis.mentorship.test.models

import com.simonplewis.mentorship.routes.*
import com.simonplewis.mentorship.models.*
import com.simonplewis.mentorship.models.UrlRecord.*
import cats.effect.IO

case class ShortUrlMockStoreRecord(
  val shortUrl: String = "",
  val secretKey: String = "",
  val targetUrl: String = "",
  val isActive: Boolean = true,
  val clicks: Int = 0
)

class ShortUrlMockStore(var records: scala.collection.mutable.ListBuffer[ShortUrlMockStoreRecord]) extends ShortUrlStore:

  override def findTargetUrl(url: String): Option[UrlRecord] =
    records.find(_.targetUrl == url) match
      case Some(record) => Some(UrlRecord(record.shortUrl, record.secretKey, record.targetUrl, record.isActive, record.clicks))
      case None => None

  override def findShortUrl(url: String): Option[UrlRecord] =
    records.find(_.shortUrl == url) match
      case Some(record) => Some(UrlRecord(record.shortUrl, record.secretKey, record.targetUrl, record.isActive, record.clicks))
      case None => None

  override def newUrl(urlRecord: ValidUrl): IO[ValidUrl] = urlRecord match
    case Left(_) => IO.pure(urlRecord)
    case Right(r) =>
      records.append(ShortUrlMockStoreRecord(r.shortUrl, r.secretKey, r.targetUrl, r.isActive, r.clicks))
      IO.pure(Right(r))  

object ShortUrlMockStore:
  def apply(): ShortUrlMockStore = new ShortUrlMockStore(scala.collection.mutable.ListBuffer[ShortUrlMockStoreRecord]())