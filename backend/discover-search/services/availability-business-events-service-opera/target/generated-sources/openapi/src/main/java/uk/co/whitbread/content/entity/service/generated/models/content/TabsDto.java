package uk.co.whitbread.content.entity.service.generated.models.content;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import org.springframework.lang.Nullable;
import uk.co.whitbread.content.entity.service.generated.models.content.CentrallyStoredDto;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import javax.validation.Valid;
import javax.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import javax.annotation.Generated;

/**
 * TabsDto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-25T07:49:46.057591+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class TabsDto {

  private @Nullable CentrallyStoredDto centrallyStored;

  private @Nullable String centrallyStoredTitle;

  private @Nullable String innBusinessPay;

  public TabsDto centrallyStored(CentrallyStoredDto centrallyStored) {
    this.centrallyStored = centrallyStored;
    return this;
  }

  /**
   * Get centrallyStored
   * @return centrallyStored
   */
  @Valid 
  @Schema(name = "centrallyStored", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("centrallyStored")
  public CentrallyStoredDto getCentrallyStored() {
    return centrallyStored;
  }

  public void setCentrallyStored(CentrallyStoredDto centrallyStored) {
    this.centrallyStored = centrallyStored;
  }

  public TabsDto centrallyStoredTitle(String centrallyStoredTitle) {
    this.centrallyStoredTitle = centrallyStoredTitle;
    return this;
  }

  /**
   * Get centrallyStoredTitle
   * @return centrallyStoredTitle
   */
  
  @Schema(name = "centrallyStoredTitle", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("centrallyStoredTitle")
  public String getCentrallyStoredTitle() {
    return centrallyStoredTitle;
  }

  public void setCentrallyStoredTitle(String centrallyStoredTitle) {
    this.centrallyStoredTitle = centrallyStoredTitle;
  }

  public TabsDto innBusinessPay(String innBusinessPay) {
    this.innBusinessPay = innBusinessPay;
    return this;
  }

  /**
   * Get innBusinessPay
   * @return innBusinessPay
   */
  
  @Schema(name = "innBusinessPay", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("innBusinessPay")
  public String getInnBusinessPay() {
    return innBusinessPay;
  }

  public void setInnBusinessPay(String innBusinessPay) {
    this.innBusinessPay = innBusinessPay;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    TabsDto tabsDto = (TabsDto) o;
    return Objects.equals(this.centrallyStored, tabsDto.centrallyStored) &&
        Objects.equals(this.centrallyStoredTitle, tabsDto.centrallyStoredTitle) &&
        Objects.equals(this.innBusinessPay, tabsDto.innBusinessPay);
  }

  @Override
  public int hashCode() {
    return Objects.hash(centrallyStored, centrallyStoredTitle, innBusinessPay);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class TabsDto {\n");
    sb.append("    centrallyStored: ").append(toIndentedString(centrallyStored)).append("\n");
    sb.append("    centrallyStoredTitle: ").append(toIndentedString(centrallyStoredTitle)).append("\n");
    sb.append("    innBusinessPay: ").append(toIndentedString(innBusinessPay)).append("\n");
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

