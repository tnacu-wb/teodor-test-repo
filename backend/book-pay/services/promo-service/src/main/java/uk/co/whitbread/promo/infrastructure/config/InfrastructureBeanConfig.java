package uk.co.whitbread.promo.infrastructure.config;

import jakarta.validation.Validator;
import java.util.concurrent.Executor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import uk.co.whitbread.promo.domain.logic.PromoBatchInPortImpl;
import uk.co.whitbread.promo.domain.model.feature.FeatureFlag;
import uk.co.whitbread.promo.domain.model.feature.UnleashWrapper;
import uk.co.whitbread.promo.domain.model.validation.ValidatorFactory;
import uk.co.whitbread.promo.domain.ports.primary.PromoBatchInPort;
import uk.co.whitbread.promo.domain.ports.secondary.PromoBatchCodeGeneratorOutPort;
import uk.co.whitbread.promo.domain.ports.secondary.PromoBatchRepositoryOutPort;
import uk.co.whitbread.promo.infrastructure.repository.BatchEligibilityRepository;
import uk.co.whitbread.promo.infrastructure.repository.PromoBatchRepository;
import uk.co.whitbread.promo.infrastructure.repository.PromoBatchRepositoryOutPortImpl;
import uk.co.whitbread.promo.infrastructure.repository.PromoBatchS3UploaderService;
import uk.co.whitbread.promo.infrastructure.repository.PromoBatchStatusService;
import uk.co.whitbread.promo.infrastructure.repository.PromoCodeRepository;
import uk.co.whitbread.promo.infrastructure.repository.mapper.BatchEligibilityMapper;
import uk.co.whitbread.promo.infrastructure.repository.mapper.PromoBatchEntityMapper;
import uk.co.whitbread.promo.infrastructure.repository.mapper.PromoBatchSummaryMapper;
import uk.co.whitbread.promo.infrastructure.rest.client.ohip.OhipAdapterClient;
import uk.co.whitbread.promo.infrastructure.rest.client.properties.S3Properties;

@Configuration
public class InfrastructureBeanConfig {

  @Bean
  public ValidatorFactory validatorFactory(Validator validator) {
    return ValidatorFactory.getInstance(validator);
  }

  @Bean
  public PromoBatchRepositoryOutPort promoBatchRepositoryOutPort(
          PromoBatchRepository promoBatchRepository,
          PromoCodeRepository promoCodeRepository,
          PromoBatchEntityMapper promoBatchEntityMapper,
          PromoBatchSummaryMapper promoBatchSummaryMapper,
          PromoBatchStatusService promoBatchStatusService,
          PromoBatchCodeGeneratorOutPort promoBatchCodeGeneratorOutPort,
          OhipAdapterClient ohipAdapterClient,
          S3Presigner s3Presigner, S3Properties s3Properties,
          PromoBatchS3UploaderService promoBatchS3UploaderService,
          @Qualifier("promoCopyExecutor") Executor promoCopyExecutor,
          @Qualifier("s3Executor") Executor s3Executor,
          BatchEligibilityRepository batchEligibilityRepository,
          BatchEligibilityMapper batchEligibilityMapper,
          UnleashWrapper<FeatureFlag> unleashWrapper) {
    return new PromoBatchRepositoryOutPortImpl(
        promoBatchRepository, promoCodeRepository, promoBatchEntityMapper,
        promoBatchSummaryMapper, promoBatchStatusService,
        promoBatchCodeGeneratorOutPort, s3Presigner,
        s3Properties, ohipAdapterClient,
        promoBatchS3UploaderService, promoCopyExecutor, s3Executor,
        batchEligibilityRepository, batchEligibilityMapper,
        unleashWrapper);
  }

  @Bean
  public PromoBatchInPort promoBatchInPort(
      PromoBatchRepositoryOutPort promoBatchRepositoryOutPort) {
    return new PromoBatchInPortImpl(promoBatchRepositoryOutPort);
  }
}
