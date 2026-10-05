package uk.co.whitbread.integrationtests.stubs.opera

import java.time.LocalDate
import java.time.temporal.ChronoUnit

internal data class OperaDateInterval(
    val startDate: LocalDate,
    val endDate: LocalDate,
)

internal fun fixedInclusiveIntervals(
    startDate: LocalDate,
    endDate: LocalDate,
    maximumDays: Int,
): List<OperaDateInterval> {
    val totalDays = ChronoUnit.DAYS.between(startDate, endDate).toInt() + 1
    return generateSequence(0) { index -> index + 1 }
        .map { index -> startDate.plusDays(index.toLong() * maximumDays) }
        .takeWhile { intervalStart -> !intervalStart.isAfter(endDate) }
        .map { intervalStart ->
            OperaDateInterval(
                startDate = intervalStart,
                endDate = minOf(intervalStart.plusDays(maximumDays.toLong() - 1), endDate),
            )
        }.toList()
        .also { intervals ->
            require(
                intervals.sumOf { interval ->
                    ChronoUnit.DAYS.between(interval.startDate, interval.endDate).toInt() + 1
                } == totalDays,
            )
        }
}

/** Mirrors OHIP's Opera interval splitting, including its avoidance of one-day final intervals. */
internal fun operaLimitedIntervals(
    startDate: LocalDate,
    endDate: LocalDate,
    requestedDays: Int,
): List<OperaDateInterval> {
    val totalDays = ChronoUnit.DAYS.between(startDate, endDate) + 1
    var daysPerInterval = requestedDays.toLong()
    var intervalCount = totalDays / daysPerInterval
    val remainingDays = totalDays % daysPerInterval

    if (intervalCount == 0L || requestedDays == 1 || startDate == endDate) {
        return listOf(OperaDateInterval(startDate, endDate))
    }

    if (remainingDays == 1L) {
        if (intervalCount > 1) {
            intervalCount -= 1
        } else if (daysPerInterval > 2) {
            daysPerInterval -= 1
        }
    }

    val intervals = intervalList(startDate, totalDays, daysPerInterval, intervalCount).toMutableList()
    if (intervals.isEmpty() || remainingDays == 0L) {
        return intervals
    }

    val remainderStart = intervals.last().endDate.plusDays(1)
    val remainderLength = ChronoUnit.DAYS.between(remainderStart, endDate) + 1
    if (remainderLength <= daysPerInterval) {
        intervals += OperaDateInterval(remainderStart, endDate)
        return intervals
    }

    var remainderIntervalDays = daysPerInterval - 1
    if (remainderIntervalDays == 1L) {
        remainderIntervalDays += 1
    }
    val remainderIntervals =
        intervalList(
            startDate = remainderStart,
            totalDays = remainderLength,
            daysPerInterval = remainderIntervalDays,
            intervalCount = remainderLength / remainderIntervalDays,
        )
    intervals += remainderIntervals

    val finalRemainder = remainderLength % remainderIntervalDays
    if (finalRemainder > 0) {
        val finalStart = remainderIntervals.last().endDate.plusDays(1)
        val finalLength = ChronoUnit.DAYS.between(finalStart, endDate) + 1
        if (finalLength <= remainderIntervalDays) {
            intervals += OperaDateInterval(finalStart, endDate)
        }
    }
    return intervals
}

private fun intervalList(
    startDate: LocalDate,
    totalDays: Long,
    daysPerInterval: Long,
    intervalCount: Long,
): List<OperaDateInterval> =
    (0 until intervalCount).map { index ->
        val intervalStart = startDate.plusDays(index * daysPerInterval)
        OperaDateInterval(
            startDate = intervalStart,
            endDate = intervalStart.plusDays(minOf(daysPerInterval - 1, totalDays - index * daysPerInterval - 1)),
        )
    }
