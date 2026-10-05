package uk.co.whitbread.spending.infrastructure.rest.client.worldline.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.co.whitbread.spending.infrastructure.config.WorldlineProperties;

@Component
public class PropertiesLoader {
  private final WorldlineProperties worldlineProperties;
  private static final String DE = "DE";

  @Autowired
  public PropertiesLoader(WorldlineProperties worldlineProperties) {
    this.worldlineProperties = worldlineProperties;
  }

  public WorldlineProperties.PropertiesByLocation getPropertiesByLocation(String location) {
    return DE.equalsIgnoreCase(location) ? worldlineProperties.getDe() : worldlineProperties.getGb();
  }

}
