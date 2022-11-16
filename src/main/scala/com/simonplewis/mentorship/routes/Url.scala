package com.simonplewis.mentorship.routes

import cats.syntax.either.catsSyntaxEither
import org.http4s.Uri

trait UrlFailure(val description: String) 
case class UrlInvalid(er: String) extends UrlFailure(er)

case class UrlRequest(url: String)

case class UrlResponse(
  target_url: String,
  is_active: Boolean,
  clicks: Int,
  url: String,
  admin_url: String)

object UrlResponse:
  def apply(target_url: String) =
    for 
      targetUri <- Uri.fromString(target_url).leftMap(e => new UrlInvalid(e.details)) 
      validUri <- validateUri(targetUri)
      shortened <- shortenUri(validUri)
    yield shortened

  // check if this url has already been shortened
  def validateUri(uri: Uri): Either[UrlFailure, Uri]
    = Right(uri)
    
  // shorten the url  
  def shortenUri(uri: Uri): Either[UrlFailure, UrlResponse]
    = Right(new UrlResponse(uri.toString, true, 0, "ABC", "DEF"))

