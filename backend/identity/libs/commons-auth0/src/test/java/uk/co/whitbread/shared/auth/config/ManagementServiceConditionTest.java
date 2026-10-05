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

public class ManagementServiceConditionTest {
    private final ManagementServiceCondition serviceCondition = new ManagementServiceCondition();
    private final ConditionContext cc = new ConditionContextImpl();

    @Test
    public void givenAllNotEmpty_returnTrue() {
        //given
        System.setProperty("auth.management.domain", "some.valid.domain");
        System.setProperty("auth.management.clientId", "clientIdTest");
        System.setProperty("auth.management.clientSecret", "clientSecretTest");
        System.setProperty("auth.management.audience", "audience");
        //when/then
        assertTrue(serviceCondition.matches(cc, null));
    }

    @Test
    public void givenAllEmpty_returnFalse() {
        //given
        System.setProperty("auth.management.domain", "");
        System.setProperty("auth.management.clientId", "");
        System.setProperty("auth.management.clientSecret", "");
        System.setProperty("auth.management.audience", "");
        //when/then
        assertFalse(serviceCondition.matches(cc, null));
    }

    @Test
    public void givenAllNull_returnFalse() {
        //given
        System.clearProperty("auth.management.domain");
        System.clearProperty("auth.management.clientId");
        System.clearProperty("auth.management.clientSecret");
        System.clearProperty("auth.management.audience");
        //when/then
        assertFalse(serviceCondition.matches(cc, null));
    }

    @Test
    public void givenOneEmpty_returnFalse() {
        //given
        System.setProperty("auth.management.domain", "some.valid.domain");
        System.setProperty("auth.management.clientId", "clientId");
        System.setProperty("auth.management.clientSecret", "clientSecret");
        System.setProperty("auth.management.audience", "");
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