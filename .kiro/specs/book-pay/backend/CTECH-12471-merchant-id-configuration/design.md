---
parent_design: "#[[file:../../../../steering/designs/payments/design-e2e.md]]"
jira: CTECH-12471
---
# Design Document

## Overview

This design implements dynamic merchant ID resolution for Datatrans payment processing, replacing hardcoded merchant IDs with environment-aware resolution based on hotel codes. The solution uses profile-based implementations following the established pattern from `threec-payment-service-opera`.

## Architecture

```mermaid
graph TB
    subgraph "Payment Workflows"
        SW[SecureFieldsPaymentWorkflowImpl]
        MW[MobileSdkPaymentWorkflowImpl]
    end
    
    subgraph "Temporal Activities"
        PA[PaymentActivities Interface]
        PAI[PaymentActivitiesImpl]
    end
    
    subgraph "Domain Ports"
        MIR[MerchantIdResolver Interface]
    end
    
    subgraph "Infrastructure Implementations"
        PROD[ProductionMerchantIdResolver<br/>@Profile("opera-prod")]
        SAND[SandboxMerchantIdResolver<br/>@Profile("!opera-prod")]
    end
    
    subgraph "Configuration"
        MIP[MerchantIdProperties]
        YML[application.yml]
    end
    
    SW --> PA
    MW --> PA
    PA --> PAI
    PAI --> MIR
    MIR --> PROD
    MIR --> SAND
    SAND --> MIP
    MIP --> YML
```

## Components and Interfaces

### Domain Port Interface

```java
package uk.co.whitbread.payment.orchestrator.domain.ports.secondary;

/**
 * Port for resolving Datatrans merchant IDs based on hotel codes.
 */
public interface MerchantIdResolver {
    
    /**
     * Resolves the appropriate Datatrans merchant ID for the given hotel code.
     * 
     * @param hotelCode the hotel identifier (e.g. "HARHOR", "GRESOU")
     * @return the full Datatrans merchant ID (e.g. "deWB-HARHOR")
     */
    String resolveMerchantId(String hotelCode);
}
```

### Production Implementation

```java
package uk.co.whitbread.payment.orchestrator.infrastructure.adapter;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import uk.co.whitbread.payment.orchestrator.domain.ports.secondary.MerchantIdResolver;
import uk.co.whitbread.payment.orchestrator.infrastructure.config.MerchantIdProperties;

@Slf4j
@Component
@Profile("opera-prod")
@RequiredArgsConstructor
public class ProductionMerchantIdResolver implements MerchantIdResolver {

    private final MerchantIdProperties properties;
    
    @Override
    public String resolveMerchantId(String hotelCode) {
        String merchantId = properties.getPrefix() + hotelCode;
        log.debug("Resolved merchant ID for hotel {}: {}", hotelCode, merchantId);
        return merchantId;
    }
}
```

### Sandbox Implementation

```java
package uk.co.whitbread.payment.orchestrator.infrastructure.adapter;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import uk.co.whitbread.payment.orchestrator.domain.ports.secondary.MerchantIdResolver;
import uk.co.whitbread.payment.orchestrator.infrastructure.config.MerchantIdProperties;

@Slf4j
@Component
@Profile("!opera-prod")
@RequiredArgsConstructor
public class SandboxMerchantIdResolver implements MerchantIdResolver {

    private final MerchantIdProperties properties;
    
    @Override
    public String resolveMerchantId(String hotelCode) {
        String merchantId;
        
        if (properties.getProvisionedHotels().contains(hotelCode)) {
            merchantId = properties.getPrefix() + hotelCode;
            log.debug("Hotel {} is provisioned in sandbox, using specific merchant ID: {}", 
                hotelCode, merchantId);
        } else {
            merchantId = properties.getDefaultMerchantId();
            log.debug("Hotel {} not provisioned in sandbox, using default merchant ID: {}", 
                hotelCode, merchantId);
        }
        
        return merchantId;
    }
}
```

### Configuration Properties

```java
package uk.co.whitbread.payment.orchestrator.infrastructure.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Data
@Component
@ConfigurationProperties(prefix = "integrations.datatrans.merchant-id")
public class MerchantIdProperties {
    
    /**
     * Merchant ID prefix for all Datatrans merchants.
     * Default: "deWB-" (Datatrans convention for Whitbread)
     */
    private String prefix = "deWB-";
    
    /**
     * Default merchant ID for hotels not provisioned in sandbox.
     * Default: "deWB-default"
     */
    private String defaultMerchantId = "deWB-default";
    
    /**
     * List of hotel codes provisioned in Datatrans sandbox.
     * Only used by SandboxMerchantIdResolver.
     */
    private List<String> provisionedHotels = List.of("HARHOR", "GRESOU");
}
```

