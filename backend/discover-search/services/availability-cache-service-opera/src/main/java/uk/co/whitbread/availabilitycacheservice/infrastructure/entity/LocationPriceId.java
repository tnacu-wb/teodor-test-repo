package uk.co.whitbread.availabilitycacheservice.infrastructure.entity;

import java.io.Serializable;
import java.time.LocalDate;
import lombok.Data;

@Data
public class LocationPriceId implements Serializable {

  private static final long serialVersionUID = 1L;

  private String placeId;

  private LocalDate date;

}