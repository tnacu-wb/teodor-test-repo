package uk.co.whitbread.hotel.account.utils;

import java.lang.reflect.Field;

public final class EnumConverter {

  public static <E extends Enum<E>> E getEnum(Class<E> enumClass, String enumName) {
     if (enumName == null) {
      return null;
    }
    String cleanEnumName = enumName.trim();
    final Field[] declaredFields = enumClass.getDeclaredFields();
    for (Field field : declaredFields) {
      final String fieldName = field.getName();
      if (cleanEnumName.equalsIgnoreCase(fieldName)) {
        return Enum.valueOf(enumClass, fieldName);
      }
    }
    return null;
  }

  private EnumConverter() {}
}
