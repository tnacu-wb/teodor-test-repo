package uk.co.whitbread.company.employee.utils;

import java.util.UUID;
import lombok.experimental.UtilityClass;

@UtilityClass
public class UUIDGenerator {

  public static  String getUUID(){
    return UUID.randomUUID().toString();
  }

}
