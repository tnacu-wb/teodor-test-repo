package uk.co.whitbread.rules.agent.infrastructure.repository;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.whitbread.rules.agent.domain.exception.RuleEngineException;
import uk.co.whitbread.rules.agent.infrastructure.repository.mapper.BusinessAllowanceRuleEntityMapper;
import uk.co.whitbread.rules.agent.infrastructure.repository.model.BusinessAllowanceRuleEntity;

import java.util.concurrent.ConcurrentHashMap;

@ExtendWith(MockitoExtension.class)
class BusinessAllowanceCacheOutPortImplTest {

  @Mock
  private ConcurrentHashMap<Integer, BusinessAllowanceRuleEntity> cachedRules;
  @Mock
  private BusinessAllowanceRuleEntityMapper businessAllowanceRuleEntityMapper;
  @InjectMocks
  private BusinessAllowanceCacheOutPortImpl businessAllowanceCacheOutPort;


  @Test
  void findRules_ShouldThrowException(){
    String expectedMessage = "No business allowances found.";

    //Act
    var exception = assertThrows(RuleEngineException.class, () ->
          businessAllowanceCacheOutPort.findRules());

    //Assert
    assertThat(exception, notNullValue());
    assertThat(exception.getMessage(), is(expectedMessage));
  }

}
