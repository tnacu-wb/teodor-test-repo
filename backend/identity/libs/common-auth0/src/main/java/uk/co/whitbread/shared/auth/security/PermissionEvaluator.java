package uk.co.whitbread.shared.auth.security;

import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import uk.co.whitbread.shared.auth.security.model.RbacRuleHasAccessResponse;
import uk.co.whitbread.shared.auth.tenant.SecurityProperties;

@Component
@ConditionalOnProperty(prefix = "auth", name = "rulesEngineHost")
@RequiredArgsConstructor
public class PermissionEvaluator {

  private static final String RESOURCE_ID_QUERY_PARAM = "resourceId";
  private static final String ROLE_ID_LIST_QUERY_PARAM = "roleIdList";

  private final AuthenticatedUserService authenticatedUserService;
  private final WebClient rulesServiceWebClient;
  private final SecurityProperties securityProperties;

  public boolean hasAccess(String resourceId) {

    var grantedAuthorities = authenticatedUserService.getAuthenticatedUserAuthorities();
    var ruleHasAccessResponse = rulesServiceWebClient.get().uri(
            uriBuilder -> uriBuilder.path(securityProperties.getIsAllowedAccessForRoleIdsEndpoint())
                .queryParam(RESOURCE_ID_QUERY_PARAM, resourceId)
                .queryParam(ROLE_ID_LIST_QUERY_PARAM, grantedAuthorities)
                .build())
        .retrieve()
        .bodyToMono(RbacRuleHasAccessResponse.class).block();
    return Objects.requireNonNull(ruleHasAccessResponse).getHasAccess();
  }
}
