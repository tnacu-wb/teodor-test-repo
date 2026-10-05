package uk.co.whitbread.shared.auth.config;

import org.junit.Test;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ResourceLoader;
import org.springframework.web.context.support.StandardServletEnvironment;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class EncryptionServiceConditionTest {
    private final EncryptionServiceCondition serviceCondition = new EncryptionServiceCondition();
    private final ConditionContext cc = new ConditionContextImpl();

    @Test
    public void givenNotEmpty_returnTrue() {
        //given
        System.setProperty("auth.encryption.key", "encryption key");
        //when/then
        assertTrue(serviceCondition.matches(cc, null));
    }

    @Test
    public void givenEmpty_returnFalse() {

        System.setProperty("auth.encryption.key", "");
        //when/then
        assertFalse(serviceCondition.matches(cc, null));
    }

    @Test
    public void givenNull_returnFalse() {
        //given
        System.clearProperty("auth.encryption.key");
        //when/then
        assertFalse(serviceCondition.matches(cc, null));
    }

    private static class ConditionContextImpl implements ConditionContext {
        @Override
        public BeanDefinitionRegistry getRegistry() {
            return null;
        }

        @Override
        public ConfigurableListableBeanFactory getBeanFactory() {
            return null;
        }

        @Override
        public Environment getEnvironment() {
            return new StandardServletEnvironment();
        }

        @Override
        public ResourceLoader getResourceLoader() {
            return null;
        }

        @Override
        public ClassLoader getClassLoader() {
            return null;
        }
    }
}