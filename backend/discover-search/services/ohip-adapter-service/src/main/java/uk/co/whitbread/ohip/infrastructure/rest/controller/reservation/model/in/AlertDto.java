package uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.model.in;

public record AlertDto(
    String id,
    String area,
    String code,
    String description,
    boolean screenNotification,
    boolean printerNotification) {

}