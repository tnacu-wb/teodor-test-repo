package uk.co.whitbread.rules.agent.infrastructure.config;

import io.micrometer.tracing.Tracer;
import jakarta.validation.Validator;
import java.util.List;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import uk.co.whitbread.rules.agent.domain.logic.AmendmentRuleInPortImpl;
import uk.co.whitbread.rules.agent.domain.logic.BaseRateRuleInPortImpl;
import uk.co.whitbread.rules.agent.domain.logic.BusinessAllowanceRuleInPortImpl;
import uk.co.whitbread.rules.agent.domain.logic.ChannelRuleInPortImpl;
import uk.co.whitbread.rules.agent.domain.logic.MaxLimitationsRuleInPortImpl;
import uk.co.whitbread.rules.agent.domain.logic.OccupancySupplementInPortImpl;
import uk.co.whitbread.rules.agent.domain.logic.PaypalRuleInPortImpl;
import uk.co.whitbread.rules.agent.domain.logic.RateSuppressionRuleInPortImpl;
import uk.co.whitbread.rules.agent.domain.logic.RbacRuleInPortImpl;
import uk.co.whitbread.rules.agent.domain.logic.RoomSubstitutionRuleInPortImpl;
import uk.co.whitbread.rules.agent.domain.logic.RuleEngineCacheManagerInPortImpl;
import uk.co.whitbread.rules.agent.domain.logic.VatRuleInPortImpl;
import uk.co.whitbread.rules.agent.domain.model.out.Rule;
import uk.co.whitbread.rules.agent.domain.model.validation.ValidatorFactory;
import uk.co.whitbread.rules.agent.domain.ports.primary.AmendmentRuleInPort;
import uk.co.whitbread.rules.agent.domain.ports.primary.BaseRateRuleInPort;
import uk.co.whitbread.rules.agent.domain.ports.primary.BusinessAllowanceRuleInPort;
import uk.co.whitbread.rules.agent.domain.ports.primary.ChannelRuleInPort;
import uk.co.whitbread.rules.agent.domain.ports.primary.MaxLimitationsRuleInPort;
import uk.co.whitbread.rules.agent.domain.ports.primary.OccupancySupplementInPort;
import uk.co.whitbread.rules.agent.domain.ports.primary.PaypalRuleInPort;
import uk.co.whitbread.rules.agent.domain.ports.primary.RateSuppressionRuleInPort;
import uk.co.whitbread.rules.agent.domain.ports.primary.RbacRuleInPort;
import uk.co.whitbread.rules.agent.domain.ports.primary.RoomSubstitutionRuleInPort;
import uk.co.whitbread.rules.agent.domain.ports.primary.RuleEngineCacheManagerInPort;
import uk.co.whitbread.rules.agent.domain.ports.primary.VatRuleInPort;
import uk.co.whitbread.rules.agent.domain.ports.secondary.AmendmentRuleRepositoryOutPort;
import uk.co.whitbread.rules.agent.domain.ports.secondary.BaseRateRuleRepositoryOutPort;
import uk.co.whitbread.rules.agent.domain.ports.secondary.BusinessAllowanceRuleRepositoryOutPort;
import uk.co.whitbread.rules.agent.domain.ports.secondary.ChannelRuleRepositoryOutPort;
import uk.co.whitbread.rules.agent.domain.ports.secondary.MaxArrivalDateRuleRepositoryOutPort;
import uk.co.whitbread.rules.agent.domain.ports.secondary.MaxNightsRuleRepositoryOutPort;
import uk.co.whitbread.rules.agent.domain.ports.secondary.MaxRoomsRuleRepositoryOutPort;
import uk.co.whitbread.rules.agent.domain.ports.secondary.OccupancySupplementRepositoryOutPort;
import uk.co.whitbread.rules.agent.domain.ports.secondary.PaypalRuleRepositoryOutPort;
import uk.co.whitbread.rules.agent.domain.ports.secondary.RateSuppressionRuleRepositoryOutPort;
import uk.co.whitbread.rules.agent.domain.ports.secondary.RbacRuleRepositoryOutPort;
import uk.co.whitbread.rules.agent.domain.ports.secondary.RoomOccRuleRepoOutPort;
import uk.co.whitbread.rules.agent.domain.ports.secondary.RoomSubstitutionRuleRepositoryOutPort;
import uk.co.whitbread.rules.agent.domain.ports.secondary.RuleEngineRepositoryOutPort;
import uk.co.whitbread.rules.agent.domain.ports.secondary.VatRuleRepositoryOutPort;
import uk.co.whitbread.rules.agent.infrastructure.repository.AmendmentRepository;
import uk.co.whitbread.rules.agent.infrastructure.repository.AmendmentRepositoryCacheOutPortImpl;
import uk.co.whitbread.rules.agent.infrastructure.repository.BaseRateCacheRepository;
import uk.co.whitbread.rules.agent.infrastructure.repository.BaseRateRepositoryCacheOutPortImpl;
import uk.co.whitbread.rules.agent.infrastructure.repository.BusinessAllowanceCacheOutPortImpl;
import uk.co.whitbread.rules.agent.infrastructure.repository.BusinessAllowanceRepository;
import uk.co.whitbread.rules.agent.infrastructure.repository.ChannelRepository;
import uk.co.whitbread.rules.agent.infrastructure.repository.ChannelRepositoryCacheOutPortImpl;
import uk.co.whitbread.rules.agent.infrastructure.repository.MaxArrivalDateCacheRepository;
import uk.co.whitbread.rules.agent.infrastructure.repository.MaxArrivalDateRepositoryCacheOutPortImpl;
import uk.co.whitbread.rules.agent.infrastructure.repository.MaxNightsCacheRepository;
import uk.co.whitbread.rules.agent.infrastructure.repository.MaxNightsRepositoryCacheOutPortImpl;
import uk.co.whitbread.rules.agent.infrastructure.repository.MaxRoomOccupancyCacheRepository;
import uk.co.whitbread.rules.agent.infrastructure.repository.MaxRoomOccupancyRuleRepoOutPortImpl;
import uk.co.whitbread.rules.agent.infrastructure.repository.MaxRoomsCacheRepository;
import uk.co.whitbread.rules.agent.infrastructure.repository.MaxRoomsRepositoryCacheOutPortImpl;
import uk.co.whitbread.rules.agent.infrastructure.repository.OccupancySupplementOutPortImpl;
import uk.co.whitbread.rules.agent.infrastructure.repository.OccupancySupplementRepository;
import uk.co.whitbread.rules.agent.infrastructure.repository.PaypalCacheRepository;
import uk.co.whitbread.rules.agent.infrastructure.repository.PaypalRepositoryCacheOutPortImpl;
import uk.co.whitbread.rules.agent.infrastructure.repository.RateSuppressionCacheRepository;
import uk.co.whitbread.rules.agent.infrastructure.repository.RateSuppressionRepositoryCacheOutPortImpl;
import uk.co.whitbread.rules.agent.infrastructure.repository.RbacCacheRepository;
import uk.co.whitbread.rules.agent.infrastructure.repository.RbacRepositoryCacheOutPortImpl;
import uk.co.whitbread.rules.agent.infrastructure.repository.RoomSubstitutionCacheRepository;
import uk.co.whitbread.rules.agent.infrastructure.repository.RoomSubstitutionRepositoryCacheOutPortImpl;
import uk.co.whitbread.rules.agent.infrastructure.repository.VatCacheRepository;
import uk.co.whitbread.rules.agent.infrastructure.repository.VatRepositoryCacheOutPortImpl;
import uk.co.whitbread.rules.agent.infrastructure.repository.mapper.AmendmentRuleEntityMapper;
import uk.co.whitbread.rules.agent.infrastructure.repository.mapper.BaseRateRuleEntityMapper;
import uk.co.whitbread.rules.agent.infrastructure.repository.mapper.BusinessAllowanceRuleEntityMapper;
import uk.co.whitbread.rules.agent.infrastructure.repository.mapper.ChannelRuleEntityMapper;
import uk.co.whitbread.rules.agent.infrastructure.repository.mapper.MaxArrivalDateRuleEntityMapper;
import uk.co.whitbread.rules.agent.infrastructure.repository.mapper.MaxNightsRuleEntityMapper;
import uk.co.whitbread.rules.agent.infrastructure.repository.mapper.MaxRoomOccupancyRuleEntityMapper;
import uk.co.whitbread.rules.agent.infrastructure.repository.mapper.MaxRoomsRuleEntityMapper;
import uk.co.whitbread.rules.agent.infrastructure.repository.mapper.OccupancySupplementEntityMapper;
import uk.co.whitbread.rules.agent.infrastructure.repository.mapper.PaypalRuleEntityMapper;
import uk.co.whitbread.rules.agent.infrastructure.repository.mapper.RateSuppressionRuleEntityMapper;
import uk.co.whitbread.rules.agent.infrastructure.repository.mapper.RbacRuleEntityMapper;
import uk.co.whitbread.rules.agent.infrastructure.repository.mapper.RoomSubstitutionRuleEntityMapper;
import uk.co.whitbread.rules.agent.infrastructure.repository.mapper.VatRuleEntityMapper;

