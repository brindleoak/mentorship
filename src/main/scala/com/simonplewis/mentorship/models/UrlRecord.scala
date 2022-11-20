package com.simonplewis.mentorship.models


type ValidUrl = Either[UrlFailure, UrlRecord]

trait ValidatedUrl:
  def isValid(): Boolean

class UrlRecord(
  val shortUrl: String = "",
  val secretKey: String = "",
  val targetUrl: String = "",
  val isActive: Boolean = true,
  val clicks: Int = 0
) extends ValidatedUrl:
  override def isValid() = true
  def validUrl: ValidUrl = Right(this)

trait UrlFailure(val description: String) extends ValidatedUrl:
  override def isValid() = false
  def invalidUrl: ValidUrl = Left(this)

case class UrlInvalid(er: String) extends UrlFailure(er)
case class UrlAlreadyShortened(er: String = "This URL has already been shortened") extends UrlFailure(er)
case class DbError(er:String) extends UrlFailure(er)
case class ShortUrlNotFound(er: String = "This short URL cannot be found") extends UrlFailure(er)
