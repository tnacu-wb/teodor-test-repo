package uk.co.whitbread.shared.auth.tenant;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

@Slf4j
public class TenantRepository {

  private final Map<String, Tenant> tenants = new ConcurrentHashMap<>();

  public Tenant findById(String tenant) {
    return tenants.get(tenant);
  }

  public void save(Tenant tenant) {
    log.info("Authentication tenant added:{}", tenant.getName());
    tenants.put(tenant.getIssuer(), tenant);
  }
}
