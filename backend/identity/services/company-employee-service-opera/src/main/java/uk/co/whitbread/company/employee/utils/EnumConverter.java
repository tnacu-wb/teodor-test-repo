package uk.co.whitbread.company.employee.utils;

import java.lang.reflect.Field;
import lombok.experimental.UtilityClass;

@UtilityClass
public class EnumConverter {

  public static <E extends Enum<E>> E getEnum(Class<E> enumClass, String enumName) {
    if (enumName == null) {
      return null;
    }
    final Field[] declaredFields = enumClass.getDeclaredFields();
    for (Field field : declaredFields) {
      final String fieldName = field.getName();
      if (enumName.trim().equalsIgnoreCase(fieldName)) {
        return Enum.valueOf(enumClass, fieldName);
      }
    }
    return null;
  }
}
