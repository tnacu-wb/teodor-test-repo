package uk.co.whitbread.rules.manager.infrastructure.repository.mapper;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.whitbread.rules.manager.domain.model.in.RuleStatus;
import uk.co.whitbread.rules.manager.domain.model.in.VatRule;
import uk.co.whitbread.rules.manager.infrastructure.repository.model.VatRuleEntity;

@ExtendWith(MockitoExtension.class)
class VatRuleMapperTest {

  public static final LocalDateTime now = LocalDateTime.now();

  @InjectMocks
  private VatRuleMapperImpl vatRuleMapper;
  @Spy
  private VatRuleTransformer vatRuleTransformer;

  @Test
  void toDomain_givenEntityObject_shouldTransformToDomainObject() {
    //Arrange
    var vatRuleDomain = createValidVatRuleDomain();
    var vatRuleEntity = createValidVatRuleEntity();

    //Act
    var result = vatRuleMapper.toDomainModel(vatRuleEntity);

    //Assert
    assertThat(result).usingRecursiveComparison().withStrictTypeChecking()
        .isEqualTo(vatRuleDomain);
  }

  @Test
  void toDomain_givenNull_shouldReturnNull() {
    //Act
    var result = vatRuleMapper.toDomainModel(null);

    //Assert
    assertThat(result).isNull();
  }

  @Test
  void toEntityDto_givenDomainObject_shouldTransformToEntityObject() {
    //Arrange
    var vatRuleDomain = createValidVatRuleDomain();
    var vatRuleEntity = createValidVatRuleEntity();

    //Act
    var result = vatRuleMapper.toEntityDto(vatRuleDomain);

    //Assert
    assertThat(result).usingRecursiveComparison().withStrictTypeChecking()
        .isEqualTo(vatRuleEntity);
  }

  @Test
  void toEntityDto_givenNull_shouldReturnNull() {
    //Act
    var result = vatRuleMapper.toEntityDto(null);

    //Assert
    assertThat(result).isNull();
  }

  @Test
  void toEntityDto_givenNullStatus_shouldTransformToEntityObjectWithNullStatus() {
    // Arrange
    var vatRuleDomain = createValidVatRuleDomain();
    vatRuleDomain.setStatus(null);

    //Act
    var result = vatRuleMapper.toEntityDto(vatRuleDomain);

    //Assert
    assertThat(result.getStatus()).isNull();
  }

  public static VatRule createValidVatRuleDomain() {
    return VatRule.builder()
        .ruleId(1)
        .refRuleId(0)
        .status(RuleStatus.NEW)
        .createdAt(now)
        .lastModifiedAt(now)
        .enableTimestamp(null)
        .disableTimestamp(now.plusDays(30))
        .vatRegion("UK")
        .tranCode("9026")
        .build();
  }

  public static VatRuleEntity createValidVatRuleEntity() {
    var vatRuleEntity = new VatRuleEntity();
    vatRuleEntity.setRuleId(1);
    vatRuleEntity.setRefRuleId(0);
    vatRuleEntity.setStatus("NEW");
    vatRuleEntity.setCreatedAt(now);
    vatRuleEntity.setLastModifiedAt(now);
    vatRuleEntity.setEnableTimestamp(null);
    vatRuleEntity.setDisableTimestamp(now.plusDays(30));
    vatRuleEntity.setVatRegion("UK");
    vatRuleEntity.setTranCode("9026");

    return vatRuleEntity;
  }

}
