package uk.co.whitbread.account.infrastructure.rest.controller.account.model.in;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import uk.co.whitbread.account.infrastructure.rest.controller.account.model.validation.ValidateSourceSystem;

@Data
@Builder(toBuilder = true)
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
public class SourceDetailsDto {
  @ValidateSourceSystem(enumClass = SourceChannelDto.class)
  private String channel;
  @ValidateSourceSystem(enumClass = UserJourneyDto.class)
  private String journey;
  @ValidateSourceSystem(enumClass = SourceLocaleDto.class)
  private String locale;
}
