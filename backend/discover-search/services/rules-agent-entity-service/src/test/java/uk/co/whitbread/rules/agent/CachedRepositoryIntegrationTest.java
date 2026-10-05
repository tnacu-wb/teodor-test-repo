package uk.co.whitbread.rules.agent;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasSize;

import java.util.List;
import javax.sql.DataSource;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.datasource.init.DatabasePopulator;
import org.springframework.jdbc.datasource.init.DatabasePopulatorUtils;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import uk.co.whitbread.rules.agent.domain.model.in.ChannelRuleRequest;
import uk.co.whitbread.rules.agent.domain.model.in.RoomSubstitutionRuleRequest;
import uk.co.whitbread.rules.agent.domain.model.in.VatRuleRequest;
import uk.co.whitbread.rules.agent.domain.ports.primary.RuleEngineCacheManagerInPort;
import uk.co.whitbread.rules.agent.domain.ports.secondary.AmendmentRuleRepositoryOutPort;
import uk.co.whitbread.rules.agent.domain.ports.secondary.ChannelRuleRepositoryOutPort;
import uk.co.whitbread.rules.agent.domain.ports.secondary.MaxArrivalDateRuleRepositoryOutPort;
import uk.co.whitbread.rules.agent.domain.ports.secondary.MaxNightsRuleRepositoryOutPort;
import uk.co.whitbread.rules.agent.domain.ports.secondary.MaxRoomsRuleRepositoryOutPort;
import uk.co.whitbread.rules.agent.domain.ports.secondary.RateSuppressionRuleRepositoryOutPort;
import uk.co.whitbread.rules.agent.domain.ports.secondary.RbacRuleRepositoryOutPort;
import uk.co.whitbread.rules.agent.domain.ports.secondary.RoomOccRuleRepoOutPort;
import uk.co.whitbread.rules.agent.domain.ports.secondary.RoomSubstitutionRuleRepositoryOutPort;
import uk.co.whitbread.rules.agent.domain.ports.secondary.VatRuleRepositoryOutPort;
import uk.co.whitbread.rules.agent.infrastructure.repository.AmendmentRepository;
import uk.co.whitbread.rules.agent.infrastructure.repository.ChannelRepository;
import uk.co.whitbread.rules.agent.infrastructure.repository.MaxArrivalDateCacheRepository;
import uk.co.whitbread.rules.agent.infrastructure.repository.MaxNightsCacheRepository;
import uk.co.whitbread.rules.agent.infrastructure.repository.MaxRoomOccupancyCacheRepository;
import uk.co.whitbread.rules.agent.infrastructure.repository.MaxRoomsCacheRepository;
import uk.co.whitbread.rules.agent.infrastructure.repository.RateSuppressionCacheRepository;
import uk.co.whitbread.rules.agent.infrastructure.repository.RbacCacheRepository;
import uk.co.whitbread.rules.agent.infrastructure.repository.RoomSubstitutionCacheRepository;
import uk.co.whitbread.rules.agent.infrastructure.repository.VatCacheRepository;
import uk.co.whitbread.rules.agent.infrastructure.rest.controller.model.in.RbacRuleHasAccessRequestDto;

