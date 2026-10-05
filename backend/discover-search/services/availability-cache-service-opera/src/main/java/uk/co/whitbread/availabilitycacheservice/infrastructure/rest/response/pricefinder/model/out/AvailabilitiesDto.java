package uk.co.whitbread.availabilitycacheservice.infrastructure.rest.response.pricefinder.model.out;

import com.fasterxml.jackson.annotation.JsonView;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AvailabilitiesDto implements Comparable<AvailabilitiesDto> {

  @JsonView({HotelNameView.class, HotelCodeView.class})
  private String availableDate;
  @JsonView({HotelNameView.class, HotelCodeView.class})
  private String currency;
  @JsonView({HotelNameView.class, HotelCodeView.class})
  private BigDecimal minimumRate;
  @JsonView({HotelNameView.class, HotelCodeView.class})
  private BigDecimal finalPrice;
  @JsonView({HotelNameView.class, HotelCodeView.class})
  private String rateCode;
  @JsonView({HotelNameView.class, HotelCodeView.class})
  private String rateClassification;
  @JsonView({HotelNameView.class, HotelCodeView.class})
  private String roomType;
  @JsonView({HotelNameView.class, HotelCodeView.class})
  private int quantity;
  @JsonView({HotelNameView.class, HotelCodeView.class})
  private Integer minimumNights;
  @JsonView({HotelNameView.class, HotelCodeView.class})
  private boolean hasMlosRestriction;
  @JsonView({HotelNameView.class, HotelCodeView.class})
  private boolean hasClosedRestriction;
  @JsonView({HotelNameView.class, HotelCodeView.class})
  private String roomCategory;

  @Override
  public int compareTo(@NotNull AvailabilitiesDto o) {
    return this.getAvailableDate().compareTo(o.getAvailableDate());
  }
}
