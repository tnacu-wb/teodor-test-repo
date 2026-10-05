package uk.co.whitbread.rules.manager.infrastructure.config;

import jakarta.validation.Validator;
import java.util.List;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import uk.co.whitbread.rules.manager.domain.logic.scheduler.RecordProcessorPipeline;
import uk.co.whitbread.rules.manager.domain.logic.scheduler.RuleEngineSweeperInPortImpl;
import uk.co.whitbread.rules.manager.domain.model.in.AmendmentRule;
import uk.co.whitbread.rules.manager.domain.model.in.BaseRateRule;
import uk.co.whitbread.rules.manager.domain.model.in.BusinessAllowanceRule;
import uk.co.whitbread.rules.manager.domain.model.in.ChannelRule;
import uk.co.whitbread.rules.manager.domain.model.in.MaxArrivalDateRule;
import uk.co.whitbread.rules.manager.domain.model.in.MaxNightsRule;
import uk.co.whitbread.rules.manager.domain.model.in.MaxRoomOccupancyRule;
import uk.co.whitbread.rules.manager.domain.model.in.MaxRoomsRule;
import uk.co.whitbread.rules.manager.domain.model.in.PaypalRule;
import uk.co.whitbread.rules.manager.domain.model.in.RateSuppressionRule;
import uk.co.whitbread.rules.manager.domain.model.in.RbacRule;
import uk.co.whitbread.rules.manager.domain.model.in.RoomSubstitutionRule;
import uk.co.whitbread.rules.manager.domain.model.in.Rule;
import uk.co.whitbread.rules.manager.domain.model.in.VatRule;
import uk.co.whitbread.rules.manager.domain.model.validation.ValidatorFactory;
import uk.co.whitbread.rules.manager.domain.ports.primary.RuleEngineSweeperInPort;
import uk.co.whitbread.rules.manager.domain.ports.secondary.RuleEngineRepositoryOutPort;
import uk.co.whitbread.rules.manager.infrastructure.repository.AmendmentRepository;
import uk.co.whitbread.rules.manager.infrastructure.repository.AmendmentRepositoryOutPortImpl;
import uk.co.whitbread.rules.manager.infrastructure.repository.BaseRateRepository;
import uk.co.whitbread.rules.manager.infrastructure.repository.BaseRateRepositoryOutPortImpl;
import uk.co.whitbread.rules.manager.infrastructure.repository.BusinessAllowanceRuleRepository;
import uk.co.whitbread.rules.manager.infrastructure.repository.BusinessAllowanceRuleRepositoryOutPortImpl;
import uk.co.whitbread.rules.manager.infrastructure.repository.ChannelRepository;
import uk.co.whitbread.rules.manager.infrastructure.repository.ChannelRepositoryOutPortImpl;
import uk.co.whitbread.rules.manager.infrastructure.repository.MaxArrivalDateRepository;
import uk.co.whitbread.rules.manager.infrastructure.repository.MaxArrivalDateRepositoryOutPortImpl;
import uk.co.whitbread.rules.manager.infrastructure.repository.MaxNightsRepository;
import uk.co.whitbread.rules.manager.infrastructure.repository.MaxNightsRepositoryOutPortImpl;
import uk.co.whitbread.rules.manager.infrastructure.repository.MaxRoomOccupancyRepository;
import uk.co.whitbread.rules.manager.infrastructure.repository.MaxRoomOccupancyRepositoryOutPortImpl;
import uk.co.whitbread.rules.manager.infrastructure.repository.MaxRoomsRepository;
import uk.co.whitbread.rules.manager.infrastructure.repository.MaxRoomsRepositoryOutPortImpl;
import uk.co.whitbread.rules.manager.infrastructure.repository.PaypalRepository;
import uk.co.whitbread.rules.manager.infrastructure.repository.PaypalRepositoryOutPortImpl;
import uk.co.whitbread.rules.manager.infrastructure.repository.RateSuppressionRepository;
import uk.co.whitbread.rules.manager.infrastructure.repository.RateSuppressionRepositoryOutPortImpl;
import uk.co.whitbread.rules.manager.infrastructure.repository.RbacRuleRepository;
import uk.co.whitbread.rules.manager.infrastructure.repository.RbacRuleRepositoryOutPortImpl;
import uk.co.whitbread.rules.manager.infrastructure.repository.RoomSubstitutionRepository;
import uk.co.whitbread.rules.manager.infrastructure.repository.RoomSubstitutionRepositoryOutPortImpl;
import uk.co.whitbread.rules.manager.infrastructure.repository.VatRuleRepository;
import uk.co.whitbread.rules.manager.infrastructure.repository.VatRuleRepositoryOutPortImpl;
import uk.co.whitbread.rules.manager.infrastructure.repository.mapper.AmendmentRuleMapper;
import uk.co.whitbread.rules.manager.infrastructure.repository.mapper.BaseRateRuleMapper;
import uk.co.whitbread.rules.manager.infrastructure.repository.mapper.BusinessAllowanceRuleMapper;
import uk.co.whitbread.rules.manager.infrastructure.repository.mapper.ChannelRuleMapper;
import uk.co.whitbread.rules.manager.infrastructure.repository.mapper.MaxArrivalDateMapper;
import uk.co.whitbread.rules.manager.infrastructure.repository.mapper.MaxNightsRuleMapper;
import uk.co.whitbread.rules.manager.infrastructure.repository.mapper.MaxRoomOccupancyRuleMapper;
import uk.co.whitbread.rules.manager.infrastructure.repository.mapper.MaxRoomsRuleMapper;
import uk.co.whitbread.rules.manager.infrastructure.repository.mapper.PaypalRuleMapper;
import uk.co.whitbread.rules.manager.infrastructure.repository.mapper.RateSuppressionRuleMapper;
import uk.co.whitbread.rules.manager.infrastructure.repository.mapper.RbacRuleMapper;
import uk.co.whitbread.rules.manager.infrastructure.repository.mapper.RoomSubstitutionRuleMapper;
import uk.co.whitbread.rules.manager.infrastructure.repository.mapper.VatRuleMapper;

