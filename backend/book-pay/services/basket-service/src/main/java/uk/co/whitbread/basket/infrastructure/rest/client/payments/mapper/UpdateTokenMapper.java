package uk.co.whitbread.basket.infrastructure.rest.client.payments.mapper;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import uk.co.whitbread.basket.domain.model.ccuieckoh.in.UpdateTokenRequest;
import uk.co.whitbread.basket.domain.model.ccuieckoh.out.UpdateTokenResponse;
import uk.co.whitbread.basket.generated.models.payments.UpdateTokenRequestDto;
import uk.co.whitbread.basket.generated.models.payments.UpdateTokenResponseDto;

@Mapper(componentModel = "spring",
    injectionStrategy = InjectionStrategy.CONSTRUCTOR, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UpdateTokenMapper {

  UpdateTokenRequestDto toDto(UpdateTokenRequest updateTokenRequest);

  UpdateTokenResponse toModel(UpdateTokenResponseDto updateTokenResponseDto);

}