@Configuration
public class InfrastructureBeanConfig {

  @Bean
  public ValidatorFactory validatorFactory(Validator validator) {
    return ValidatorFactory.getInstance(validator);
  }

  @Bean
  public OccupancySupplementInPort occupancySupplementInPort(
      OccupancySupplementRepositoryOutPort occupancySupplementRepositoryOutPort) {
    return new OccupancySupplementInPortImpl(occupancySupplementRepositoryOutPort);
  }

  @Bean
  public AmendmentRuleInPort amendmentRuleInPort(
      AmendmentRuleRepositoryOutPort amendmentRuleRepositoryOutPort) {
    return new AmendmentRuleInPortImpl(amendmentRuleRepositoryOutPort);
  }

  @Bean
  public PaypalRuleInPort paypalRuleInPort(
      PaypalRuleRepositoryOutPort paypalRuleRepositoryOutPort) {
    return new PaypalRuleInPortImpl(paypalRuleRepositoryOutPort);
  }

  @Bean
  public BusinessAllowanceRuleInPort businessAllowanceRuleInPort(
      BusinessAllowanceRuleRepositoryOutPort businessAllowanceRuleRepositoryOutPort) {
    return new BusinessAllowanceRuleInPortImpl(businessAllowanceRuleRepositoryOutPort);
  }

