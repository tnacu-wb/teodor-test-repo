package uk.co.whitbread.piba.account.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.co.whitbread.piba.account.properties.WorldlineRestProperties;

@Component
public class PropertiesLoader {
  private final WorldlineRestProperties worldlineRestProperties;
  private static final String DE = "DE";

  @Autowired
  public PropertiesLoader(WorldlineRestProperties worldlineRestProperties) {
    this.worldlineRestProperties = worldlineRestProperties;
  }

  public WorldlineRestProperties.PropertiesByLocation getPropertiesByLocation(String location) {
    return DE.equalsIgnoreCase(location) ? worldlineRestProperties.getDe() : worldlineRestProperties.getGb();
  }

}
