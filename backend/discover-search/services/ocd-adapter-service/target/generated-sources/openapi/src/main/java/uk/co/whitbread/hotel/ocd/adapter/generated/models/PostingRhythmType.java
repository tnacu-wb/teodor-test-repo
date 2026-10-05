package uk.co.whitbread.hotel.ocd.adapter.generated.models;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonValue;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import javax.validation.Valid;
import javax.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import javax.annotation.Generated;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * The posting frequency of the package element or package group associateded to the rate plan.  EveryNight - Package charge posted every night of the stay  ArrivalNight - Package charge posted only on arrival night  EveryXNightsStartingNightY - Package charge posted every X nights, beginning the Y night of stay  CertainNightsOfTheWeek - Package charge posted on certain night of the stay determined by the property  LastNight - Package charge posted only on the last night of the stay  EveryNightExceptArrivalNight - Package charge posted on all nights of the stay except the arrival night  EveryNightExceptLast - Package charge posted on all nights of the stay except the last night  EveryNightExceptFirstAndLast - Package posted on all nights of the stay except the first and last nights of the stay  CustomStaySchedule - Package charge posted on stays determined by the property  CustomNightSchedule - Package charge posted on nights of the stay determined by the property  FloatingAllowancePerStay - Allows for the package allowance to be consumed at anytime during the stay  TicketPosting - Package charge posted immediately when a successful response is received back from the ticketing vendor (requires interface with a vendor) 
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-25T07:51:32.554166+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public enum PostingRhythmType {
  
  EVERY_NIGHT("EveryNight"),
  
  ARRIVAL_NIGHT("ArrivalNight"),
  
  EVERY_X_NIGHTS_STARTING_NIGHT_Y("EveryXNightsStartingNightY"),
  
  CERTAIN_NIGHTS_OF_THE_WEEK("CertainNightsOfTheWeek"),
  
  LAST_NIGHT("LastNight"),
  
  EVERY_NIGHT_EXCEPT_ARRIVAL_NIGHT("EveryNightExceptArrivalNight"),
  
  EVERY_NIGHT_EXCEPT_LAST("EveryNightExceptLast"),
  
  EVERY_NIGHT_EXCEPT_FIRST_AND_LAST("EveryNightExceptFirstAndLast"),
  
  CUSTOM_STAY_SCHEDULE("CustomStaySchedule"),
  
  CUSTOM_NIGHT_SCHEDULE("CustomNightSchedule"),
  
  FLOATING_ALLOWANCE_PER_STAY("FloatingAllowancePerStay"),
  
  TICKET_POSTING("TicketPosting");

  private String value;

  PostingRhythmType(String value) {
    this.value = value;
  }

  @JsonValue
  public String getValue() {
    return value;
  }

  @Override
  public String toString() {
    return String.valueOf(value);
  }

  @JsonCreator
  public static PostingRhythmType fromValue(String value) {
    for (PostingRhythmType b : PostingRhythmType.values()) {
      if (b.value.equals(value)) {
        return b;
      }
    }
    throw new IllegalArgumentException("Unexpected value '" + value + "'");
  }
}

