package uk.co.whitbread.ohip.domain.model.reservation.in;

public enum PersonNameTypeType {

  PRIMARY("Primary"),

  ALTERNATE("Alternate"),

  INCOGNITO("Incognito"),

  EXTERNAL("External"),

  PHONETIC("Phonetic");

  private String value;

  PersonNameTypeType(String value) {
    this.value = value;
  }

  public String getValue() {
    return value;
  }

  @Override
  public String toString() {
    return String.valueOf(value);
  }

  public static PersonNameTypeType fromValue(String value) {
    for (PersonNameTypeType b : PersonNameTypeType.values()) {
      if (b.value.equals(value)) {
        return b;
      }
    }
    return ALTERNATE;
  }
}