  @Bean
  public MaxLimitationsRuleInPort maxLimitationsRuleInPort(
      MaxNightsRuleRepositoryOutPort maxNightsRuleRepositoryOutPort,
      MaxRoomsRuleRepositoryOutPort maxRoomsRuleRepositoryOutPort,
      RoomOccRuleRepoOutPort roomOccupancyRuleRepoOutPort,
      MaxArrivalDateRuleRepositoryOutPort maxArrivalDateRuleRepositoryOutPort) {
    return new MaxLimitationsRuleInPortImpl(maxNightsRuleRepositoryOutPort,
        maxRoomsRuleRepositoryOutPort, roomOccupancyRuleRepoOutPort,
        maxArrivalDateRuleRepositoryOutPort);
  }

  @Bean
  public RbacRuleInPort rbacRuleInPort(RbacRuleRepositoryOutPort rbacRuleOutPort) {
    return new RbacRuleInPortImpl(rbacRuleOutPort);
  }

  @Bean
  public RoomSubstitutionRuleInPort roomSubstitutionRuleInPort(
      RoomSubstitutionRuleRepositoryOutPort roomSubstitutionRuleRepositoryOutPort) {
    return new RoomSubstitutionRuleInPortImpl(roomSubstitutionRuleRepositoryOutPort);
  }

  @Bean
  public VatRuleInPort vatRuleInPort(VatRuleRepositoryOutPort vatRuleRepositoryOutPort) {
    return new VatRuleInPortImpl(vatRuleRepositoryOutPort);
  }

  @Bean
  public RateSuppressionRuleInPort rateSuppressionRuleInPort(
      RateSuppressionRuleRepositoryOutPort rateSuppressionRuleRepositoryOutPort) {
    return new RateSuppressionRuleInPortImpl(rateSuppressionRuleRepositoryOutPort);
  }

  @Bean
  public OccupancySupplementRepositoryOutPort occupancySupplementRepositoryOutPort(
      OccupancySupplementRepository occupancySupplementRepository,
      OccupancySupplementEntityMapper occupancySupplementEntityMapper) {
    return new OccupancySupplementOutPortImpl(occupancySupplementRepository,
        occupancySupplementEntityMapper);
  }

  @Bean
  public RateSuppressionRuleRepositoryOutPort rateSuppressionRuleOutPort(
      RateSuppressionCacheRepository rateSuppressionCacheRepository,
      RateSuppressionRuleEntityMapper rateSuppressionRuleEntityMapper
  ) {
    return new RateSuppressionRepositoryCacheOutPortImpl(rateSuppressionCacheRepository,
        rateSuppressionRuleEntityMapper);
  }

  @Bean
  public AmendmentRuleRepositoryOutPort amendmentRepositoryCacheOutPort(
      AmendmentRepository amendmentRepository,
      AmendmentRuleEntityMapper amendmentRuleEntityMapper) {
    return new AmendmentRepositoryCacheOutPortImpl(amendmentRepository, amendmentRuleEntityMapper);
  }

  @Bean
  public BusinessAllowanceRuleRepositoryOutPort businessAllowanceRuleRepositoryOutPort(
      BusinessAllowanceRepository businessAllowanceRepository,
      BusinessAllowanceRuleEntityMapper businessAllowanceRuleEntityMapper
  ) {
    return new BusinessAllowanceCacheOutPortImpl(businessAllowanceRepository,
        businessAllowanceRuleEntityMapper);
  }

  @Bean
  public PaypalRuleRepositoryOutPort paypalRuleRepositoryOutPort(
      PaypalCacheRepository paypalCacheRepository,
      PaypalRuleEntityMapper paypalRuleEntityMapper
  ) {
    return new PaypalRepositoryCacheOutPortImpl(paypalCacheRepository
        );
  }

