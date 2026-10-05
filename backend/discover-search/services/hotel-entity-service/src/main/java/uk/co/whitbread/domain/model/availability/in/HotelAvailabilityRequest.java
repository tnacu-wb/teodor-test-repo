package uk.co.whitbread.domain.model.availability.in;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import uk.co.whitbread.domain.model.validation.SelfValidation;
import uk.co.whitbread.domain.model.validation.ValidAvailabilityDates;
import uk.co.whitbread.infrastructure.rest.controller.availability.model.in.PromoKind;

@Data
@Builder(toBuilder = true)
@ToString
@EqualsAndHashCode(callSuper = false)
@ValidAvailabilityDates
public class HotelAvailabilityRequest implements SelfValidation<HotelAvailabilityRequest> {

  @NotNull
  String hotelId;
  @NotEmpty
  String arrivalDate;
  @NotEmpty
  String departureDate;
  List<String> roomTypes;
  @NotEmpty
  List<Integer> adultsNumber;
  List<Integer> childrenNumber;
  List<Boolean> cotsRequired;
  String companyId;
  String channel;
  String subchannel;
  String language;
  String country;
  List<String> ratePlanCodes;
  String promotionCode;
  String originalBasketReference;
  String softBundle;
  PromoKind promoKind;

  @SuppressWarnings("squid:S107")
  public HotelAvailabilityRequest(
      String hotelId, String arrivalDate, String departureDate,
      List<String> roomTypes, List<Integer> adultsNumber,
      List<Integer> childrenNumber,
      List<Boolean> cotsRequired, String companyId, String channel,
      String subchannel, String language, String country, List<String> ratePlanCodes,
      String promotionCode, String originalBasketReference, String softBundle,
      PromoKind promoKind) {
    this.hotelId = hotelId;
    this.arrivalDate = arrivalDate;
    this.departureDate = departureDate;
    this.roomTypes = roomTypes;
    this.adultsNumber = adultsNumber;
    this.childrenNumber = childrenNumber;
    this.cotsRequired = cotsRequired;
    this.companyId = companyId;
    this.channel = channel;
    this.subchannel = subchannel;
    this.language = language;
    this.country = country;
    this.ratePlanCodes = ratePlanCodes;
    this.promotionCode = promotionCode;
    this.originalBasketReference = originalBasketReference;
    this.softBundle = softBundle;
    this.promoKind = promoKind;
    this.validateSelf();
  }
}
