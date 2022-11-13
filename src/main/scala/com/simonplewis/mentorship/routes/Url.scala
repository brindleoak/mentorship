package com.simonplewis.mentorship.routes

import cats.*
import cats.data.*

case class UrlRequest(url: String)

case class UrlResponse(
  target_url: String,
  is_active: Boolean,
  clicks: Int,
  url: String,
  admin_url: String)

object Url:
  def get(key: String): Validated[String, UrlResponse] = ???


