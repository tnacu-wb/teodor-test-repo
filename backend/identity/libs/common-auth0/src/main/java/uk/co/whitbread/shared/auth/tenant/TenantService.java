package uk.co.whitbread.shared.auth.tenant;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class TenantService {

  private final TenantRepository tenantRepository;

  public String getNamespace(String issuer) {
    return Optional.ofNullable(this.tenantRepository.findById(issuer))
        .map(Tenant::getNamespace)
        .orElseThrow(() -> {
          log.error("UNKNOWN_TENANT");
          return new IllegalArgumentException("UNKNOWN_TENANT");
        });
  }
}