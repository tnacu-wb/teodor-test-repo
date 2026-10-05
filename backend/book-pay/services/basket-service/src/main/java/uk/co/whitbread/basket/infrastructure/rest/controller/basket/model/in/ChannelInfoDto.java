package uk.co.whitbread.basket.infrastructure.rest.controller.basket.model.in;


import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.co.whitbread.basket.domain.model.validation.SelfValidation;

@Data
@Builder
@NoArgsConstructor
public class ChannelInfoDto implements SelfValidation<ChannelInfoDto> {

  private String channel;
  private String subChannel;

  public ChannelInfoDto(String channel, String subChannel) {
    this.channel = channel;
    this.subChannel = subChannel;
    this.validateSelf();
  }
}

