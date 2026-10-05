package uk.co.whitbread.reservation.domain.model.srp.in;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.Value;
import uk.co.whitbread.reservation.domain.model.validator.SelfValidation;

@Value
@Builder(toBuilder = true)
@ToString
@EqualsAndHashCode(callSuper = false)
public class HotelAvailabilitiesRequest implements SelfValidation<HotelAvailabilitiesRequest> {

  @NotEmpty
  String location;
  @NotNull
  LocationFormatEnum locationFormat;
  @NotNull
  Integer radius;
  @NotNull
  RadiusUnitEnum radiusUnit;
  String arrivalDate;
  String departureDate;
  @NotEmpty
  String language;
  @NotEmpty
  String country;
  @NotEmpty
  List<Integer> adultsNumber;
  @NotEmpty
  List<Integer> childrenNumber;
  @NotEmpty
  List<String> roomTypes;
  @NotNull
  OldWorldChannelEnum oldWorldChannel;
  @NotNull
  String channel;
  @NotNull
  String subChannel;
  String companyId;
  @NotNull
  Integer page;
  @NotNull
  Integer pageSize;


  Integer lazyLoadPageSize;
  String sort;
  List<String> filters;
  String authorization;
  List<String> ratePlanCodes;

  public HotelAvailabilitiesRequest(String location, LocationFormatEnum locationFormat,
                                    Integer radius,
                                    RadiusUnitEnum radiusUnit,
                                    String arrivalDate,
                                    String departureDate,
                                    String language, String country, List<Integer> adultsNumber,
                                    List<Integer> childrenNumber,
                                    List<String> roomTypes, OldWorldChannelEnum oldWorldChannel,
                                    String channel, String subChannel, String companyId,
                                    Integer page, Integer pageSize, Integer lazyLoadPageSize, String sort,
                                    List<String> filters, String authorization, List<String> ratePlanCodes) {
    this.location = location;
    this.locationFormat = locationFormat;
    this.radius = radius;
    this.radiusUnit = radiusUnit;
    this.arrivalDate = arrivalDate;
    this.departureDate = departureDate;
    this.language = language;
    this.country = country;
    this.adultsNumber = adultsNumber;
    this.childrenNumber = childrenNumber;
    this.roomTypes = roomTypes;
    this.oldWorldChannel = oldWorldChannel;
    this.channel = channel;
    this.subChannel = subChannel;
    this.companyId = companyId;
    this.page = page;
    this.pageSize = pageSize;
    this.lazyLoadPageSize = lazyLoadPageSize;
    this.sort = sort;
    this.filters = filters;
    this.authorization = authorization;
    this.ratePlanCodes = ratePlanCodes;
    this.validateSelf();
  }
}
