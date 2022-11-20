package com.simonplewis.mentorship.routes

import com.simonplewis.mentorship.models.*
import scala.util.Random
      
object UrlShortener:
  def targetUrlAlreadyShortened(urlRecord: ValidUrl)(using db: UrlsDb): ValidUrl =
    urlRecord match
      case Left(e) => urlRecord
      case Right(u) => db.findTargetUrl(u.targetUrl) match
        case Some(_) => UrlAlreadyShortened().invalidUrl
        case None => urlRecord

  def shortenUrl(urlRecord: ValidUrl)(using db: UrlsDb): ValidUrl =
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

        db.newUrl(newUrlRecord)

  def lookupShortUrl(urlRecord: UrlRecord)(using db: UrlsDb): ValidUrl =
    db.findShortUrl(urlRecord.shortUrl) match
      case Some(u) => Right(UrlRecord(u.targetUrl, u.secretKey, u.secretKey, u.isActive, u.clicks))
      case _ => Left(ShortUrlNotFound()) 