@Configuration
public class InfrastructureBeanConfig {

  @Bean
  public ValidatorFactory validatorFactory(Validator validator) {
    return ValidatorFactory.getInstance(validator);
  }

  @Bean
  public RuleEngineSweeperInPort ruleEngineSweeperInPort(
      List<? extends RuleEngineRepositoryOutPort<? extends Rule>> repos) {
    return new RuleEngineSweeperInPortImpl(repos, processorPipeline());
  }

  @Bean
  public RuleEngineRepositoryOutPort<AmendmentRule> amendmentRepositoryOutPort(
      AmendmentRepository amendmentRepository, AmendmentRuleMapper amendmentRuleMapper) {
    return new AmendmentRepositoryOutPortImpl(amendmentRepository, amendmentRuleMapper);
  }

  @Bean
  public RuleEngineRepositoryOutPort<RbacRule> rbacRepositoryOutPort(
      RbacRuleRepository rbacRuleRepository, RbacRuleMapper rbacRuleMapper) {
    return new RbacRuleRepositoryOutPortImpl(rbacRuleRepository, rbacRuleMapper);
  }

  @Bean
  public RuleEngineRepositoryOutPort<MaxNightsRule> maxNightsRepositoryOutPort(
      MaxNightsRepository maxNightsRepository, MaxNightsRuleMapper maxNightsRuleMapper) {
    return new MaxNightsRepositoryOutPortImpl(maxNightsRepository, maxNightsRuleMapper);
  }

  @Bean
  public RuleEngineRepositoryOutPort<MaxRoomsRule> maxRoomsRepositoryOutPort(
      MaxRoomsRepository maxRoomsRepository, MaxRoomsRuleMapper maxRoomsRuleMapper) {
    return new MaxRoomsRepositoryOutPortImpl(maxRoomsRepository, maxRoomsRuleMapper);
  }

  @Bean
  public RuleEngineRepositoryOutPort<MaxArrivalDateRule> maxArrivalDateRepositoryOutPort(
      MaxArrivalDateRepository maxArrivalDateRepository,
      MaxArrivalDateMapper maxArrivalDateMapper) {

    return new MaxArrivalDateRepositoryOutPortImpl(maxArrivalDateRepository, maxArrivalDateMapper);
  }

  @Bean
  public RuleEngineRepositoryOutPort<MaxRoomOccupancyRule> maxRoomsOccupancyRepositoryOutPort(
      MaxRoomOccupancyRepository roomOccupancyRepository,
      MaxRoomOccupancyRuleMapper roomOccupancyMapper) {
    return new MaxRoomOccupancyRepositoryOutPortImpl(roomOccupancyRepository, roomOccupancyMapper);
  }

  @Bean
  public RuleEngineRepositoryOutPort<RoomSubstitutionRule> roomSubstitutionRepositoryOutPort(
      RoomSubstitutionRepository roomSubstitutionRepository,
      RoomSubstitutionRuleMapper roomSubstitutionRuleMapper) {
    return new RoomSubstitutionRepositoryOutPortImpl(roomSubstitutionRepository,
        roomSubstitutionRuleMapper);
  }

  @Bean
  public RuleEngineRepositoryOutPort<VatRule> vatRepositoryOutPort(
      VatRuleRepository vatRuleRepository, VatRuleMapper vatRuleMapper) {
    return new VatRuleRepositoryOutPortImpl(vatRuleRepository,
        vatRuleMapper);
  }

  @Bean
  public RuleEngineRepositoryOutPort<BusinessAllowanceRule> businessAllowanceRepositoryOutPort(
      BusinessAllowanceRuleRepository businessAllowanceRuleRepository,
      BusinessAllowanceRuleMapper businessAllowanceRuleMapper) {
    return new BusinessAllowanceRuleRepositoryOutPortImpl(businessAllowanceRuleRepository,
        businessAllowanceRuleMapper);
  }

  @Bean
  public RuleEngineRepositoryOutPort<RateSuppressionRule> suppressionRepositoryOutPort(
      RateSuppressionRepository rateSuppressionRepository,
      RateSuppressionRuleMapper rateSuppressionRuleMapper) {
    return new RateSuppressionRepositoryOutPortImpl(rateSuppressionRepository,
        rateSuppressionRuleMapper);
  }

  @Bean
  public RuleEngineRepositoryOutPort<ChannelRule> channelRepositoryOutPort(
      ChannelRepository channelRepository, ChannelRuleMapper channelRuleMapper) {
    return new ChannelRepositoryOutPortImpl(channelRepository,
        channelRuleMapper);
  }

  @Bean
  public RecordProcessorPipeline processorPipeline() {
    return new RecordProcessorPipeline();
  }

  @Bean
  public RuleEngineRepositoryOutPort<PaypalRule> paypalRepositoryOutPort(
      PaypalRepository paypalRepository, PaypalRuleMapper paypalRuleMapper) {
    return new PaypalRepositoryOutPortImpl(paypalRepository, paypalRuleMapper);
  }
  
  @Bean RuleEngineRepositoryOutPort<BaseRateRule> baseRateRepositoryOutPort(
      BaseRateRepository baseRateRepository, BaseRateRuleMapper baseRateRuleMapper) {
    return new BaseRateRepositoryOutPortImpl(baseRateRepository, baseRateRuleMapper);
  }
}
