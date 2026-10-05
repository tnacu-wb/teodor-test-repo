package uk.co.whitbread.account.infrastructure.rest.client.marketing.mapper;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import uk.co.whitbread.account.domain.model.in.ConfirmDoubleOptInRequest;
import uk.co.whitbread.account.domain.model.in.MarketingPreferencesRequest;
import uk.co.whitbread.account.domain.model.in.MarketingPreferencesRequestV2;
import uk.co.whitbread.account.infrastructure.rest.client.marketing.model.in.ConfirmDoubleOptInRequestDto;
import uk.co.whitbread.account.infrastructure.rest.client.marketing.model.in.MarketingPreferencesRequestDto;
import uk.co.whitbread.account.infrastructure.rest.client.marketing.model.in.MarketingPreferencesRequestV2Dto;
import uk.co.whitbread.account.infrastructure.rest.controller.account.model.in.UpdatePreferencesRequestDto;
import uk.co.whitbread.account.infrastructure.rest.controller.account.model.in.UpdatePreferencesRequestV2Dto;

@Mapper(componentModel = "spring",
    injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface MarketingPreferencesMapper {
  MarketingPreferencesRequestDto
        toMarketingPreferencesRequestDto(MarketingPreferencesRequest marketingPreferencesRequest);

  MarketingPreferencesRequestV2Dto
      toMarketingPreferencesRequestDto(MarketingPreferencesRequestV2 marketingPreferencesRequest);

  MarketingPreferencesRequest
        toMarketingPreferencesRequestModel(UpdatePreferencesRequestDto updatePreferencesRequestDto);

  MarketingPreferencesRequestV2
      toMarketingPreferencesRequestModel(UpdatePreferencesRequestV2Dto updatePreferencesRequestDto);

  ConfirmDoubleOptInRequestDto toConfirmDoubleOptInDto(ConfirmDoubleOptInRequest confirmDoubleOptInRequest);

  ConfirmDoubleOptInRequest toConfirmDoubleOptInModel(ConfirmDoubleOptInRequestDto confirmDoubleOptInRequestDto);
}
