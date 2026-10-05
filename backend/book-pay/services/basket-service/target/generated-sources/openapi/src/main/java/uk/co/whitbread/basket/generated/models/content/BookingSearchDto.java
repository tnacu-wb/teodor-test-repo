package uk.co.whitbread.basket.generated.models.content;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import org.springframework.lang.Nullable;
import uk.co.whitbread.basket.generated.models.content.DashboardRedirectDto;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * BookingSearchDto
 */
@com.fasterxml.jackson.annotation.JsonIgnoreProperties(ignoreUnknown = true)

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-09-04T08:10:06.327224+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class BookingSearchDto {

  private @Nullable DashboardRedirectDto dashboardRedirect;

  private @Nullable Boolean show;

  public BookingSearchDto dashboardRedirect(DashboardRedirectDto dashboardRedirect) {
    this.dashboardRedirect = dashboardRedirect;
    return this;
  }

  /**
   * Get dashboardRedirect
   * @return dashboardRedirect
   */
  @Valid 
  @Schema(name = "dashboardRedirect", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("dashboardRedirect")
  public DashboardRedirectDto getDashboardRedirect() {
    return dashboardRedirect;
  }

  public void setDashboardRedirect(DashboardRedirectDto dashboardRedirect) {
    this.dashboardRedirect = dashboardRedirect;
  }

  public BookingSearchDto show(Boolean show) {
    this.show = show;
    return this;
  }

  /**
   * Get show
   * @return show
   */
  
  @Schema(name = "show", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("show")
  public Boolean getShow() {
    return show;
  }

  public void setShow(Boolean show) {
    this.show = show;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    BookingSearchDto bookingSearchDto = (BookingSearchDto) o;
    return Objects.equals(this.dashboardRedirect, bookingSearchDto.dashboardRedirect) &&
        Objects.equals(this.show, bookingSearchDto.show);
  }

  @Override
  public int hashCode() {
    return Objects.hash(dashboardRedirect, show);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class BookingSearchDto {\n");
    sb.append("    dashboardRedirect: ").append(toIndentedString(dashboardRedirect)).append("\n");
    sb.append("    show: ").append(toIndentedString(show)).append("\n");
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