@SpringBootTest(classes = RulesAgentServiceApplication.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Slf4j
class CachedRepositoryIntegrationTest {

  @Autowired
  private MaxNightsCacheRepository maxNightsCacheRepository;
  @Autowired
  private MaxNightsRuleRepositoryOutPort maxNightsRuleRepositoryOutPort;

  @Autowired
  private AmendmentRepository amendmentRepository;
  @Autowired
  private AmendmentRuleRepositoryOutPort amendmentRuleRepositoryOutPort;

  @Autowired
  private MaxArrivalDateCacheRepository maxArrivalDateRepository;
  @Autowired
  private MaxArrivalDateRuleRepositoryOutPort maxArrivalDateRuleRepositoryOutPort;

  @Autowired
  private MaxRoomOccupancyCacheRepository maxRoomOccupancyRepository;
  @Autowired
  private RoomOccRuleRepoOutPort maxRoomOccupancyRuleRepositoryOutPort;

  @Autowired
  private MaxRoomsCacheRepository maxRoomsRepository;
  @Autowired
  private MaxRoomsRuleRepositoryOutPort maxRoomsRuleRepositoryOutPort;

  @Autowired
  private RbacCacheRepository rbacRepository;
  @Autowired
  private RbacRuleRepositoryOutPort rbacRuleRepositoryOutPort;

  @Autowired
  private RoomSubstitutionCacheRepository roomSubstitutionCacheRepository;
  @Autowired
  private RoomSubstitutionRuleRepositoryOutPort roomSubstitutionRuleRepositoryOutPort;

  @Autowired
  private VatCacheRepository vatCacheRepository;
  @Autowired
  private VatRuleRepositoryOutPort vatRuleRepositoryOutPort;

  @Autowired
  private RateSuppressionCacheRepository rateSuppressionCacheRepository;

  @Autowired
  private RateSuppressionRuleRepositoryOutPort rateSuppressionRuleRepositoryOutPort;

  @Autowired
  private ChannelRepository channelRepository;

  @Autowired
  private ChannelRuleRepositoryOutPort channelRuleRepositoryOutPort;

  @Autowired
  private DataSource dataSource;

  @Autowired
  private RuleEngineCacheManagerInPort ruleManagerInPort;

  @BeforeAll
  void setUp() {
    Resource initData = new ClassPathResource("stubs/data.sql");
    DatabasePopulator dbPopulator = new ResourceDatabasePopulator(initData);
    DatabasePopulatorUtils.execute(dbPopulator, dataSource);

    ruleManagerInPort.loadRulesInMemory();
  }

  @Test
  void testMaxNightsRepo() {
    //Act
    var maxNightsRules = maxNightsCacheRepository.findAll();
    var activeMaxNightsRules = maxNightsCacheRepository.findAllByStatusActive();

    var cachedActiveMaxNightsRule = maxNightsRuleRepositoryOutPort.findRule("CCUI");

    //Assert
    assertThat(maxNightsRules).isNotNull();
    assertThat(maxNightsRules, hasSize(greaterThan(1)));

    assertThat(activeMaxNightsRules).isNotNull();
    assertThat(activeMaxNightsRules, hasSize(greaterThan(1)));

    assertThat(cachedActiveMaxNightsRule.get()).isNotNull();
    assertThat(cachedActiveMaxNightsRule.get().getMaxNights()).isEqualTo(364);
    assertThat(cachedActiveMaxNightsRule.get().getChannelId()).isEqualTo("CCUI");
  }

  @Test
  void testMaxArrivalDateRepo() {
    //Act
    var maxArrivalDateRules = maxArrivalDateRepository.findAll();
    var activeMaxArrivalDateRules = maxArrivalDateRepository.findAllByStatusActive();

    var cachedActiveMaxArrivalDateRule = maxArrivalDateRuleRepositoryOutPort.findRule("CCUI");

    //Assert
    assertThat(maxArrivalDateRules).isNotNull();
    assertThat(maxArrivalDateRules, hasSize(greaterThan(1)));

    assertThat(activeMaxArrivalDateRules).isNotNull();
    assertThat(activeMaxArrivalDateRules, hasSize(greaterThan(1)));

    assertThat(cachedActiveMaxArrivalDateRule.get()).isNotNull();
    assertThat(cachedActiveMaxArrivalDateRule.get().getMaxArrivalDate()).isEqualTo(364);
    assertThat(cachedActiveMaxArrivalDateRule.get().getChannelId()).isEqualTo("CCUI");
  }

  @Test
  void testMaxRoomOccupancyRepo() {
    //Act
    var maxRoomOccupancyRules = maxRoomOccupancyRepository.findAll();
    var activeRoomOccupancyRules = maxRoomOccupancyRepository.findAllByStatusActive();

    var cachedActiveMaxRoomOccupancyRules = maxRoomOccupancyRuleRepositoryOutPort.findRules("CCUI", "PI");

    //Assert
    assertThat(maxRoomOccupancyRules).isNotNull();
    assertThat(maxRoomOccupancyRules, hasSize(greaterThan(1)));

    assertThat(activeRoomOccupancyRules).isNotNull();
    assertThat(activeRoomOccupancyRules, hasSize(greaterThan(1)));

    assertThat(cachedActiveMaxRoomOccupancyRules).isNotNull();
    assertThat(cachedActiveMaxRoomOccupancyRules, hasSize(greaterThan(1)));
  }

  @Test
  void testMaxRoomsRepo() {
    //Act
    var maxRoomsRules = maxRoomsRepository.findAll();
    var activeRoomsRules = maxRoomsRepository.findAllByStatusActive();

    var cachedActiveMaxRoomsRules = maxRoomsRuleRepositoryOutPort.findRule("CCUI");

    //Assert
    assertThat(maxRoomsRules).isNotNull();
    assertThat(maxRoomsRules, hasSize(greaterThan(1)));

    assertThat(activeRoomsRules).isNotNull();
    assertThat(activeRoomsRules, hasSize(greaterThan(1)));

    assertThat(cachedActiveMaxRoomsRules.get()).isNotNull();
    assertThat(cachedActiveMaxRoomsRules.get().getMaxRooms()).isEqualTo(9);
    assertThat(cachedActiveMaxRoomsRules.get().getChannelId()).isEqualTo("CCUI");
  }

  @Test
  void testRbacRepo() {
    //Arrange
    var rbacRuleRequest = RbacRuleHasAccessRequestDto.builder()
        .resourceId("CC_Role01")
        .roleIdList(List.of("AGENT_ROLE"))
        .build();

    //Act
    var rbacRules = rbacRepository.findAll();
    var activeRbacRules = rbacRepository.findAllByStatusActive();

    var cachedActiveRbacRule = rbacRuleRepositoryOutPort.getRbacHasAccess(rbacRuleRequest.getRoleIdList(),rbacRuleRequest.getResourceId());

    //Assert
    assertThat(rbacRules).isNotNull();
    assertThat(rbacRules, hasSize(greaterThan(1)));

    assertThat(activeRbacRules).isNotNull();
    assertThat(activeRbacRules, hasSize(greaterThan(1)));

    assertThat(cachedActiveRbacRule.get()).isNotNull();
    assertThat(cachedActiveRbacRule.get()).isTrue();
  }

  @Test
  void testRoomSubstitutionRepo() {
    //Arrange
    var roomSubstitutionRuleRequest = RoomSubstitutionRuleRequest.builder()
        .adults(2)
        .children(0)
        .roomType("Double")
        .pms("OP")
        .channel("PI")
        .build();

    //Act
    var roomSubstitutionRules = roomSubstitutionCacheRepository.findAll();
    var activeRoomSubstitutionRules = roomSubstitutionCacheRepository.findAllByStatusActive();

    var cachedActiveRoomSubstitutionRule = roomSubstitutionRuleRepositoryOutPort.findRoomSubstitutionRules(
        roomSubstitutionRuleRequest);

    //Assert
    assertThat(roomSubstitutionRules).isNotNull();
    assertThat(roomSubstitutionRules, hasSize(greaterThan(1)));

    assertThat(activeRoomSubstitutionRules).isNotNull();
    assertThat(activeRoomSubstitutionRules, hasSize(greaterThan(1)));

    assertThat(cachedActiveRoomSubstitutionRule).isNotNull();
    assertThat(cachedActiveRoomSubstitutionRule, hasSize(greaterThan(1)));
  }

  @Test
  void testAmendmentRepo() {
    //Act
    var amendmentRules = amendmentRepository.findAll();
    var amendmentActiveRules = amendmentRepository.findAllByStatusActive();

    var cachedAmendmentActiveRule = amendmentRuleRepositoryOutPort.findRule("Flex", "GB");

    //Assert
    assertThat(amendmentRules).isNotNull();
    assertThat(amendmentRules, hasSize(greaterThan(0)));

    assertThat(amendmentActiveRules).isNotNull();
    assertThat(amendmentActiveRules, hasSize(greaterThan(0)));

    assertThat(cachedAmendmentActiveRule.get()).isNotNull();
    assertThat(cachedAmendmentActiveRule.get().getRateType()).isEqualTo("Flex");
  }

  @Test
  void testRateSuppressionRepo() {
    //Act
    var rateSuppressionRules = rateSuppressionCacheRepository.findAll();
    var rateSuppressionActiveRules = rateSuppressionCacheRepository.findAllByStatusActive();

    var cachedRateSuppressionsActiveRule = rateSuppressionRuleRepositoryOutPort.findRateSuppressionRule();

    //Assert
    assertThat(rateSuppressionRules).isNotNull();
    assertThat(rateSuppressionRules, hasSize(greaterThan(0)));

    assertThat(rateSuppressionActiveRules).isNotNull();
    assertThat(rateSuppressionActiveRules, hasSize(greaterThan(0)));

    assertThat(cachedRateSuppressionsActiveRule.get(0)).isNotNull();
    assertThat(cachedRateSuppressionsActiveRule.get(0).getRateType()).isEqualTo("FLEX");
    assertThat(cachedRateSuppressionsActiveRule.get(0).getPriority()).isEqualTo((short) 10);
  }

  @Test
  void testVatRepo() {
    //Arrange
    var vatRuleRequest = VatRuleRequest.builder()
        .vatRegion("UK")
        .pkgCodeArr(List.of("OBFGGO"))
        .build();

    //Act
    var vatRules = vatCacheRepository.findAll();
    var activeVatRules = vatCacheRepository.findAllByStatusActive();
    var cachedActiveVatRule = vatRuleRepositoryOutPort.findVatRules(
        vatRuleRequest);

    //Assert
    assertThat(vatRules).isNotNull();
    assertThat(vatRules, hasSize(greaterThan(1)));

    assertThat(activeVatRules).isNotNull();
    assertThat(activeVatRules, hasSize(greaterThan(1)));

    assertThat(cachedActiveVatRule).isNotNull();
    assertThat(cachedActiveVatRule, hasSize(greaterThan(1)));
  }

  @Test
  void testChannelRuleRepo() {
    //Act
    var request = ChannelRuleRequest.builder()
        .channel("PI")
        .subchannel("WEB")
        .language("EN")
        .pms("OP")
        .build();

    var channelRules = channelRepository.findAll();
    var channelActiveRules = channelRepository.findAllByStatusActive();

    var cachedChannelActiveRule = channelRuleRepositoryOutPort.findRule(request);

    //Assert
    assertThat(channelRules).isNotNull();
    assertThat(channelRules, hasSize(greaterThan(0)));

    assertThat(channelActiveRules).isNotNull();
    assertThat(channelActiveRules, hasSize(greaterThan(0)));

    assertThat(cachedChannelActiveRule.get()).isNotNull();
    assertThat(cachedChannelActiveRule.get().getChannel()).isEqualTo("PI");
    assertThat(cachedChannelActiveRule.get().getSubchannel()).isEqualTo("WEB");
    assertThat(cachedChannelActiveRule.get().getLanguage()).isEqualTo("EN");
    assertThat(cachedChannelActiveRule.get().getPms()).isEqualTo("OP");
  }
}
