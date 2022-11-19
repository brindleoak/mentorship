package com.simonplewis.mentorship.routes

final case class ShortenUrlRequest(url: String)

final case class ShortenUrlResponse(
  targetUrl: String,
  isActive: Boolean,
  clicks: Int,
  shortenedUrl: String,
  adminKey: String)