## Data Models

### Updated Temporal Activity Interface

```java
// Addition to PaymentActivities interface
@ActivityMethod
String resolveMerchantId(String hotelCode);
```

### Updated Activity Implementation

```java
// Addition to PaymentActivitiesImpl class
private final MerchantIdResolver merchantIdResolver;

@Override
public String resolveMerchantId(String hotelCode) {
    log.info("Resolving merchant ID for hotelCode={}", hotelCode);
    return merchantIdResolver.resolveMerchantId(hotelCode);
}
```

## API Contracts

No external API changes required. This is an internal refactoring that replaces hardcoded merchant IDs with dynamic resolution.

### Configuration Contract

```yaml
integrations:
  datatrans:
    merchant-id:
      prefix: "deWB-"
      default-merchant-id: "deWB-default"
      provisioned-hotels:
        - HARHOR
        - GRESOU
```

## Error Handling

### Null/Empty Hotel Code Handling

```java
@Override
public String resolveMerchantId(String hotelCode) {
    if (hotelCode == null || hotelCode.trim().isEmpty()) {
        log.warn("Hotel code is null or empty, using default merchant ID");
        return properties.getDefaultMerchantId();
    }
    // ... resolution logic
}
```

### Configuration Validation

```java
@PostConstruct
public void validate() {
    if (prefix == null || prefix.trim().isEmpty()) {
        throw new IllegalStateException("Merchant ID prefix must not be null or empty");
    }
    if (defaultMerchantId == null || defaultMerchantId.trim().isEmpty()) {
        throw new IllegalStateException("Default merchant ID must not be null or empty");
    }
}
```

## Testing Strategy

### Unit Tests

1. **ProductionMerchantIdResolver**:
   - Test direct hotel code to merchant ID mapping
   - Test with various hotel codes
   - Test with null/empty input handling

2. **SandboxMerchantIdResolver**:
   - Test provisioned hotel codes return specific merchant IDs
   - Test non-provisioned hotel codes return default merchant ID
   - Test empty provisioned hotels list
   - Test null/empty input handling

3. **Workflow Integration**:
   - Mock activities to return resolved merchant ID
   - Verify workflow uses resolved merchant ID in Datatrans requests
   - Test both SecureFields and MobileSDK workflows

### Integration Tests

1. **Profile Activation**:
   - Test `@Profile("opera-prod")` activates ProductionMerchantIdResolver
   - Test `@Profile("!opera-prod")` activates SandboxMerchantIdResolver

2. **Configuration Binding**:
   - Test properties load correctly from application.yml
   - Test property validation on startup

### Property-Based Testing

Using jqwik for fuzz testing:
- Generate random hotel codes and verify merchant ID format
- Test provisioned vs non-provisioned hotel routing in sandbox

## Workflow Integration Points

### Secure Fields Workflow Changes

```java
// Current code (line ~140 in SecureFieldsPaymentWorkflowImpl)
// TODO: derive merchantId from hotelId via Payment Method Entity Service
//       once multiple hotel merchant IDs are provisioned in Datatrans sandbox.
String merchantId = "deWB-ABEAIB";

// New code
String merchantId = activities.resolveMerchantId(reservation.hotelId());
```

### Mobile SDK Workflow Changes

```java
// Current code (line ~310 in MobileSdkPaymentWorkflowImpl)  
// TODO: derive merchantId from hotelId via Payment Method Entity Service
//       once multiple hotel merchant IDs are provisioned in Datatrans sandbox.
String merchantId = "deWB-ABEAIB";

// New code  
String merchantId = activities.resolveMerchantId(reservation.hotelId());
```

## Configuration Management

### Environment-Specific Configuration

**application.yml** (base configuration):
```yaml
integrations:
  datatrans:
    merchant-id:
      prefix: "deWB-"
      default-merchant-id: "deWB-default"
      provisioned-hotels:
        - HARHOR
        - GRESOU
```

**application-opera-dev.yml** (dev-specific overrides):
```yaml
integrations:
  datatrans:
    merchant-id:
      provisioned-hotels:
        - HARHOR
        - GRESOU  
        - TESTHT
```

**application-opera-prod.yml** (production - no overrides needed):
```yaml
# Production uses all hotel codes directly, no configuration needed
```