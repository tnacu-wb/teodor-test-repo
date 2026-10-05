package uk.co.whitbread.infrastructure.rest.controller.groupbooking.model.in;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.co.whitbread.infrastructure.rest.controller.groupbooking.validation.MealPackagesConstraint;
import uk.co.whitbread.infrastructure.rest.controller.groupbooking.validation.NotEmptyConditional;
import uk.co.whitbread.infrastructure.rest.controller.groupbooking.validation.RoomNumberConstraint;
import uk.co.whitbread.infrastructure.rest.controller.validation.DateFormat;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@RoomNumberConstraint
@MealPackagesConstraint
@NotEmptyConditional(checkedField = "additionalInformation", condition = "isSchoolOrYouth")
public class GroupBookingRequestDto {

  @NotEmpty
  @Parameter(name = "title", example = "Mr.", required = true, schema = @Schema(type = "string"))
  private String title;

  @NotEmpty
  @Parameter(name = "firstName", example = "John", required = true, schema = @Schema(type = "string"))
  private String firstName;

  @NotEmpty
  @Parameter(name = "lastName", example = "Doe", required = true, schema = @Schema(type = "string"))
  private String lastName;

  @NotEmpty
  @Parameter(name = "emailAddress", example = "john.doe@host.com", required = true, schema = @Schema(type = "string"))
  private String emailAddress;

  @NotEmpty
  @Parameter(name = "phoneNumber", example = "02071234567", required = true, schema = @Schema(type = "string"))
  private String phoneNumber;

  @NotEmpty
  @Parameter(name = "bookerType", example = "Business", required = true, schema = @Schema(type = "string"))
  private String bookerType;

  @NotEmpty
  @Parameter(name = "purposeOfStay", example = "Business", required = true, schema = @Schema(type = "string"))
  private String purposeOfStay;

  @Parameter(name = "isSchoolOrYouth", example = "true", schema = @Schema(type = "string"))
  private Boolean isSchoolOrYouth;

  @NotEmpty
  @Parameter(name = "reasonForVisit", example = "Business Meeting", required = true, schema = @Schema(type = "string"))
  private String reasonForVisit;

  @Parameter(name = "reasonForVisitOther", example = "Team Building", schema = @Schema(type = "string"))
  private String reasonForVisitOther;

  @Parameter(name = "companyName", example = "Tech Corp", schema = @Schema(type = "string"))
  private String companyName;

  @Parameter(name = "isPackageTypeBf", example = "true", schema = @Schema(type = "boolean"))
  private Boolean isPackageTypeBf;

  @Parameter(name = "isPackageTypeMealDeal", example = "false", schema = @Schema(type = "boolean"))
  private Boolean isPackageTypeMealDeal;

  @Parameter(name = "hotelName", example = "London Euston", schema = @Schema(type = "string"))
  private String hotelName;

  @Parameter(name = "hotelBrand", example = "PI", schema = @Schema(type = "string"))
  private String hotelBrand;

  @NotEmpty
  @DateFormat
  @Parameter(name = "arrivalDate", example = "2024-07-01", required = true, schema = @Schema(type = "string"))
  private String arrivalDate;

  @NotEmpty
  @DateFormat
  @Parameter(name = "departureDate", example = "2024-07-10", required = true, schema = @Schema(type = "string"))
  private String departureDate;

  @PositiveOrZero
  @Parameter(name = "singleOccupancy", example = "5", schema = @Schema(type = "integer"))
  private Integer singleOccupancy;

  @PositiveOrZero
  @Parameter(name = "doubleOccupancy", example = "2", schema = @Schema(type = "integer"))
  private Integer doubleOccupancy;

  @PositiveOrZero
  @Parameter(name = "twinRooms", example = "1", schema = @Schema(type = "integer"))
  private Integer twinRooms;

  @Parameter(name = "isTravellingWithChild", example = "true", schema = @Schema(type = "boolean"))
  private Boolean isTravellingWithChild;

  @Parameter(name = "isAccessibleRoom", example = "false", schema = @Schema(type = "boolean"))
  private Boolean isAccessibleRoom;

  @PositiveOrZero
  @Parameter(name = "familyOf21A1C", example = "1", schema = @Schema(type = "integer"))
  private Integer familyOf21A1C;

  @PositiveOrZero
  @Parameter(name = "familyOf32A1C", example = "1", schema = @Schema(type = "integer"))
  private Integer familyOf32A1C;

  @PositiveOrZero
  @Parameter(name = "familyOf31A2C", example = "1", schema = @Schema(type = "integer"))
  private Integer familyOf31A2C;

  @PositiveOrZero
  @Parameter(name = "familyOf42A2C", example = "1", schema = @Schema(type = "integer"))
  private Integer familyOf42A2C;

  @PositiveOrZero
  @Parameter(name = "accessibleSingle", example = "1", schema = @Schema(type = "integer"))
  private Integer accessibleSingle;

  @PositiveOrZero
  @Parameter(name = "accessibleDouble", example = "1", schema = @Schema(type = "integer"))
  private Integer accessibleDouble;

  @PositiveOrZero
  @Parameter(name = "accessibleTwin", example = "1", schema = @Schema(type = "integer"))
  private Integer accessibleTwin;

  @Parameter(name = "additionalInformation", example = "We would like quiet rooms", schema = @Schema(type = "string"))
  private String additionalInformation;

  @Parameter(name = "language", example = "de", schema = @Schema(type = "string"))
  private String language;
}
