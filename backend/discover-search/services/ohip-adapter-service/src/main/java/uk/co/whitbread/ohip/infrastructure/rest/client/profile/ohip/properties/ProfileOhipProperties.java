package uk.co.whitbread.ohip.infrastructure.rest.client.profile.ohip.properties;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
public class ProfileOhipProperties {

  private final String profilesEndpoint;
  private final String addProfileEndpoint;
  private final String companiesEndpoint;
  private final String companiesByCompanyIdEndpoint;
  private final String reservationGuestEndpoint;
  private final String hubId;


  public ProfileOhipProperties(
      @Value("${config.service.ohip.profilesEndpoint}") String profilesEndpoint,
      @Value("${config.service.ohip.addProfileEndpoint}") String addProfileEndpoint,
      @Value("${config.service.ohip.companiesEndpoint}") String companiesEndpoint,
      @Value("${config.service.ohip.companiesByCompanyIdEndpoint}") String companiesByCompanyIdEndpoint,
      @Value("${config.service.ohip.reservationGuestEndpoint}") String reservationGuestEndpoint,
      @Value("${config.service.ohip.hubId}") String hubId
  ) {
    this.profilesEndpoint = profilesEndpoint;
    this.addProfileEndpoint = addProfileEndpoint;
    this.companiesEndpoint = companiesEndpoint;
    this.companiesByCompanyIdEndpoint = companiesByCompanyIdEndpoint;
    this.reservationGuestEndpoint = reservationGuestEndpoint;
    this.hubId = hubId;
  }

}
