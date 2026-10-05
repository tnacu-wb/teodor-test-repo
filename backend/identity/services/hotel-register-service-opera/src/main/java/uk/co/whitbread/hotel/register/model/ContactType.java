package uk.co.whitbread.hotel.register.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ContactType {
  email("Email"),
  phone("Telephone");
  private final String type;
}