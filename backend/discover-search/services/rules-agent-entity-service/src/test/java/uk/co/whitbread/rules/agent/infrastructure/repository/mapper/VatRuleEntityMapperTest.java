package uk.co.whitbread.rules.agent.infrastructure.repository.mapper;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static uk.co.whitbread.rules.agent.domain.model.out.RuleStatus.ACTIVE;

import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;
import uk.co.whitbread.rules.agent.domain.model.out.VatRule;
import uk.co.whitbread.rules.agent.infrastructure.repository.model.VatRuleEntity;

class VatRuleEntityMapperTest {

  private static final String VAT_REGION = "UK";
  private static final String PKG_CODE = "MDBEVA";
  private static final String TRAN_CODE = "9028";

  private final VatRuleEntityMapper vatRuleEntityMapper = new VatRuleEntityMapperImpl();

  @Test
  void toModel_givenEntityObject_shouldTransformToModelObject() {
    //Arrange
    var time = LocalDateTime.now();
    var vatRuleEntity = new VatRuleEntity();
    vatRuleEntity.setCreatedAt(time);
    vatRuleEntity.setLastModifiedAt(time);
    vatRuleEntity.setVatRegion(VAT_REGION);
    vatRuleEntity.setPkgCode(PKG_CODE);
    vatRuleEntity.setTranCode(TRAN_CODE);
    vatRuleEntity.setRuleId(1);
    vatRuleEntity.setStatus(ACTIVE.name());
    var vatRule = VatRule.builder()
        .createdAt(time)
        .lastModifiedAt(time)
        .vatRegion(VAT_REGION)
        .pkgCode(PKG_CODE)
        .tranCode(TRAN_CODE)
        .ruleId(1)
        .status(ACTIVE)
        .build();

    //Act
    var result = vatRuleEntityMapper.toModel(vatRuleEntity);

    //Assert
    assertThat(result).usingRecursiveComparison()
        .withStrictTypeChecking()
        .isEqualTo(vatRule);
  }

  @Test
  void toModel_givenNullEntityObject_shouldReturnNull() {
    //Act
    var result = vatRuleEntityMapper.toModel(null);

    //Assert
    assertThat(result).isNull();
  }
}
