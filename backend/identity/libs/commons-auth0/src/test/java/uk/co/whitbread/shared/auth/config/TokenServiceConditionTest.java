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

public class TokenServiceConditionTest {
    private final TokenServiceCondition serviceCondition = new TokenServiceCondition();
    private final ConditionContext cc = new ConditionContextImpl();

    @Test
    public void givenAllNotEmpty_returnTrue() {
        //given
        System.setProperty("auth.token.providers[0].domain", "https://correct.domain.com");
        System.setProperty("auth.token.providers[0].host", "https://correct.domain.com");
        System.setProperty("auth.token.providers[0].issuer", "https://correct.domain.com/");
        //when/then
        assertTrue(serviceCondition.matches(cc, null));
    }

    @Test
    public void givenAllEmpty_returnFalse() {
        //given
        System.setProperty("auth.token.providers[0].domain", "");
        System.setProperty("auth.token.providers[0].host", "");
        System.setProperty("auth.token.providers[0].issuer", "");
        //when/then
        assertFalse(serviceCondition.matches(cc, null));
    }

    @Test
    public void givenAllNull_returnFalse() {
        //given
        System.clearProperty("auth.token.providers[0].domain");
        System.clearProperty("auth.token.providers[0].host");
        System.clearProperty("auth.token.providers[0].issuer");
        //when/then
        assertFalse(serviceCondition.matches(cc, null));
    }

    @Test
    public void givenOneEmpty_returnFalse() {
        //given
        System.setProperty("auth.token.providers[0].domain", "https://correct.domain.com");
        System.setProperty("auth.token.providers[0].host", "https://correct.domain.com");
        System.setProperty("auth.token.providers[0].issuer", "");
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