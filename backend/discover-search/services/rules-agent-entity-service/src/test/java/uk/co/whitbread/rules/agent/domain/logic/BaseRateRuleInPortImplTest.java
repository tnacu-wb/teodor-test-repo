package uk.co.whitbread.rules.agent.domain.logic;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.whitbread.rules.agent.domain.exception.RuleEngineException;
import uk.co.whitbread.rules.agent.domain.ports.secondary.BaseRateRuleRepositoryOutPort;

@ExtendWith(MockitoExtension.class)
public class BaseRateRuleInPortImplTest {
  
  @InjectMocks
  private BaseRateRuleInPortImpl baseRateRuleInPort;
  @Mock
  private BaseRateRuleRepositoryOutPort baseRateRuleRepositoryOutPort;
  
  @Test
  void getBaseRateRule__shouldThrowExceptionIfRuleNotFound() {
    //Arrange
    when(baseRateRuleRepositoryOutPort.getBaseRate(anyString())).thenReturn(Optional.empty());
    
    //Act
    assertThrows(RuleEngineException.class,
        () -> baseRateRuleInPort.getBaseRate("BUSIFLEX"));
    
    //Assert
    verifyNoMoreInteractions(baseRateRuleRepositoryOutPort);
  }
}