  @Bean
  public MaxArrivalDateRuleRepositoryOutPort maxArrivalDateRuleRepositoryOutPort(
      MaxArrivalDateCacheRepository maxArrivalDateCacheRepository,
      MaxArrivalDateRuleEntityMapper maxArrivalDateRuleEntityMapper
  ) {
    return new MaxArrivalDateRepositoryCacheOutPortImpl(maxArrivalDateCacheRepository,
        maxArrivalDateRuleEntityMapper);
  }

  @Bean
  public MaxNightsRuleRepositoryOutPort maxNightsRuleRepositoryOutPort(
      MaxNightsCacheRepository maxNightsCacheRepository,
      MaxNightsRuleEntityMapper maxNightsRuleEntityMapper
  ) {
    return new MaxNightsRepositoryCacheOutPortImpl(maxNightsCacheRepository,
        maxNightsRuleEntityMapper);
  }

  @Bean
  public MaxRoomsRuleRepositoryOutPort maxRoomsRuleRepositoryOutPort(
      MaxRoomsCacheRepository maxRoomsCacheRepository,
      MaxRoomsRuleEntityMapper maxRoomsRuleEntityMapper
  ) {
    return new MaxRoomsRepositoryCacheOutPortImpl(maxRoomsCacheRepository,
        maxRoomsRuleEntityMapper);
  }

  @Bean
  public RbacRuleRepositoryOutPort rbacRepositoryCacheOutPort(
      RbacCacheRepository rbacCacheRepository, RbacRuleEntityMapper rbacRuleEntityMapper) {
    return new RbacRepositoryCacheOutPortImpl(rbacCacheRepository, rbacRuleEntityMapper);
  }

  @Bean
  public RoomOccRuleRepoOutPort maxRoomOccupancyRuleRepoOutPort(
      MaxRoomOccupancyCacheRepository maxRoomOccupancyCacheRepository,
      MaxRoomOccupancyRuleEntityMapper maxRoomOccupancyRuleEntityMapper
  ) {
    return new MaxRoomOccupancyRuleRepoOutPortImpl(maxRoomOccupancyCacheRepository,
        maxRoomOccupancyRuleEntityMapper);
  }

  @Bean
  public RoomSubstitutionRuleRepositoryOutPort roomSubstitutionRepositoryCacheOutPort(
      RoomSubstitutionCacheRepository roomSubstitutionCacheRepository,
      RoomSubstitutionRuleEntityMapper roomSubstitutionRuleEntityMapper) {
    return new RoomSubstitutionRepositoryCacheOutPortImpl(roomSubstitutionCacheRepository,
        roomSubstitutionRuleEntityMapper);
  }

  @Bean
  public VatRuleRepositoryOutPort vatRepositoryCacheOutPort(
      VatCacheRepository vatCacheRepository,
      VatRuleEntityMapper vatRuleEntityMapper) {
    return new VatRepositoryCacheOutPortImpl(vatCacheRepository,
        vatRuleEntityMapper);
  }

  @Bean
  public ChannelRuleInPort channelRuleInPort(
      ChannelRuleRepositoryOutPort channelRuleRepositoryOutPort) {
    return new ChannelRuleInPortImpl(channelRuleRepositoryOutPort);
  }

  @Bean
  public ChannelRuleRepositoryOutPort channelRuleRepositoryOutPort(
      ChannelRepository channelRepository,
      ChannelRuleEntityMapper channelRuleEntityMapper
  ) {
    return new ChannelRepositoryCacheOutPortImpl(channelRepository,
        channelRuleEntityMapper);
  }
  
  @Bean
  public BaseRateRuleInPort baseRateRuleInPort(
      BaseRateRuleRepositoryOutPort baseRateRuleRepositoryOutPort) {
    return new BaseRateRuleInPortImpl(baseRateRuleRepositoryOutPort);
  }
  
  @Bean
  public BaseRateRuleRepositoryOutPort baseRateRepositoryCacheOutPort(
      BaseRateCacheRepository baseRateCacheRepository,
      BaseRateRuleEntityMapper baseRateRuleEntityMapper) {
    return new BaseRateRepositoryCacheOutPortImpl(baseRateCacheRepository, baseRateRuleEntityMapper);
  }

  @Bean
  public RuleEngineCacheManagerInPort ruleEngineSweeperInPort(
      List<? extends RuleEngineRepositoryOutPort<? extends Rule>> repos) {
    return new RuleEngineCacheManagerInPortImpl(repos);
  }

  @Bean
  @ConditionalOnMissingBean(Tracer.class)
  public Tracer tracer() {
    return Tracer.NOOP;
  }

}
