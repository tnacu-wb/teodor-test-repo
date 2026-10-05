package uk.co.whitbread.availabilitycacheservice.infrastructure.entity;

import java.io.Serializable;
import lombok.Data;

@Data
public class HotelLocationId implements Serializable {

  private static final long serialVersionUID = 1L;

  private String placeId;

  private String hotelCode;

}