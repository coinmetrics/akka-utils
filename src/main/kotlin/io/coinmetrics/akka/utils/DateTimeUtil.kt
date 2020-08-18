/*
 * Copyright (c) 2020. Coin Metrics Inc.
 */

package io.coinmetrics.akka.utils

import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZonedDateTime

object DateTimeUtil {

    val NewYorkZoneId = ZoneId.of("America/New_York")
    val NewYorkCloseTime = LocalTime.of(16, 0)

}

fun LocalDate.toMillis(zoneId: ZoneId, closeTime: LocalTime): Long = ZonedDateTime.of(this, closeTime, zoneId).toEpochSecond() * 1000

fun Long.toLocalDate(zoneId: ZoneId): LocalDate = Instant.ofEpochMilli(this).atZone(zoneId).toLocalDate()
