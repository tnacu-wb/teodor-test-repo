package uk.co.whitbread.availabilitycacheservice.infrastructure.validation;

import jakarta.validation.Configuration;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorFactory;
import java.util.List;
import org.hibernate.validator.HibernateValidator;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

public class CustomLocalValidatorFactoryBean extends LocalValidatorFactoryBean {

  private final List<ConstraintValidator<?, ?>> customConstraintValidators;

  public CustomLocalValidatorFactoryBean(List<ConstraintValidator<?, ?>> customConstraintValidators) {
    this.customConstraintValidators = customConstraintValidators;
    setProviderClass(HibernateValidator.class);
    afterPropertiesSet();
  }

  @Override
  protected void postProcessConfiguration(Configuration<?> configuration) {
    super.postProcessConfiguration(configuration);
    ConstraintValidatorFactory defaultConstraintValidatorFactory =
        configuration.getDefaultConstraintValidatorFactory();
    configuration.constraintValidatorFactory(
        new ConstraintValidatorFactory() {
          @Override
          public <T extends ConstraintValidator<?, ?>> T getInstance(Class<T> key) {
            for (ConstraintValidator<?, ?> constraintValidator : customConstraintValidators) {
              if (key.equals(constraintValidator.getClass())) { //noinspection unchecked
                return (T) constraintValidator;
              }
            }
            return defaultConstraintValidatorFactory.getInstance(key);
          }

          @Override
          public void releaseInstance(ConstraintValidator<?, ?> instance) {
            defaultConstraintValidatorFactory.releaseInstance(instance);
          }
        }
    );
  }
}
