package uk.co.whitbread.content.infrastructure.rest.client.content.promotions.utils;

import java.time.LocalDate;

public record ParsedDates(
    LocalDate bookingDate,
    LocalDate stayStartDate,
    LocalDate stayEndDate
) {}
