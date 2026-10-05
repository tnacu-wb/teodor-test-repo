package uk.co.whitbread.content.entity.service.generated.models.content;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.lang.Nullable;
import uk.co.whitbread.content.entity.service.generated.models.content.BusinessNoteDto;
import uk.co.whitbread.content.entity.service.generated.models.content.NoteDto;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import javax.validation.Valid;
import javax.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import javax.annotation.Generated;

/**
 * BusinessNotesResponseDto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-27T18:51:28.563902+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class BusinessNotesResponseDto {

  @Valid
  private List<@Valid NoteDto> allowances = new ArrayList<>();

  @Valid
  private List<@Valid BusinessNoteDto> businessNotes = new ArrayList<>();

  @Valid
  private List<@Valid NoteDto> cardTypes = new ArrayList<>();

  @Valid
  private List<@Valid NoteDto> footers = new ArrayList<>();

  @Valid
  private List<@Valid NoteDto> headers = new ArrayList<>();

  @Valid
  private List<@Valid NoteDto> packages = new ArrayList<>();

  public BusinessNotesResponseDto allowances(List<@Valid NoteDto> allowances) {
    this.allowances = allowances;
    return this;
  }

  public BusinessNotesResponseDto addAllowancesItem(NoteDto allowancesItem) {
    if (this.allowances == null) {
      this.allowances = new ArrayList<>();
    }
    this.allowances.add(allowancesItem);
    return this;
  }

  /**
   * Get allowances
   * @return allowances
   */
  @Valid 
  @Schema(name = "allowances", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("allowances")
  public List<@Valid NoteDto> getAllowances() {
    return allowances;
  }

  public void setAllowances(List<@Valid NoteDto> allowances) {
    this.allowances = allowances;
  }

  public BusinessNotesResponseDto businessNotes(List<@Valid BusinessNoteDto> businessNotes) {
    this.businessNotes = businessNotes;
    return this;
  }

  public BusinessNotesResponseDto addBusinessNotesItem(BusinessNoteDto businessNotesItem) {
    if (this.businessNotes == null) {
      this.businessNotes = new ArrayList<>();
    }
    this.businessNotes.add(businessNotesItem);
    return this;
  }

  /**
   * Get businessNotes
   * @return businessNotes
   */
  @Valid 
  @Schema(name = "businessNotes", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("businessNotes")
  public List<@Valid BusinessNoteDto> getBusinessNotes() {
    return businessNotes;
  }

  public void setBusinessNotes(List<@Valid BusinessNoteDto> businessNotes) {
    this.businessNotes = businessNotes;
  }

  public BusinessNotesResponseDto cardTypes(List<@Valid NoteDto> cardTypes) {
    this.cardTypes = cardTypes;
    return this;
  }

  public BusinessNotesResponseDto addCardTypesItem(NoteDto cardTypesItem) {
    if (this.cardTypes == null) {
      this.cardTypes = new ArrayList<>();
    }
    this.cardTypes.add(cardTypesItem);
    return this;
  }

  /**
   * Get cardTypes
   * @return cardTypes
   */
  @Valid 
  @Schema(name = "cardTypes", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("cardTypes")
  public List<@Valid NoteDto> getCardTypes() {
    return cardTypes;
  }

  public void setCardTypes(List<@Valid NoteDto> cardTypes) {
    this.cardTypes = cardTypes;
  }

  public BusinessNotesResponseDto footers(List<@Valid NoteDto> footers) {
    this.footers = footers;
    return this;
  }

  public BusinessNotesResponseDto addFootersItem(NoteDto footersItem) {
    if (this.footers == null) {
      this.footers = new ArrayList<>();
    }
    this.footers.add(footersItem);
    return this;
  }

  /**
   * Get footers
   * @return footers
   */
  @Valid 
  @Schema(name = "footers", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("footers")
  public List<@Valid NoteDto> getFooters() {
    return footers;
  }

  public void setFooters(List<@Valid NoteDto> footers) {
    this.footers = footers;
  }

  public BusinessNotesResponseDto headers(List<@Valid NoteDto> headers) {
    this.headers = headers;
    return this;
  }

  public BusinessNotesResponseDto addHeadersItem(NoteDto headersItem) {
    if (this.headers == null) {
      this.headers = new ArrayList<>();
    }
    this.headers.add(headersItem);
    return this;
  }

  /**
   * Get headers
   * @return headers
   */
  @Valid 
  @Schema(name = "headers", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("headers")
  public List<@Valid NoteDto> getHeaders() {
    return headers;
  }

  public void setHeaders(List<@Valid NoteDto> headers) {
    this.headers = headers;
  }

  public BusinessNotesResponseDto packages(List<@Valid NoteDto> packages) {
    this.packages = packages;
    return this;
  }

  public BusinessNotesResponseDto addPackagesItem(NoteDto packagesItem) {
    if (this.packages == null) {
      this.packages = new ArrayList<>();
    }
    this.packages.add(packagesItem);
    return this;
  }

  /**
   * Get packages
   * @return packages
   */
  @Valid 
  @Schema(name = "packages", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("packages")
  public List<@Valid NoteDto> getPackages() {
    return packages;
  }

  public void setPackages(List<@Valid NoteDto> packages) {
    this.packages = packages;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    BusinessNotesResponseDto businessNotesResponseDto = (BusinessNotesResponseDto) o;
    return Objects.equals(this.allowances, businessNotesResponseDto.allowances) &&
        Objects.equals(this.businessNotes, businessNotesResponseDto.businessNotes) &&
        Objects.equals(this.cardTypes, businessNotesResponseDto.cardTypes) &&
        Objects.equals(this.footers, businessNotesResponseDto.footers) &&
        Objects.equals(this.headers, businessNotesResponseDto.headers) &&
        Objects.equals(this.packages, businessNotesResponseDto.packages);
  }

  @Override
  public int hashCode() {
    return Objects.hash(allowances, businessNotes, cardTypes, footers, headers, packages);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class BusinessNotesResponseDto {\n");
    sb.append("    allowances: ").append(toIndentedString(allowances)).append("\n");
    sb.append("    businessNotes: ").append(toIndentedString(businessNotes)).append("\n");
    sb.append("    cardTypes: ").append(toIndentedString(cardTypes)).append("\n");
    sb.append("    footers: ").append(toIndentedString(footers)).append("\n");
    sb.append("    headers: ").append(toIndentedString(headers)).append("\n");
    sb.append("    packages: ").append(toIndentedString(packages)).append("\n");
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

