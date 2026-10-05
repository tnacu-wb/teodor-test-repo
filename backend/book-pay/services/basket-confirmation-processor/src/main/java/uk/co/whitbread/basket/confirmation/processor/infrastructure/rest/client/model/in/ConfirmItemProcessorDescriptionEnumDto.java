package uk.co.whitbread.basket.confirmation.processor.infrastructure.rest.client.model.in;

public enum ConfirmItemProcessorDescriptionEnumDto {
  COMPLETED_TYPE("COMPLETED"), FAILED_TYPE("FAILED");
  private final String value;

  ConfirmItemProcessorDescriptionEnumDto(String value) {
    this.value = value;
  }

  public String getValue() {
    return value;
  }
}
