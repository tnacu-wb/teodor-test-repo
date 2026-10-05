package uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.model.in;

public record AlertsDto(
    String id,
    String area,
    String code,
    String description,
    boolean screenNotification,
    boolean printerNotification) {

}
