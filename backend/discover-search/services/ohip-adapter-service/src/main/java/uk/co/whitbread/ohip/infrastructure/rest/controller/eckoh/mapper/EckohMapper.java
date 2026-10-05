package uk.co.whitbread.ohip.infrastructure.rest.controller.eckoh.mapper;

import org.mapstruct.Mapper;
import uk.co.whitbread.ohip.domain.model.eckoh.in.EckohWebhook;
import uk.co.whitbread.ohip.infrastructure.rest.controller.eckoh.model.in.EckohWebhookRequestDto;

@Mapper(componentModel = "spring")
public interface EckohMapper {

  EckohWebhook toModel(EckohWebhookRequestDto eckohWebhookRequestDto);
}
