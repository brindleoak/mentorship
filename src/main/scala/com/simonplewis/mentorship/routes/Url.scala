package com.simonplewis.mentorship.routes

import cats.*
import cats.data.*
import cats.syntax.either.catsSyntaxEither
import org.http4s.Uri
import org.http4s.ParseFailure

trait UrlFailure(er: String)
class UrlInvalid(er: String) extends UrlFailure(er)

case class UrlResponse(
  target_url: String,
  is_active: Boolean,
  clicks: Int,
  url: String,
  admin_url: String):
    def map(f: UrlResponse => UrlResponse) = ???

object UrlResponse:
  def apply(
    target_url: String,
    is_active: Boolean,
    clicks: Int,
    url: String,
    admin_url: String): Either[UrlFailure, UrlResponse] = for {
      targetUri <- Uri.fromString(target_url)  //.leftMap(e => new UrlInvalid(e.details)) 
      validUri <- validateUri(targetUri)
      urlResponse <- 
        new UrlResponse("",true,0,"","")
    } yield urlResponse

    def validateUri(uri: Either[UrlFailure, UrlResponse]): Either[UrlFailure, UrlResponse]
      = uri

object Url:
  def get(key: String): Validated[String, UrlResponse] = ???


