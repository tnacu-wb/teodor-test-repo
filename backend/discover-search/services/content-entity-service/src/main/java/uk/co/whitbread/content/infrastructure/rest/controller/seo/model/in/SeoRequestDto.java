package uk.co.whitbread.content.infrastructure.rest.controller.seo.model.in;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SeoRequestDto {

  @Parameter(in = ParameterIn.QUERY, name = "hotelId", example = "MANOLD",
      required = true, schema = @Schema(type = "string"))
  private String hotelId;

  @NotEmpty
  @Pattern(regexp = "Home|SRP|HDP|Ancillaries|GDP|Payment|Confirmation|Amend|Dashboard|CYB|Register|CYT",
      flags = Pattern.Flag.CASE_INSENSITIVE, message = "Unknown Page")
  @Parameter(in = ParameterIn.QUERY, name = "page", example = "hdp", required = true,
      schema = @Schema(type = "string"))
  private String page;

  @NotEmpty
  @Parameter(in = ParameterIn.QUERY, name = "country", example = "GB", required = true,
      schema = @Schema(type = "string"))
  private String country;

  @NotEmpty
  @Parameter(in = ParameterIn.QUERY, name = "language", example = "en", required = true,
      schema = @Schema(type = "string"))
  private String language;

  private String bookingFlowId;

  public SeoRequestDto(String hotelId, String page, String country, String language, String bookingFlowId) {
    this.hotelId = hotelId;
    this.page = page;
    this.country = country;
    this.language = language;
    this.bookingFlowId = bookingFlowId;
  }
}
