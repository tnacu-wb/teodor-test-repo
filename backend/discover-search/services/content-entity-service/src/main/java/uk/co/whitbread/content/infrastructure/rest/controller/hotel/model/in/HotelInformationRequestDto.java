package uk.co.whitbread.content.infrastructure.rest.controller.hotel.model.in;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.EqualsAndHashCode;
import lombok.Value;
import lombok.experimental.SuperBuilder;
import uk.co.whitbread.content.infrastructure.rest.controller.model.in.LocalizationBaseClassDto;

@Value
@EqualsAndHashCode(callSuper = true)
@SuperBuilder(toBuilder = true)
public class HotelInformationRequestDto extends LocalizationBaseClassDto {

  @Parameter(in = ParameterIn.QUERY, name = "channel", example = "BB",
      schema = @Schema(type = "string"))
  private String channel;

  @Parameter(in = ParameterIn.QUERY, name = "subchannel", example = "WEB",
      schema = @Schema(type = "string"))
  private String subchannel;

  public HotelInformationRequestDto(String country, String language,
      String channel, String subchannel) {
    super(country, language);
    this.channel = channel;
    this.subchannel = subchannel;
  }

  protected HotelInformationRequestDto(final HotelInformationRequestDtoBuilder<?, ?> b,
      String channel, String subchannel) {
    super(b);
    this.channel = channel;
    this.subchannel = subchannel;
  }

}
