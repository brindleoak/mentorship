package com.simonplewis.mentorship.test.models

import com.simonplewis.mentorship.routes.*
import com.simonplewis.mentorship.models.*
import com.simonplewis.mentorship.models.UrlRecord.*

class UrlsMockDb(
  val shortUrl: String = "",
  val secretKey: String = "",
  val targetUrl: String = "",
  val isActive: Boolean = true,
  val clicks: Int = 0
) extends PersistUrls:

  override def findTargetUrl(url: String): Option[UrlRecord] =
    url match
      case "http://www.google.com" => Some(UrlRecord("ABC12", "ABC12KEY", "http://www.google.com", true, 0))
      case _ => None

  override def findShortUrl(url: String): Option[UrlRecord] =
    url match
      case "ABC12" => Some(UrlRecord("ABC12", "ABC12KEY", "http://www.google.com", true, 0))
      case _ => None

  override def newUrl(urlRecord: ValidUrl): ValidUrl = urlRecord

object UrlsMockDb:

  def apply(): UrlsMockDb = new UrlsMockDb