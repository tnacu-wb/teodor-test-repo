package uk.co.whitbread.integrationtests.stubs.opera

import uk.co.whitbread.integrationtests.testkit.model.HotelRoomType
import java.time.LocalDate

/** Lowest available room count across an inclusive date interval. */
internal fun HotelRoomType.minimumAvailableRooms(
    startDate: LocalDate,
    endDateInclusive: LocalDate,
): Int {
    require(!endDateInclusive.isBefore(startDate)) {
        "endDateInclusive must not be before startDate"
    }

    return generateSequence(startDate) { date -> date.plusDays(1) }
        .takeWhile { date -> !date.isAfter(endDateInclusive) }
        .minOf { date ->
            inventoryPeriods
                .singleOrNull { period ->
                    !date.isBefore(period.startDate) && !date.isAfter(period.endDate)
                }?.numberOfRooms ?: numberOfRooms
        }
}
