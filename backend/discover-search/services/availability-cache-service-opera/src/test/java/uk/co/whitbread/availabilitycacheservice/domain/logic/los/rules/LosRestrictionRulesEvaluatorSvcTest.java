package uk.co.whitbread.availabilitycacheservice.domain.logic.los.rules;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.whitbread.availabilitycacheservice.domain.model.availabilities.RatePlan;
import uk.co.whitbread.availabilitycacheservice.domain.model.enums.LosRestrictionName;
import uk.co.whitbread.availabilitycacheservice.domain.model.los.rules.LosRestriction;
import uk.co.whitbread.availabilitycacheservice.domain.ports.primary.LosRestrictionRule;
import uk.co.whitbread.availabilitycacheservice.domain.ports.primary.LosRestrictionRulesEvaluator;
import uk.co.whitbread.availabilitycacheservice.infrastructure.rest.request.SearchCriteria;

@Slf4j
@ExtendWith(MockitoExtension.class)
class LosRestrictionRulesEvaluatorSvcTest {

  private final RatePlan hoteRate = RatePlan.builder().classification("A").build();
  @Mock
  private LosRestrictionRule losRule;
  private LosRestrictionRulesEvaluator losRestrictionSvc;
  private SearchCriteria searchCriteria;

  @BeforeEach
  void setUp() throws Exception {
    losRestrictionSvc = new LosRestrictionRulesEvaluatorSvc();
    LosRestrictionRulesEvaluator.registerRule(losRule);
    searchCriteria = buildSearchCriteria(2);
  }

  @AfterEach
  void tearDown() throws Exception {
    LosRestrictionRulesEvaluator.clearRules();
  }

  @Test
  void testEvaluateRestrictionRulesForCtaRestrictions() {
    hoteRate.setMinNights(999);
    when(losRule.isApplicable(Mockito.any(RatePlan.class), Mockito.any(SearchCriteria.class))).thenReturn(true);
    when(losRule.getRuleName()).thenReturn(LosRestrictionName.CTA_RESTRICTIONS);
    final LosRestriction restrictionToApply = losRestrictionSvc.evaluateRestrictionRules(hoteRate, searchCriteria);
    log.info("Restriction to apply:{}", restrictionToApply);
    assertThat(restrictionToApply).isNotNull();
    assertThat(restrictionToApply.getLosRule()).isEqualTo(LosRestrictionName.CTA_RESTRICTIONS);
  }

  @Test
  void testEvaluateRestrictionRulesForMinNightRestrictions() {
    hoteRate.setMinNights(3);
    hoteRate.setMaxNights(0);
    when(losRule.isApplicable(Mockito.any(RatePlan.class), Mockito.any(SearchCriteria.class))).thenReturn(true);
    when(losRule.getRuleName()).thenReturn(LosRestrictionName.MIN_NIGHT_RESTRICTIONS);
    final LosRestriction restrictionToApply = losRestrictionSvc.evaluateRestrictionRules(hoteRate, searchCriteria);
    log.info("Restriction to apply:{}", restrictionToApply);
    assertThat(restrictionToApply).isNotNull();
    assertThat(restrictionToApply.getLosRule()).isEqualTo(LosRestrictionName.MIN_NIGHT_RESTRICTIONS);
  }

  @Test
  void testEvaluateRestrictionRulesForMaxNightRestrictions() {
    hoteRate.setMinNights(5);
    hoteRate.setMaxNights(2);
    when(losRule.isApplicable(Mockito.any(RatePlan.class), Mockito.any(SearchCriteria.class))).thenReturn(true);
    when(losRule.getRuleName()).thenReturn(LosRestrictionName.MAX_NIGHT_RESTRICTIONS);
    final LosRestriction restrictionToApply = losRestrictionSvc.evaluateRestrictionRules(hoteRate, searchCriteria);
    log.info("Restriction to apply:{}", restrictionToApply);
    assertThat(restrictionToApply).isNotNull();
    assertThat(restrictionToApply.getLosRule()).isEqualTo(LosRestrictionName.MAX_NIGHT_RESTRICTIONS);
  }

  @Test
  void testEvaluateRestrictionRulesForMinMaxNightRestrictions() {
    hoteRate.setMinNights(4);
    hoteRate.setMaxNights(1);
    when(losRule.isApplicable(Mockito.any(RatePlan.class), Mockito.any(SearchCriteria.class))).thenReturn(true);
    when(losRule.getRuleName()).thenReturn(LosRestrictionName.MIN_MAX_NIGHT_RESTRICTIONS);
    final LosRestriction restrictionToApply = losRestrictionSvc.evaluateRestrictionRules(hoteRate, searchCriteria);
    log.info("Restriction to apply:{}", restrictionToApply);
    assertThat(restrictionToApply).isNotNull();
    assertThat(restrictionToApply.getLosRule()).isEqualTo(LosRestrictionName.MIN_MAX_NIGHT_RESTRICTIONS);
  }

  @Test
  void testEvaluateRestrictionRulesForNoRestrictions() {
    hoteRate.setMinNights(0);
    hoteRate.setMaxNights(0);
    when(losRule.isApplicable(Mockito.any(RatePlan.class), Mockito.any(SearchCriteria.class))).thenReturn(true);
    when(losRule.getRuleName()).thenReturn(LosRestrictionName.NO_RESTRICTIONS);
    final LosRestriction restrictionToApply = losRestrictionSvc.evaluateRestrictionRules(hoteRate, searchCriteria);
    log.info("Restriction to apply:{}", restrictionToApply);
    assertThat(restrictionToApply).isNotNull();
    assertThat(restrictionToApply.getLosRule()).isEqualTo(LosRestrictionName.NO_RESTRICTIONS);
  }

  @Test
  void testEvaluateRestrictionRulesForNoRestrictionsIfNoRulesAreRegistered() {
    hoteRate.setMinNights(0);
    hoteRate.setMaxNights(0);
    LosRestrictionRulesEvaluator.clearRules();
    final LosRestriction restrictionToApply = losRestrictionSvc.evaluateRestrictionRules(hoteRate, searchCriteria);
    log.info("Restriction to apply:{}", restrictionToApply);
    assertThat(restrictionToApply).isNotNull();
    assertThat(restrictionToApply.getLosRule()).isEqualTo(LosRestrictionName.NO_RESTRICTIONS);
  }

  private SearchCriteria buildSearchCriteria(final int numberOfNightStays) {
    final LocalDate now = LocalDate.now();
    String arrival = LocalDate.parse(LocalDate.now().toString(), DateTimeFormatter.ISO_LOCAL_DATE).toString();
    String departure = LocalDate.parse(LocalDate.now().plusDays(numberOfNightStays).toString(),
        DateTimeFormatter.ISO_LOCAL_DATE).toString();
    log.info("Arrival:{}, Departure:{}", arrival, departure);
    return SearchCriteria.builder().arrival(arrival).departure(departure).build();
  }
}
