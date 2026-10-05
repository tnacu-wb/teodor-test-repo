package uk.co.whitbread.basket.infrastructure.rest.client.marketing.mapper;

import java.util.Collections;
import java.util.Objects;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import uk.co.whitbread.basket.domain.model.marketing.out.MarketingLocale;
import uk.co.whitbread.basket.domain.model.marketing.out.MarketingPreferences;
import uk.co.whitbread.basket.generated.models.marketing.Customer;
import uk.co.whitbread.basket.generated.models.marketing.SourceDetails;
import uk.co.whitbread.basket.generated.models.marketing.UpdatePreferencesRequest;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR,
    unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface MarketingMapper {

  default UpdatePreferencesRequest toUpdatePreferencesRequestDto(
      MarketingPreferences marketingPreferences,
      String defaultBrandCode, String defaultLanguage, String defaultJourney,
      String defaultChannel) {

    Customer customer = new Customer();
    customer.setTitle(marketingPreferences.getCustomer().getTitle());
    customer.setFirstName(marketingPreferences.getCustomer().getFirstName());
    customer.setLastName(marketingPreferences.getCustomer().getLastName());
    String language =
        marketingPreferences.getCustomer().getLanguage() != null ? marketingPreferences.getCustomer().getLanguage()
            : defaultLanguage;
    customer.setLanguage(language);
    customer.setCountryOfResidence(
        Objects.requireNonNullElse(marketingPreferences.getCustomer().getCountryOfResidence(), ""));

    // hardcoding the values for now
    // journey will remain the same
    // chanel may be different for BB flow
    SourceDetails sourceDetails = new SourceDetails();
    sourceDetails.setJourney(defaultJourney);
    sourceDetails.setChannel(defaultChannel);
    sourceDetails.setLocale(MarketingLocale.fromLanguage(language).name());

    UpdatePreferencesRequest updatePreferencesRequest = new UpdatePreferencesRequest();
    updatePreferencesRequest.setBrandCodes(Collections.singletonList(defaultBrandCode));
    updatePreferencesRequest.setOptIn(marketingPreferences.getOptIn());
    updatePreferencesRequest.setCustomer(customer);
    updatePreferencesRequest.setSourceDetails(sourceDetails);

    return updatePreferencesRequest;
  }

}
