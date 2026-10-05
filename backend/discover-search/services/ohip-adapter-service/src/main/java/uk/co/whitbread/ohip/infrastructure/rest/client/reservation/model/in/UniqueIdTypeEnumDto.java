package uk.co.whitbread.ohip.infrastructure.rest.client.reservation.model.in;

public enum UniqueIdTypeEnumDto {
  PROFILE_TYPE("Profile"),
  RESERVATION_TYPE("Reservation"),
  CONFIRMATION_TYPE("Confirmation"),
  POLICY_SCHEDULE_TYPE("PolicyScheduleId"),
  CANCELLATION_TYPE("Cancellation");

  private final String value;

  UniqueIdTypeEnumDto(String s) {
    this.value = s;
  }

  public String value() {
    return value;
  }
}