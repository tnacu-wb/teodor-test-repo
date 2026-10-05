package uk.co.whitbread.domain.model.groupbooking.in;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import uk.co.whitbread.domain.model.validation.SelfValidation;
import uk.co.whitbread.infrastructure.rest.controller.validation.DateFormat;


@Data
@Builder(toBuilder = true)
@ToString
@EqualsAndHashCode(callSuper = false)
public class GroupBookingRequest implements SelfValidation<GroupBookingRequest> {

  @NotEmpty
  private String title;

  @NotEmpty
  private String firstName;

  @NotEmpty
  private String lastName;

  @NotEmpty
  private String emailAddress;

  @NotEmpty
  private String phoneNumber;

  @NotEmpty
  private String bookerType;

  @NotEmpty
  private String purposeOfStay;

  private Boolean isSchoolOrYouth;

  @NotEmpty
  private String reasonForVisit;

  private String reasonForVisitOther;

  private String companyName;

  private Boolean isPackageTypeBf;

  private Boolean isPackageTypeMealDeal;

  @NotEmpty
  private String hotelCode;

  @NotEmpty
  private String hotelName;

  @NotEmpty
  private String hotelBrand;

  @NotEmpty
  @DateFormat
  private String arrivalDate;

  @NotEmpty
  @DateFormat
  private String departureDate;

  @PositiveOrZero
  private Integer singleOccupancy;

  @PositiveOrZero
  private Integer doubleOccupancy;

  @PositiveOrZero
  private Integer twinRooms;

  private Boolean isTravellingWithChild;

  private Boolean isAccessibleRoom;

  @PositiveOrZero
  private Integer familyOf21A1C;

  @PositiveOrZero
  private Integer familyOf32A1C;

  @PositiveOrZero
  private Integer familyOf31A2C;

  @PositiveOrZero
  private Integer familyOf42A2C;

  @PositiveOrZero
  private Integer accessibleSingle;

  @PositiveOrZero
  private Integer accessibleDouble;

  @PositiveOrZero
  private Integer accessibleTwin;

  private String additionalInformation;

  private String language;

}
