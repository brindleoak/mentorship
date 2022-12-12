package com.simonplewis.mentorship.routes

import com.simonplewis.mentorship.models.*
import com.simonplewis.mentorship.models.UrlRecord.*
import scala.util.Random
import cats.effect.unsafe.implicits.global
      
class UrlShortenService(db: ShortUrlStore):
  def targetUrlAlreadyShortened(urlRecord: ValidUrl): ValidUrl =
    urlRecord match
      case Left(e) => urlRecord
      case Right(u) => db.findTargetUrl(u.targetUrl) match
        case Some(_) => UrlAlreadyShortened().invalidUrl
        case None => urlRecord

  def shortenUrl(urlRecord: ValidUrl): ValidUrl =
    urlRecord match
      case Left(e) => urlRecord
      case Right(u) =>
        val newUrlRecord: ValidUrl = 
          targetUrlAlreadyShortened(Right(
            new UrlRecord(
              Random.shuffle('A' to 'Z').mkString.take(5),
              Random.shuffle('A' to 'Z').mkString.take(8),
              u.targetUrl,
              true,
              0
            )
          ))

        db.newUrl(newUrlRecord).unsafeRunSync()

  def lookupShortUrl(urlRecord: UrlRecord): ValidUrl =
    db.findShortUrl(urlRecord.shortUrl) match
      case Some(u) => Right(UrlRecord(u.targetUrl, u.secretKey, u.targetUrl, u.isActive, u.clicks))
      case _ => Left(ShortUrlNotFound()) 