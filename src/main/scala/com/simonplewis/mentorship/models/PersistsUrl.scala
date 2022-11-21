package com.simonplewis.mentorship.models
  
trait PersistUrls:
  def findTargetUrl(url: String): Option[UrlRecord]
  def findShortUrl(url: String): Option[UrlRecord]
  def newUrl(urlRecord: ValidUrl): ValidUrl
