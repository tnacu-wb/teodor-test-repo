package uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;
import uk.co.whitbread.ohip.domain.model.feature.FeatureFlag;
import uk.co.whitbread.ohip.domain.model.feature.UnleashWrapper;
import uk.co.whitbread.ohip.domain.model.reservation.in.ConsumptionDetails;
import uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.model.in.ConsumptionDetailsDto;

@Mapper(componentModel = "spring")
public abstract class ConsumptionDetailsMapper {

  protected UnleashWrapper<FeatureFlag> unleashWrapper;

  /**
   * Sets the UnleashWrapper dependency using setter injection.
   *
   * @param unleashWrapper the unleash wrapper for feature flag management
   */
  @Autowired
  protected void setUnleashWrapper(UnleashWrapper<FeatureFlag> unleashWrapper) {
    this.unleashWrapper = unleashWrapper;
  }

  /**
   * Maps ConsumptionDetailsDto to ConsumptionDetails domain model.
   * Conditionally sets defaultQuantity based on feature flag.
   *
   * @param dto the DTO to map from
   * @return the mapped domain model
   */
  @Mapping(target = "defaultQuantity", expression = "java(mapDefaultQuantity(dto))")
  public abstract ConsumptionDetails toModel(ConsumptionDetailsDto dto);

  /**
   * Determines the defaultQuantity value based on feature flag.
   * When feature flag is enabled, returns the totalQuantity value.
   * When disabled, returns null (field will be excluded from JSON).
   *
   * @param dto the DTO containing totalQuantity
   * @return the defaultQuantity value or null
   */
  protected Integer mapDefaultQuantity(ConsumptionDetailsDto dto) {
    if (dto == null) {
      return null;
    }

    var featureFlag = unleashWrapper.featureFlag().getConsumptionDetailsDefaultQuantity();
    if (featureFlag != null && unleashWrapper.isEnabled(featureFlag)) {
      return dto.getTotalQuantity();
    }

    return null;
  }
}

