package uk.co.whitbread.hotel.ohip.adapter.generated.models;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.lang.Nullable;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.DonationPackageDto;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * DonationPackagesResponseDto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-27T18:50:34.031950+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class DonationPackagesResponseDto {

  @Valid
  private @Nullable List<@Valid DonationPackageDto> donationPackages;

  public DonationPackagesResponseDto donationPackages(List<@Valid DonationPackageDto> donationPackages) {
    this.donationPackages = donationPackages;
    return this;
  }

  public DonationPackagesResponseDto addDonationPackagesItem(DonationPackageDto donationPackagesItem) {
    if (this.donationPackages == null) {
      this.donationPackages = new ArrayList<>();
    }
    this.donationPackages.add(donationPackagesItem);
    return this;
  }

  /**
   * Get donationPackages
   * @return donationPackages
   */
  @Valid 
  @Schema(name = "donationPackages", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("donationPackages")
  public List<@Valid DonationPackageDto> getDonationPackages() {
    return donationPackages;
  }

  public void setDonationPackages(List<@Valid DonationPackageDto> donationPackages) {
    this.donationPackages = donationPackages;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    DonationPackagesResponseDto donationPackagesResponseDto = (DonationPackagesResponseDto) o;
    return Objects.equals(this.donationPackages, donationPackagesResponseDto.donationPackages);
  }

  @Override
  public int hashCode() {
    return Objects.hash(donationPackages);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class DonationPackagesResponseDto {\n");
    sb.append("    donationPackages: ").append(toIndentedString(donationPackages)).append("\n");
    sb.append("}");
    return sb.toString();
  }

  /**
   * Convert the given object to string with each line indented by 4 spaces
   * (except the first line).
   */
  private String toIndentedString(Object o) {
    if (o == null) {
      return "null";
    }
    return o.toString().replace("\n", "\n    ");
  }
}

